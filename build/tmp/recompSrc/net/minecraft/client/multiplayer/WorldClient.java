package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFireworkStarterFX;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

@SideOnly(Side.CLIENT)
public class WorldClient extends World
{
    /** The packets that need to be sent to the server. */
    private NetHandlerPlayClient sendQueue;
    /** The ChunkProviderClient instance */
    private ChunkProviderClient clientChunkProvider;
    /**
     * The hash set of entities handled by this client. Uses the entity's ID as the hash set's key.
     */
    private IntHashMap entityHashSet = new IntHashMap();
    /** Contains all entities for this client, both spawned and non-spawned. */
    private Set entityList = new HashSet();
    /**
     * Contains all entities for this client that were not spawned due to a non-present chunk. The game will attempt to
     * spawn up to 10 pending entities with each subsequent tick until the spawn queue is empty.
     */
    private Set entitySpawnQueue = new HashSet();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Set previousActiveChunkSet = new HashSet();
    private static final String __OBFID = "CL_00000882";

    public WorldClient(NetHandlerPlayClient p_i45063_1_, WorldSettings p_i45063_2_, int p_i45063_3_, EnumDifficulty p_i45063_4_, Profiler p_i45063_5_)
    {
        super(new SaveHandlerMP(), "MpServer", WorldProvider.getProviderForDimension(p_i45063_3_), p_i45063_2_, p_i45063_5_);
        this.sendQueue = p_i45063_1_;
        this.difficultySetting = p_i45063_4_;
        this.mapStorage = p_i45063_1_.mapStorageOrigin;
        this.isRemote = true;
        this.finishSetup();
        this.setSpawnLocation(8, 64, 8);
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(this));
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        super.tick();
        this.func_82738_a(this.getTotalWorldTime() + 1L);

        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
        {
            this.setWorldTime(this.getWorldTime() + 1L);
        }

        this.theProfiler.startSection("reEntryProcessing");

