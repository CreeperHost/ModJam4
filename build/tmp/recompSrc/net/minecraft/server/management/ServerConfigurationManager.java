package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager
{
    private static final Logger logger = LogManager.getLogger();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");
    /** Reference to the MinecraftServer object. */
    private final MinecraftServer mcServer;
    /** A list of player entities that exist on this server. */
    public final List playerEntityList = new ArrayList();
    private final BanList bannedPlayers = new BanList(new File("banned-players.txt"));
    private final BanList bannedIPs = new BanList(new File("banned-ips.txt"));
    /** A set containing the OPs. */
    private final Set ops = new HashSet();
    /** The Set of all whitelisted players. */
    private final Set whiteListedPlayers = new HashSet();
    private final Map field_148547_k = Maps.newHashMap();
    /** Reference to the PlayerNBTManager object. */
    private IPlayerFileData playerNBTManagerObj;
    /**
     * Server setting to only allow OPs and whitelisted players to join the server.
     */
    private boolean whiteListEnforced;
    /** The maximum number of players that can be connected at a time. */
    protected int maxPlayers;
    protected int viewDistance;
    private WorldSettings.GameType gameType;
    /** True if all players are allowed to use commands (cheats). */
    private boolean commandsAllowedForAll;
    /**
     * index into playerEntities of player to ping, updated every tick; currently hardcoded to max at 200 players
     */
    private int playerPingIndex;
    private static final String __OBFID = "CL_00001423";

    public ServerConfigurationManager(MinecraftServer par1MinecraftServer)
    {
        this.mcServer = par1MinecraftServer;
        this.bannedPlayers.setListActive(false);
        this.bannedIPs.setListActive(false);
        this.maxPlayers = 8;
    }

    public void initializeConnectionToPlayer(NetworkManager par1INetworkManager, EntityPlayerMP par2EntityPlayerMP, NetHandlerPlayServer nethandlerplayserver)
    {
        NBTTagCompound nbttagcompound = this.readPlayerDataFromFile(par2EntityPlayerMP);
        par2EntityPlayerMP.setWorld(this.mcServer.worldServerForDimension(par2EntityPlayerMP.dimension));
        par2EntityPlayerMP.theItemInWorldManager.setWorld((WorldServer)par2EntityPlayerMP.worldObj);
        String s = "local";

        if (par1INetworkManager.getSocketAddress() != null)
        {
            s = par1INetworkManager.getSocketAddress().toString();
        }

        logger.info(par2EntityPlayerMP.getCommandSenderName() + "[" + s + "] logged in with entity id " + par2EntityPlayerMP.getEntityId() + " at (" + par2EntityPlayerMP.posX + ", " + par2EntityPlayerMP.posY + ", " + par2EntityPlayerMP.posZ + ")");
        WorldServer worldserver = this.mcServer.worldServerForDimension(par2EntityPlayerMP.dimension);
        ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
        this.func_72381_a(par2EntityPlayerMP, (EntityPlayerMP)null, worldserver);
        par2EntityPlayerMP.playerNetServerHandler = nethandlerplayserver;
        nethandlerplayserver.sendPacket(new S01PacketJoinGame(par2EntityPlayerMP.getEntityId(), par2EntityPlayerMP.theItemInWorldManager.getGameType(), worldserver.getWorldInfo().isHardcoreModeEnabled(), worldserver.provider.dimensionId, worldserver.difficultySetting, this.getMaxPlayers(), worldserver.getWorldInfo().getTerrainType()));
        nethandlerplayserver.sendPacket(new S3FPacketCustomPayload("MC|Brand", this.getServerInstance().getServerModName().getBytes(Charsets.UTF_8)));
        nethandlerplayserver.sendPacket(new S05PacketSpawnPosition(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ));
        nethandlerplayserver.sendPacket(new S39PacketPlayerAbilities(par2EntityPlayerMP.capabilities));
        nethandlerplayserver.sendPacket(new S09PacketHeldItemChange(par2EntityPlayerMP.inventory.currentItem));
        par2EntityPlayerMP.func_147099_x().func_150877_d();
        par2EntityPlayerMP.func_147099_x().func_150884_b(par2EntityPlayerMP);
        this.func_96456_a((ServerScoreboard)worldserver.getScoreboard(), par2EntityPlayerMP);
        this.mcServer.func_147132_au();
        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.joined", new Object[] {par2EntityPlayerMP.func_145748_c_()});
        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        this.sendChatMsg(chatcomponenttranslation);
        this.playerLoggedIn(par2EntityPlayerMP);
        nethandlerplayserver.setPlayerLocation(par2EntityPlayerMP.posX, par2EntityPlayerMP.posY, par2EntityPlayerMP.posZ, par2EntityPlayerMP.rotationYaw, par2EntityPlayerMP.rotationPitch);
        this.updateTimeAndWeatherForPlayer(par2EntityPlayerMP, worldserver);

        if (this.mcServer.getTexturePack().length() > 0)
        {
            par2EntityPlayerMP.requestTexturePackLoad(this.mcServer.getTexturePack());
        }

        Iterator iterator = par2EntityPlayerMP.getActivePotionEffects().iterator();

        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect)iterator.next();
            nethandlerplayserver.sendPacket(new S1DPacketEntityEffect(par2EntityPlayerMP.getEntityId(), potioneffect));
        }

        par2EntityPlayerMP.addSelfToInternalCraftingInventory();

        FMLCommonHandler.instance().firePlayerLoggedIn(par2EntityPlayerMP);
        if (nbttagcompound != null && nbttagcompound.hasKey("Riding", 10))
        {
            Entity entity = EntityList.createEntityFromNBT(nbttagcompound.getCompoundTag("Riding"), worldserver);

            if (entity != null)
            {
                entity.forceSpawn = true;
                worldserver.spawnEntityInWorld(entity);
                par2EntityPlayerMP.mountEntity(entity);
                entity.forceSpawn = false;
            }
        }
    }

    protected void func_96456_a(ServerScoreboard par1ServerScoreboard, EntityPlayerMP par2EntityPlayerMP)
    {
        HashSet hashset = new HashSet();
        Iterator iterator = par1ServerScoreboard.getTeams().iterator();

        while (iterator.hasNext())
        {
            ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)iterator.next();
            par2EntityPlayerMP.playerNetServerHandler.sendPacket(new S3EPacketTeams(scoreplayerteam, 0));
        }

        for (int i = 0; i < 3; ++i)
        {
            ScoreObjective scoreobjective = par1ServerScoreboard.func_96539_a(i);

            if (scoreobjective != null && !hashset.contains(scoreobjective))
            {
                List list = par1ServerScoreboard.func_96550_d(scoreobjective);
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext())
                {
                    Packet packet = (Packet)iterator1.next();
                    par2EntityPlayerMP.playerNetServerHandler.sendPacket(packet);
                }

                hashset.add(scoreobjective);
            }
        }
    }

    /**
     * Sets the NBT manager to the one for the WorldServer given.
     */
    public void setPlayerManager(WorldServer[] par1ArrayOfWorldServer)
    {
        this.playerNBTManagerObj = par1ArrayOfWorldServer[0].getSaveHandler().getSaveHandler();
    }

    public void func_72375_a(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        WorldServer worldserver1 = par1EntityPlayerMP.getServerForPlayer();

        if (par2WorldServer != null)
        {
            par2WorldServer.getPlayerManager().removePlayer(par1EntityPlayerMP);
        }

        worldserver1.getPlayerManager().addPlayer(par1EntityPlayerMP);
        worldserver1.theChunkProviderServer.loadChunk((int)par1EntityPlayerMP.posX >> 4, (int)par1EntityPlayerMP.posZ >> 4);
    }

    public int getEntityViewDistance()
    {
        return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
    }

    /**
     * called during player login. reads the player information from disk.
     */
    public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP par1EntityPlayerMP)
    {
        NBTTagCompound nbttagcompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
        NBTTagCompound nbttagcompound1;

        if (par1EntityPlayerMP.getCommandSenderName().equals(this.mcServer.getServerOwner()) && nbttagcompound != null)
        {
            par1EntityPlayerMP.readFromNBT(nbttagcompound);
            nbttagcompound1 = nbttagcompound;
            logger.debug("loading single player");
        }
        else
        {
            nbttagcompound1 = this.playerNBTManagerObj.readPlayerData(par1EntityPlayerMP);
        }

        return nbttagcompound1;
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP par1EntityPlayerMP)
    {
        if (par1EntityPlayerMP.playerNetServerHandler == null) return;

        this.playerNBTManagerObj.writePlayerData(par1EntityPlayerMP);
        StatisticsFile statisticsfile = (StatisticsFile)this.field_148547_k.get(par1EntityPlayerMP.getCommandSenderName());

        if (statisticsfile != null)
        {
            statisticsfile.func_150883_b();
        }
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    public void playerLoggedIn(EntityPlayerMP par1EntityPlayerMP)
    {
        this.sendPacketToAllPlayers(new S38PacketPlayerListItem(par1EntityPlayerMP.getCommandSenderName(), true, 1000));
        this.playerEntityList.add(par1EntityPlayerMP);
        WorldServer worldserver = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        worldserver.spawnEntityInWorld(par1EntityPlayerMP);
        this.func_72375_a(par1EntityPlayerMP, (WorldServer)null);

        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            EntityPlayerMP entityplayermp1 = (EntityPlayerMP)this.playerEntityList.get(i);
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(entityplayermp1.getCommandSenderName(), true, entityplayermp1.ping));
        }
    }

    /**
     * Update which chunks the player needs info on.
     */
    public void updatePlayerPertinentChunks(EntityPlayerMP par1EntityPlayerMP)
    {
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().updatePlayerPertinentChunks(par1EntityPlayerMP);
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    public void playerLoggedOut(EntityPlayerMP par1EntityPlayerMP)
    {
        FMLCommonHandler.instance().firePlayerLoggedOut(par1EntityPlayerMP);
        par1EntityPlayerMP.triggerAchievement(StatList.leaveGameStat);
        this.writePlayerData(par1EntityPlayerMP);
        WorldServer worldserver = par1EntityPlayerMP.getServerForPlayer();

        if (par1EntityPlayerMP.ridingEntity != null)
        {
            worldserver.removePlayerEntityDangerously(par1EntityPlayerMP.ridingEntity);
            logger.debug("removing player mount");
        }

        worldserver.removeEntity(par1EntityPlayerMP);
        worldserver.getPlayerManager().removePlayer(par1EntityPlayerMP);
        this.playerEntityList.remove(par1EntityPlayerMP);
        this.field_148547_k.remove(par1EntityPlayerMP.getCommandSenderName());
        this.sendPacketToAllPlayers(new S38PacketPlayerListItem(par1EntityPlayerMP.getCommandSenderName(), false, 9999));
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String allowUserToConnect(SocketAddress p_148542_1_, GameProfile p_148542_2_)
    {
        if (this.bannedPlayers.isBanned(p_148542_2_.getName()))
        {
            BanEntry banentry1 = (BanEntry)this.bannedPlayers.getBannedList().get(p_148542_2_.getName());
            String s2 = "You are banned from this server!\nReason: " + banentry1.getBanReason();

            if (banentry1.getBanEndDate() != null)
            {
                s2 = s2 + "\nYour ban will be removed on " + dateFormat.format(banentry1.getBanEndDate());
            }

            return s2;
        }
        else if (!this.isAllowedToLogin(p_148542_2_.getName()))
        {
            return "You are not white-listed on this server!";
        }
        else
        {
            String s = p_148542_1_.toString();
            s = s.substring(s.indexOf("/") + 1);
            s = s.substring(0, s.indexOf(":"));

            if (this.bannedIPs.isBanned(s))
            {
                BanEntry banentry = (BanEntry)this.bannedIPs.getBannedList().get(s);
                String s1 = "Your IP address is banned from this server!\nReason: " + banentry.getBanReason();

                if (banentry.getBanEndDate() != null)
                {
                    s1 = s1 + "\nYour ban will be removed on " + dateFormat.format(banentry.getBanEndDate());
                }

                return s1;
            }
            else
            {
                return this.playerEntityList.size() >= this.maxPlayers ? "The server is full!" : null;
            }
        }
    }

    /**
     * also checks for multiple logins across servers
     */
    public EntityPlayerMP createPlayerForUser(GameProfile p_148545_1_)
    {
        ArrayList arraylist = new ArrayList();
        EntityPlayerMP entityplayermp;

        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            entityplayermp = (EntityPlayerMP)this.playerEntityList.get(i);

            if (entityplayermp.getCommandSenderName().equalsIgnoreCase(p_148545_1_.getName()))
            {
                arraylist.add(entityplayermp);
            }
        }

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext())
        {
            entityplayermp = (EntityPlayerMP)iterator.next();
            entityplayermp.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
        }

        Object object;

        if (this.mcServer.isDemo())
        {
            object = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
        }
        else
        {
            object = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
        }

        return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), p_148545_1_, (ItemInWorldManager)object);
    }

    /**
     * creates and returns a respawned player based on the provided PlayerEntity. Args are the PlayerEntityMP to
     * respawn, an INT for the dimension to respawn into (usually 0), and a boolean value that is true if the player
     * beat the game rather than dying
     */
    public EntityPlayerMP respawnPlayer(EntityPlayerMP par1EntityPlayerMP, int par2, boolean par3)
    {
        World world = mcServer.worldServerForDimension(par2);
        if (world == null)
        {
            par2 = 0;
        }
        else if (!world.provider.canRespawnHere())
        {
            par2 = world.provider.getRespawnDimension(par1EntityPlayerMP);
        }

        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getEntityTracker().removeEntityFromAllTrackingPlayers(par1EntityPlayerMP);
        par1EntityPlayerMP.getServerForPlayer().getPlayerManager().removePlayer(par1EntityPlayerMP);
        this.playerEntityList.remove(par1EntityPlayerMP);
        this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension).removePlayerEntityDangerously(par1EntityPlayerMP);
        ChunkCoordinates chunkcoordinates = par1EntityPlayerMP.getBedLocation(par2);
        boolean flag1 = par1EntityPlayerMP.isSpawnForced(par2);
        par1EntityPlayerMP.dimension = par2;
        Object object;

        if (this.mcServer.isDemo())
        {
            object = new DemoWorldManager(this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension));
        }
        else
        {
            object = new ItemInWorldManager(this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension));
        }

        EntityPlayerMP entityplayermp1 = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), par1EntityPlayerMP.getGameProfile(), (ItemInWorldManager)object);
        entityplayermp1.playerNetServerHandler = par1EntityPlayerMP.playerNetServerHandler;
        entityplayermp1.clonePlayer(par1EntityPlayerMP, par3);
        entityplayermp1.dimension = par2;
        entityplayermp1.setEntityId(par1EntityPlayerMP.getEntityId());
        WorldServer worldserver = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        this.func_72381_a(entityplayermp1, par1EntityPlayerMP, worldserver);
        ChunkCoordinates chunkcoordinates1;

        if (chunkcoordinates != null)
        {
            chunkcoordinates1 = EntityPlayer.verifyRespawnCoordinates(this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension), chunkcoordinates, flag1);

            if (chunkcoordinates1 != null)
            {
                entityplayermp1.setLocationAndAngles((double)((float)chunkcoordinates1.posX + 0.5F), (double)((float)chunkcoordinates1.posY + 0.1F), (double)((float)chunkcoordinates1.posZ + 0.5F), 0.0F, 0.0F);
                entityplayermp1.setSpawnChunk(chunkcoordinates, flag1);
            }
            else
            {
                entityplayermp1.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
            }
        }

        worldserver.theChunkProviderServer.loadChunk((int)entityplayermp1.posX >> 4, (int)entityplayermp1.posZ >> 4);

        while (!worldserver.getCollidingBoundingBoxes(entityplayermp1, entityplayermp1.boundingBox).isEmpty())
        {
            entityplayermp1.setPosition(entityplayermp1.posX, entityplayermp1.posY + 1.0D, entityplayermp1.posZ);
        }

        entityplayermp1.playerNetServerHandler.sendPacket(new S07PacketRespawn(entityplayermp1.dimension, entityplayermp1.worldObj.difficultySetting, entityplayermp1.worldObj.getWorldInfo().getTerrainType(), entityplayermp1.theItemInWorldManager.getGameType()));
        chunkcoordinates1 = worldserver.getSpawnPoint();
        entityplayermp1.playerNetServerHandler.setPlayerLocation(entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch);
        entityplayermp1.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(chunkcoordinates1.posX, chunkcoordinates1.posY, chunkcoordinates1.posZ));
        entityplayermp1.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityplayermp1.experience, entityplayermp1.experienceTotal, entityplayermp1.experienceLevel));
        this.updateTimeAndWeatherForPlayer(entityplayermp1, worldserver);
        worldserver.getPlayerManager().addPlayer(entityplayermp1);
        worldserver.spawnEntityInWorld(entityplayermp1);
        this.playerEntityList.add(entityplayermp1);
        entityplayermp1.addSelfToInternalCraftingInventory();
        entityplayermp1.setHealth(entityplayermp1.getHealth());
        FMLCommonHandler.instance().firePlayerRespawnEvent(entityplayermp1);
        return entityplayermp1;
    }

    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2)
    {
        transferPlayerToDimension(par1EntityPlayerMP, par2, mcServer.worldServerForDimension(par2).getDefaultTeleporter());
    }

    public void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2, Teleporter teleporter)
    {
        int j = par1EntityPlayerMP.dimension;
        WorldServer worldserver = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.dimension = par2;
        WorldServer worldserver1 = this.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S07PacketRespawn(par1EntityPlayerMP.dimension, par1EntityPlayerMP.worldObj.difficultySetting, par1EntityPlayerMP.worldObj.getWorldInfo().getTerrainType(), par1EntityPlayerMP.theItemInWorldManager.getGameType()));
        worldserver.removePlayerEntityDangerously(par1EntityPlayerMP);
        par1EntityPlayerMP.isDead = false;
        this.transferEntityToWorld(par1EntityPlayerMP, j, worldserver, worldserver1, teleporter);
        this.func_72375_a(par1EntityPlayerMP, worldserver);
        par1EntityPlayerMP.playerNetServerHandler.setPlayerLocation(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
        par1EntityPlayerMP.theItemInWorldManager.setWorld(worldserver1);
        this.updateTimeAndWeatherForPlayer(par1EntityPlayerMP, worldserver1);
        this.syncPlayerInventory(par1EntityPlayerMP);
        Iterator iterator = par1EntityPlayerMP.getActivePotionEffects().iterator();

        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect)iterator.next();
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(par1EntityPlayerMP.getEntityId(), potioneffect));
        }
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(par1EntityPlayerMP, j, par2);
    }

    /**
     * Transfers an entity from a world to another world.
     */
    public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer)
    {
        transferEntityToWorld(par1Entity, par2, par3WorldServer, par4WorldServer, par4WorldServer.getDefaultTeleporter());
    }

    public void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer, Teleporter teleporter)
    {
        WorldProvider pOld = par3WorldServer.provider;
        WorldProvider pNew = par4WorldServer.provider;
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double d0 = par1Entity.posX * moveFactor;
        double d1 = par1Entity.posZ * moveFactor;
        double d3 = par1Entity.posX;
        double d4 = par1Entity.posY;
        double d5 = par1Entity.posZ;
        float f = par1Entity.rotationYaw;
        par3WorldServer.theProfiler.startSection("moving");

        /*
        if (par1Entity.dimension == -1)
        {
            d0 /= d2;
            d1 /= d2;
            par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, par1Entity.rotationYaw, par1Entity.rotationPitch);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }
        else if (par1Entity.dimension == 0)
        {
            d0 *= d2;
            d1 *= d2;
            par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, par1Entity.rotationYaw, par1Entity.rotationPitch);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }
        */
        if (par1Entity.dimension == 1)
        {
            ChunkCoordinates chunkcoordinates;

            if (par2 == 1)
            {
                chunkcoordinates = par4WorldServer.getSpawnPoint();
            }
            else
            {
                chunkcoordinates = par4WorldServer.getEntrancePortalLocation();
            }

            d0 = (double)chunkcoordinates.posX;
            par1Entity.posY = (double)chunkcoordinates.posY;
            d1 = (double)chunkcoordinates.posZ;
            par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, 90.0F, 0.0F);

            if (par1Entity.isEntityAlive())
            {
                par3WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }
        }

        par3WorldServer.theProfiler.endSection();

        if (par2 != 1)
        {
            par3WorldServer.theProfiler.startSection("placing");
            d0 = (double)MathHelper.clamp_int((int)d0, -29999872, 29999872);
            d1 = (double)MathHelper.clamp_int((int)d1, -29999872, 29999872);

            if (par1Entity.isEntityAlive())
            {
                par1Entity.setLocationAndAngles(d0, par1Entity.posY, d1, par1Entity.rotationYaw, par1Entity.rotationPitch);
                teleporter.placeInPortal(par1Entity, d3, d4, d5, f);
                par4WorldServer.spawnEntityInWorld(par1Entity);
                par4WorldServer.updateEntityWithOptionalForce(par1Entity, false);
            }

            par3WorldServer.theProfiler.endSection();
        }

        par1Entity.setWorld(par4WorldServer);
    }

    /**
     * sends 1 player per tick, but only sends a player once every 600 ticks
     */
    public void sendPlayerInfoToAllPlayers()
    {
        if (++this.playerPingIndex > 600)
        {
            this.playerPingIndex = 0;
        }

        if (this.playerPingIndex < this.playerEntityList.size())
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntityList.get(this.playerPingIndex);
            this.sendPacketToAllPlayers(new S38PacketPlayerListItem(entityplayermp.getCommandSenderName(), true, entityplayermp.ping));
        }
    }

    public void sendPacketToAllPlayers(Packet p_148540_1_)
    {
        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            ((EntityPlayerMP)this.playerEntityList.get(i)).playerNetServerHandler.sendPacket(p_148540_1_);
        }
    }

    /**
     * Sends a packet to all players in the specified Dimension
     */
    public void sendPacketToAllPlayersInDimension(Packet p_148537_1_, int p_148537_2_)
    {
        for (int j = 0; j < this.playerEntityList.size(); ++j)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntityList.get(j);

            if (entityplayermp.dimension == p_148537_2_)
            {
                entityplayermp.playerNetServerHandler.sendPacket(p_148537_1_);
            }
        }
    }

    /**
     * returns a string containing a comma-seperated list of player names
     */
    public String getPlayerListAsString()
    {
        String s = "";

        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            if (i > 0)
            {
                s = s + ", ";
            }

            s = s + ((EntityPlayerMP)this.playerEntityList.get(i)).getCommandSenderName();
        }

        return s;
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getAllUsernames()
    {
        String[] astring = new String[this.playerEntityList.size()];

        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            astring[i] = ((EntityPlayerMP)this.playerEntityList.get(i)).getCommandSenderName();
        }

        return astring;
    }

    public BanList getBannedPlayers()
    {
        return this.bannedPlayers;
    }

    public BanList getBannedIPs()
    {
        return this.bannedIPs;
    }

    /**
     * This adds a username to the ops list, then saves the op list
     */
    public void addOp(String par1Str)
    {
        this.ops.add(par1Str.toLowerCase());
    }

    /**
     * This removes a username from the ops list, then saves the op list
     */
    public void removeOp(String par1Str)
    {
        this.ops.remove(par1Str.toLowerCase());
    }

    /**
     * Determine if the player is allowed to connect based on current server settings.
     */
    public boolean isAllowedToLogin(String par1Str)
    {
        par1Str = par1Str.trim().toLowerCase();
        return !this.whiteListEnforced || this.ops.contains(par1Str) || this.whiteListedPlayers.contains(par1Str);
    }

    /**
     * Returns true if the specified player is opped, even if they're currently offline.
     */
    public boolean isPlayerOpped(String par1Str)
    {
        return this.ops.contains(par1Str.trim().toLowerCase()) || this.mcServer.isSinglePlayer() && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed() && this.mcServer.getServerOwner().equalsIgnoreCase(par1Str) || this.commandsAllowedForAll;
    }

    public EntityPlayerMP getPlayerForUsername(String par1Str)
    {
        Iterator iterator = this.playerEntityList.iterator();
        EntityPlayerMP entityplayermp;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            entityplayermp = (EntityPlayerMP)iterator.next();
        }
        while (!entityplayermp.getCommandSenderName().equalsIgnoreCase(par1Str));

        return entityplayermp;
    }

    /**
     * Find all players in a specified range and narrowing down by other parameters
     */
    public List findPlayers(ChunkCoordinates par1ChunkCoordinates, int par2, int par3, int par4, int par5, int par6, int par7, Map par8Map, String par9Str, String par10Str, World par11World)
    {
        if (this.playerEntityList.isEmpty())
        {
            return null;
        }
        else
        {
            Object object = new ArrayList();
            boolean flag = par4 < 0;
            boolean flag1 = par9Str != null && par9Str.startsWith("!");
            boolean flag2 = par10Str != null && par10Str.startsWith("!");
            int k1 = par2 * par2;
            int l1 = par3 * par3;
            par4 = MathHelper.abs_int(par4);

            if (flag1)
            {
                par9Str = par9Str.substring(1);
            }

            if (flag2)
            {
                par10Str = par10Str.substring(1);
            }

            for (int i2 = 0; i2 < this.playerEntityList.size(); ++i2)
            {
                EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntityList.get(i2);

                if ((par11World == null || entityplayermp.worldObj == par11World) && (par9Str == null || flag1 != par9Str.equalsIgnoreCase(entityplayermp.getCommandSenderName())))
                {
                    if (par10Str != null)
                    {
                        Team team = entityplayermp.getTeam();
                        String s2 = team == null ? "" : team.getRegisteredName();

                        if (flag2 == par10Str.equalsIgnoreCase(s2))
                        {
                            continue;
                        }
                    }

                    if (par1ChunkCoordinates != null && (par2 > 0 || par3 > 0))
                    {
                        float f = par1ChunkCoordinates.getDistanceSquaredToChunkCoordinates(entityplayermp.getPlayerCoordinates());

                        if (par2 > 0 && f < (float)k1 || par3 > 0 && f > (float)l1)
                        {
                            continue;
                        }
                    }

                    if (this.func_96457_a(entityplayermp, par8Map) && (par5 == WorldSettings.GameType.NOT_SET.getID() || par5 == entityplayermp.theItemInWorldManager.getGameType().getID()) && (par6 <= 0 || entityplayermp.experienceLevel >= par6) && entityplayermp.experienceLevel <= par7)
                    {
                        ((List)object).add(entityplayermp);
                    }
                }
            }

            if (par1ChunkCoordinates != null)
            {
                Collections.sort((List)object, new PlayerPositionComparator(par1ChunkCoordinates));
            }

            if (flag)
            {
                Collections.reverse((List)object);
            }

            if (par4 > 0)
            {
                object = ((List)object).subList(0, Math.min(par4, ((List)object).size()));
            }

            return (List)object;
        }
    }

    private boolean func_96457_a(EntityPlayer par1EntityPlayer, Map par2Map)
    {
        if (par2Map != null && par2Map.size() != 0)
        {
            Iterator iterator = par2Map.entrySet().iterator();
            Entry entry;
            boolean flag;
            int i;

            do
            {
                if (!iterator.hasNext())
                {
                    return true;
                }

                entry = (Entry)iterator.next();
                String s = (String)entry.getKey();
                flag = false;

                if (s.endsWith("_min") && s.length() > 4)
                {
                    flag = true;
                    s = s.substring(0, s.length() - 4);
                }

                Scoreboard scoreboard = par1EntityPlayer.getWorldScoreboard();
                ScoreObjective scoreobjective = scoreboard.getObjective(s);

                if (scoreobjective == null)
                {
                    return false;
                }

                Score score = par1EntityPlayer.getWorldScoreboard().func_96529_a(par1EntityPlayer.getCommandSenderName(), scoreobjective);
                i = score.getScorePoints();

                if (i < ((Integer)entry.getValue()).intValue() && flag)
                {
                    return false;
                }
            }
            while (i <= ((Integer)entry.getValue()).intValue() || flag);

            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * params: x,y,z,r,dimension. The packet is sent to all players within r radius of x,y,z (r^2>x^2+y^2+z^2)
     */
    public void sendToAllNear(double p_148541_1_, double p_148541_3_, double p_148541_5_, double p_148541_7_, int p_148541_9_, Packet p_148541_10_)
    {
        this.sendToAllNearExcept((EntityPlayer)null, p_148541_1_, p_148541_3_, p_148541_5_, p_148541_7_, p_148541_9_, p_148541_10_);
    }

    /**
     * params: srcPlayer,x,y,z,r,dimension. The packet is not sent to the srcPlayer, but all other players within the
     * search radius
     */
    public void sendToAllNearExcept(EntityPlayer p_148543_1_, double p_148543_2_, double p_148543_4_, double p_148543_6_, double p_148543_8_, int p_148543_10_, Packet p_148543_11_)
    {
        for (int j = 0; j < this.playerEntityList.size(); ++j)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playerEntityList.get(j);

            if (entityplayermp != p_148543_1_ && entityplayermp.dimension == p_148543_10_)
            {
                double d4 = p_148543_2_ - entityplayermp.posX;
                double d5 = p_148543_4_ - entityplayermp.posY;
                double d6 = p_148543_6_ - entityplayermp.posZ;

                if (d4 * d4 + d5 * d5 + d6 * d6 < p_148543_8_ * p_148543_8_)
                {
                    entityplayermp.playerNetServerHandler.sendPacket(p_148543_11_);
                }
            }
        }
    }

    /**
     * Saves all of the players' current states.
     */
    public void saveAllPlayerData()
    {
        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            this.writePlayerData((EntityPlayerMP)this.playerEntityList.get(i));
        }
    }

    /**
     * Add the specified player to the white list.
     */
    public void addToWhiteList(String par1Str)
    {
        this.whiteListedPlayers.add(par1Str);
    }

    /**
     * Remove the specified player from the whitelist.
     */
    public void removeFromWhitelist(String par1Str)
    {
        this.whiteListedPlayers.remove(par1Str);
    }

    /**
     * Returns the whitelisted players.
     */
    public Set getWhiteListedPlayers()
    {
        return this.whiteListedPlayers;
    }

    public Set getOps()
    {
        return this.ops;
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void loadWhiteList() {}

    /**
     * Updates the time and weather for the given player to those of the given world
     */
    public void updateTimeAndWeatherForPlayer(EntityPlayerMP par1EntityPlayerMP, WorldServer par2WorldServer)
    {
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S03PacketTimeUpdate(par2WorldServer.getTotalWorldTime(), par2WorldServer.getWorldTime(), par2WorldServer.getGameRules().getGameRuleBooleanValue("doDaylightCycle")));

        if (par2WorldServer.isRaining())
        {
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, par2WorldServer.getRainStrength(1.0F)));
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, par2WorldServer.getWeightedThunderStrength(1.0F)));
        }
    }

    /**
     * sends the players inventory to himself
     */
    public void syncPlayerInventory(EntityPlayerMP par1EntityPlayerMP)
    {
        par1EntityPlayerMP.sendContainerToPlayer(par1EntityPlayerMP.inventoryContainer);
        par1EntityPlayerMP.setPlayerHealthUpdated();
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(par1EntityPlayerMP.inventory.currentItem));
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount()
    {
        return this.playerEntityList.size();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getAvailablePlayerDat()
    {
        return this.mcServer.worldServers[0].getSaveHandler().getSaveHandler().getAvailablePlayerDat();
    }

    public void setWhiteListEnabled(boolean par1)
    {
        this.whiteListEnforced = par1;
    }

    public List getPlayerList(String par1Str)
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.playerEntityList.iterator();

        while (iterator.hasNext())
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)iterator.next();

            if (entityplayermp.getPlayerIP().equals(par1Str))
            {
                arraylist.add(entityplayermp);
            }
        }

        return arraylist;
    }

    /**
     * Gets the View Distance.
     */
    public int getViewDistance()
    {
        return this.viewDistance;
    }

    public MinecraftServer getServerInstance()
    {
        return this.mcServer;
    }

    /**
     * On integrated servers, returns the host's player data to be written to level.dat.
     */
    public NBTTagCompound getHostPlayerData()
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        this.gameType = par1EnumGameType;
    }

    private void func_72381_a(EntityPlayerMP par1EntityPlayerMP, EntityPlayerMP par2EntityPlayerMP, World par3World)
    {
        if (par2EntityPlayerMP != null)
        {
            par1EntityPlayerMP.theItemInWorldManager.setGameType(par2EntityPlayerMP.theItemInWorldManager.getGameType());
        }
        else if (this.gameType != null)
        {
            par1EntityPlayerMP.theItemInWorldManager.setGameType(this.gameType);
        }

        par1EntityPlayerMP.theItemInWorldManager.initializeGameType(par3World.getWorldInfo().getGameType());
    }

    /**
     * Sets whether all players are allowed to use commands (cheats) on the server.
     */
    @SideOnly(Side.CLIENT)
    public void setCommandsAllowedForAll(boolean par1)
    {
        this.commandsAllowedForAll = par1;
    }

    /**
     * Kicks everyone with "Server closed" as reason.
     */
    public void removeAllPlayers()
    {
        for (int i = 0; i < this.playerEntityList.size(); ++i)
        {
            ((EntityPlayerMP)this.playerEntityList.get(i)).playerNetServerHandler.kickPlayerFromServer("Server closed");
        }
    }

    public void sendChatMsgImpl(IChatComponent p_148544_1_, boolean p_148544_2_)
    {
        this.mcServer.addChatMessage(p_148544_1_);
        this.sendPacketToAllPlayers(new S02PacketChat(p_148544_1_, p_148544_2_));
    }

    /**
     * Sends the given string to every player as chat message.
     */
    public void sendChatMsg(IChatComponent p_148539_1_)
    {
        this.sendChatMsgImpl(p_148539_1_, true);
    }

    public StatisticsFile func_148538_i(String p_148538_1_)
    {
        StatisticsFile statisticsfile = (StatisticsFile)this.field_148547_k.get(p_148538_1_);

        if (statisticsfile == null)
        {
            statisticsfile = new StatisticsFile(this.mcServer, new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats/" + p_148538_1_ + ".json"));
            statisticsfile.func_150882_a();
            this.field_148547_k.put(p_148538_1_, statisticsfile);
        }

        return statisticsfile;
    }

    @SideOnly(Side.SERVER)
    public boolean isWhiteListEnabled()
    {
        return this.whiteListEnforced;
    }
}