        for (int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); ++i)
        {
            Entity entity = (Entity)this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(entity);

            if (!this.loadedEntityList.contains(entity))
            {
                this.spawnEntityInWorld(entity);
            }
        }

        this.theProfiler.endStartSection("connection");
        this.sendQueue.onNetworkTick();
        this.theProfiler.endStartSection("chunkCache");
        this.clientChunkProvider.unloadQueuedChunks();
        this.theProfiler.endStartSection("blocks");
        this.func_147456_g();
        this.theProfiler.endSection();
    }

    /**
     * Invalidates an AABB region of blocks from the receive queue, in the event that the block has been modified
     * client-side in the intervening 80 receive ticks.
     */
    public void invalidateBlockReceiveRegion(int par1, int par2, int par3, int par4, int par5, int par6) {}

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
    }

    protected void func_147456_g()
    {
        super.func_147456_g();
        this.previousActiveChunkSet.retainAll(this.activeChunkSet);

        if (this.previousActiveChunkSet.size() == this.activeChunkSet.size())
        {
            this.previousActiveChunkSet.clear();
        }

        int i = 0;
        Iterator iterator = this.activeChunkSet.iterator();

        while (iterator.hasNext())
        {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair)iterator.next();

            if (!this.previousActiveChunkSet.contains(chunkcoordintpair))
            {
                int j = chunkcoordintpair.chunkXPos * 16;
                int k = chunkcoordintpair.chunkZPos * 16;
                this.theProfiler.startSection("getChunk");
                Chunk chunk = this.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
                this.func_147467_a(j, k, chunk);
                this.theProfiler.endSection();
                this.previousActiveChunkSet.add(chunkcoordintpair);
                ++i;

                if (i >= 10)
                {
                    return;
                }
            }
        }
    }

    public void doPreChunk(int par1, int par2, boolean par3)
    {
        if (par3)
        {
            this.clientChunkProvider.loadChunk(par1, par2);
        }
        else
        {
            this.clientChunkProvider.unloadChunk(par1, par2);
        }

        if (!par3)
        {
            this.markBlockRangeForRenderUpdate(par1 * 16, 0, par2 * 16, par1 * 16 + 15, 256, par2 * 16 + 15);
        }
    }

    /**
     * Called to place all entities as part of a world
     */
    public boolean spawnEntityInWorld(Entity par1Entity)
    {
        boolean flag = super.spawnEntityInWorld(par1Entity);
        this.entityList.add(par1Entity);

        if (!flag)
        {
            this.entitySpawnQueue.add(par1Entity);
        }
        else if (par1Entity instanceof EntityMinecart)
        {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)par1Entity));
        }

        return flag;
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(Entity par1Entity)
    {
        super.removeEntity(par1Entity);
        this.entityList.remove(par1Entity);
    }

    public void onEntityAdded(Entity par1Entity)
    {
        super.onEntityAdded(par1Entity);

        if (this.entitySpawnQueue.contains(par1Entity))
        {
            this.entitySpawnQueue.remove(par1Entity);
        }
    }

    public void onEntityRemoved(Entity par1Entity)
    {
        super.onEntityRemoved(par1Entity);
        boolean flag = false;

        if (this.entityList.contains(par1Entity))
        {
            if (par1Entity.isEntityAlive())
            {
                this.entitySpawnQueue.add(par1Entity);
                flag = true;
            }
            else
            {
                this.entityList.remove(par1Entity);
            }
        }

        if (RenderManager.instance.getEntityRenderObject(par1Entity).isStaticEntity() && !flag)
        {
            this.mc.renderGlobal.onStaticEntitiesChanged();
        }
    }

    /**
     * Add an ID to Entity mapping to entityHashSet
     */
    public void addEntityToWorld(int par1, Entity par2Entity)
    {
        Entity entity1 = this.getEntityByID(par1);

        if (entity1 != null)
        {
            this.removeEntity(entity1);
        }

        this.entityList.add(par2Entity);
        par2Entity.setEntityId(par1);

        if (!this.spawnEntityInWorld(par2Entity))
        {
            this.entitySpawnQueue.add(par2Entity);
        }

        this.entityHashSet.addKey(par1, par2Entity);

        if (RenderManager.instance.getEntityRenderObject(par2Entity).isStaticEntity())
        {
            this.mc.renderGlobal.onStaticEntitiesChanged();
        }
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(int par1)
    {
        return (Entity)(par1 == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : (Entity)this.entityHashSet.lookup(par1));
    }

    public Entity removeEntityFromWorld(int par1)
    {
        Entity entity = (Entity)this.entityHashSet.removeObject(par1);

        if (entity != null)
        {
            this.entityList.remove(entity);
            this.removeEntity(entity);
        }

        return entity;
    }

    public boolean func_147492_c(int p_147492_1_, int p_147492_2_, int p_147492_3_, Block p_147492_4_, int p_147492_5_)
    {
        this.invalidateBlockReceiveRegion(p_147492_1_, p_147492_2_, p_147492_3_, p_147492_1_, p_147492_2_, p_147492_3_);
        return super.setBlock(p_147492_1_, p_147492_2_, p_147492_3_, p_147492_4_, p_147492_5_, 3);
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket()
    {
        this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
        super.updateWeather();
    }

    @Override
    public void updateWeatherBody()
    {
        if (!this.provider.hasNoSky)
        {
            ;
        }
    }

    public void doVoidFogParticles(int par1, int par2, int par3)
    {
        byte b0 = 16;
        Random random = new Random();

        for (int l = 0; l < 1000; ++l)
        {
            int i1 = par1 + this.rand.nextInt(b0) - this.rand.nextInt(b0);
            int j1 = par2 + this.rand.nextInt(b0) - this.rand.nextInt(b0);
            int k1 = par3 + this.rand.nextInt(b0) - this.rand.nextInt(b0);
            Block block = this.getBlock(i1, j1, k1);

            if (block.getMaterial() == Material.air)
            {
                if (this.rand.nextInt(8) > j1 && this.provider.getWorldHasVoidParticles())
                {
                    this.spawnParticle("depthsuspend", (double)((float)i1 + this.rand.nextFloat()), (double)((float)j1 + this.rand.nextFloat()), (double)((float)k1 + this.rand.nextFloat()), 0.0D, 0.0D, 0.0D);
                }
            }
            else
            {
                block.randomDisplayTick(this, i1, j1, k1, random);
            }
        }
    }

    /**
     * also releases skins.
     */
    public void removeAllEntities()
    {
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int i;
        Entity entity;
        int j;
        int k;

        for (i = 0; i < this.unloadedEntityList.size(); ++i)
        {
            entity = (Entity)this.unloadedEntityList.get(i);
            j = entity.chunkCoordX;
            k = entity.chunkCoordZ;

            if (entity.addedToChunk && this.chunkExists(j, k))
            {
                this.getChunkFromChunkCoords(j, k).removeEntity(entity);
            }
        }

        for (i = 0; i < this.unloadedEntityList.size(); ++i)
        {
            this.onEntityRemoved((Entity)this.unloadedEntityList.get(i));
        }

        this.unloadedEntityList.clear();

        for (i = 0; i < this.loadedEntityList.size(); ++i)
        {
            entity = (Entity)this.loadedEntityList.get(i);

            if (entity.ridingEntity != null)
            {
                if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity)
                {
                    continue;
                }

                entity.ridingEntity.riddenByEntity = null;
                entity.ridingEntity = null;
            }

            if (entity.isDead)
            {
                j = entity.chunkCoordX;
                k = entity.chunkCoordZ;

                if (entity.addedToChunk && this.chunkExists(j, k))
                {
                    this.getChunkFromChunkCoords(j, k).removeEntity(entity);
                }

                this.loadedEntityList.remove(i--);
                this.onEntityRemoved(entity);
            }
        }
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport par1CrashReport)
    {
        CrashReportCategory crashreportcategory = super.addWorldInfoToCrashReport(par1CrashReport);
        crashreportcategory.addCrashSectionCallable("Forced entities", new Callable()
        {
            private static final String __OBFID = "CL_00000883";
            public String call()
            {
                return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList.toString();
            }
        });
        crashreportcategory.addCrashSectionCallable("Retry entities", new Callable()
        {
            private static final String __OBFID = "CL_00000884";
            public String call()
            {
                return WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue.toString();
            }
        });
        crashreportcategory.addCrashSectionCallable("Server brand", new Callable()
        {
            private static final String __OBFID = "CL_00000885";
            public String call()
            {
                return WorldClient.this.mc.thePlayer.func_142021_k();
            }
        });
        crashreportcategory.addCrashSectionCallable("Server type", new Callable()
        {
            private static final String __OBFID = "CL_00000886";
            public String call()
            {
                return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
            }
        });
        return crashreportcategory;
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(double par1, double par3, double par5, String par7Str, float par8, float par9, boolean par10)
    {
        double d3 = this.mc.renderViewEntity.getDistanceSq(par1, par3, par5);
        PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(new ResourceLocation(par7Str), par8, par9, (float)par1, (float)par3, (float)par5);

        if (par10 && d3 > 100.0D)
        {
            double d4 = Math.sqrt(d3) / 40.0D;
            this.mc.getSoundHandler().playDelayedSound(positionedsoundrecord, (int)(d4 * 20.0D));
        }
        else
        {
            this.mc.getSoundHandler().playSound(positionedsoundrecord);
        }
    }

    public void makeFireworks(double par1, double par3, double par5, double par7, double par9, double par11, NBTTagCompound par13NBTTagCompound)
    {
        this.mc.effectRenderer.addEffect(new EntityFireworkStarterFX(this, par1, par3, par5, par7, par9, par11, this.mc.effectRenderer, par13NBTTagCompound));
    }

    public void setWorldScoreboard(Scoreboard par1Scoreboard)
    {
        this.worldScoreboard = par1Scoreboard;
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(long par1)
    {
        if (par1 < 0L)
        {
            par1 = -par1;
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
        }
        else
        {
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
        }

        super.setWorldTime(par1);
    }
}