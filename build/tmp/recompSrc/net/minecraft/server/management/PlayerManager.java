package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class PlayerManager
{
    private final WorldServer theWorldServer;
    /** players in the current instance */
    private final List players = new ArrayList();
    /**
     * A map of chunk position (two ints concatenated into a long) to PlayerInstance
     */
    private final LongHashMap playerInstances = new LongHashMap();
    /**
     * contains a PlayerInstance for every chunk they can see. the "player instance" cotains a list of all players who
     * can also that chunk
     */
    private final List chunkWatcherWithPlayers = new ArrayList();
    /** This field is using when chunk should be processed (every 8000 ticks) */
    private final List playerInstanceList = new ArrayList();
    /**
     * Number of chunks the server sends to the client. Valid 3<=x<=15. In server.properties.
     */
    private final int playerViewRadius;
    /** time what is using to check if InhabitedTime should be calculated */
    private long previousTotalWorldTime;
    /** x, z direction vectors: east, south, west, north */
    private final int[][] xzDirectionsConst = new int[][] {{1, 0}, {0, 1}, { -1, 0}, {0, -1}};
    private static final String __OBFID = "CL_00001434";

    public PlayerManager(WorldServer par1WorldServer, int par2)
    {
        if (par2 > 15)
        {
            throw new IllegalArgumentException("Too big view radius!");
        }
        else if (par2 < 3)
        {
            throw new IllegalArgumentException("Too small view radius!");
        }
        else
        {
            this.playerViewRadius = par2;
            this.theWorldServer = par1WorldServer;
        }
    }

    public WorldServer getWorldServer()
    {
        return this.theWorldServer;
    }

    /**
     * updates all the player instances that need to be updated
     */
    public void updatePlayerInstances()
    {
        long i = this.theWorldServer.getTotalWorldTime();
        int j;
        PlayerManager.PlayerInstance playerinstance;

        if (i - this.previousTotalWorldTime > 8000L)
        {
            this.previousTotalWorldTime = i;

            for (j = 0; j < this.playerInstanceList.size(); ++j)
            {
                playerinstance = (PlayerManager.PlayerInstance)this.playerInstanceList.get(j);
                playerinstance.sendChunkUpdate();
                playerinstance.processChunk();
            }
        }
        else
        {
            for (j = 0; j < this.chunkWatcherWithPlayers.size(); ++j)
            {
                playerinstance = (PlayerManager.PlayerInstance)this.chunkWatcherWithPlayers.get(j);
                playerinstance.sendChunkUpdate();
            }
        }

        this.chunkWatcherWithPlayers.clear();

        if (this.players.isEmpty())
        {
            WorldProvider worldprovider = this.theWorldServer.provider;

            if (!worldprovider.canRespawnHere())
            {
                this.theWorldServer.theChunkProviderServer.unloadAllChunks();
            }
        }
    }

    private PlayerManager.PlayerInstance getOrCreateChunkWatcher(int par1, int par2, boolean par3)
    {
        long k = (long)par1 + 2147483647L | (long)par2 + 2147483647L << 32;
        PlayerManager.PlayerInstance playerinstance = (PlayerManager.PlayerInstance)this.playerInstances.getValueByKey(k);

        if (playerinstance == null && par3)
        {
            playerinstance = new PlayerManager.PlayerInstance(par1, par2);
            this.playerInstances.add(k, playerinstance);
            this.playerInstanceList.add(playerinstance);
        }

        return playerinstance;
    }

    public void markBlockForUpdate(int p_151250_1_, int p_151250_2_, int p_151250_3_)
    {
        int l = p_151250_1_ >> 4;
        int i1 = p_151250_3_ >> 4;
        PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(l, i1, false);

        if (playerinstance != null)
        {
            playerinstance.flagChunkForUpdate(p_151250_1_ & 15, p_151250_2_, p_151250_3_ & 15);
        }
    }

    /**
     * Adds an EntityPlayerMP to the PlayerManager.
     */
    public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int i = (int)par1EntityPlayerMP.posX >> 4;
        int j = (int)par1EntityPlayerMP.posZ >> 4;
        par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
        par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;

        for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k)
        {
            for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l)
            {
                this.getOrCreateChunkWatcher(k, l, true).addPlayer(par1EntityPlayerMP);
            }
        }

        this.players.add(par1EntityPlayerMP);
        this.filterChunkLoadQueue(par1EntityPlayerMP);
    }

    /**
     * Removes all chunks from the given player's chunk load queue that are not in viewing range of the player.
     */
    public void filterChunkLoadQueue(EntityPlayerMP par1EntityPlayerMP)
    {
        ArrayList arraylist = new ArrayList(par1EntityPlayerMP.loadedChunks);
        int i = 0;
        int j = this.playerViewRadius;
        int k = (int)par1EntityPlayerMP.posX >> 4;
        int l = (int)par1EntityPlayerMP.posZ >> 4;
        int i1 = 0;
        int j1 = 0;
        ChunkCoordIntPair chunkcoordintpair = this.getOrCreateChunkWatcher(k, l, true).chunkLocation;
        par1EntityPlayerMP.loadedChunks.clear();

        if (arraylist.contains(chunkcoordintpair))
        {
            par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
        }

        int k1;

        for (k1 = 1; k1 <= j * 2; ++k1)
        {
            for (int l1 = 0; l1 < 2; ++l1)
            {
                int[] aint = this.xzDirectionsConst[i++ % 4];

                for (int i2 = 0; i2 < k1; ++i2)
                {
                    i1 += aint[0];
                    j1 += aint[1];
                    chunkcoordintpair = this.getOrCreateChunkWatcher(k + i1, l + j1, true).chunkLocation;

                    if (arraylist.contains(chunkcoordintpair))
                    {
                        par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
                    }
                }
            }
        }

        i %= 4;

        for (k1 = 0; k1 < j * 2; ++k1)
        {
            i1 += this.xzDirectionsConst[i][0];
            j1 += this.xzDirectionsConst[i][1];
            chunkcoordintpair = this.getOrCreateChunkWatcher(k + i1, l + j1, true).chunkLocation;

            if (arraylist.contains(chunkcoordintpair))
            {
                par1EntityPlayerMP.loadedChunks.add(chunkcoordintpair);
            }
        }
    }

    /**
     * Removes an EntityPlayerMP from the PlayerManager.
     */
    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int i = (int)par1EntityPlayerMP.managedPosX >> 4;
        int j = (int)par1EntityPlayerMP.managedPosZ >> 4;

        for (int k = i - this.playerViewRadius; k <= i + this.playerViewRadius; ++k)
        {
            for (int l = j - this.playerViewRadius; l <= j + this.playerViewRadius; ++l)
            {
                PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(k, l, false);

                if (playerinstance != null)
                {
                    playerinstance.removePlayer(par1EntityPlayerMP);
                }
            }
        }

        this.players.remove(par1EntityPlayerMP);
    }

    /**
     * Determine if two rectangles centered at the given points overlap for the provided radius. Arguments: x1, z1, x2,
     * z2, radius.
     */
    private boolean overlaps(int par1, int par2, int par3, int par4, int par5)
    {
        int j1 = par1 - par3;
        int k1 = par2 - par4;
        return j1 >= -par5 && j1 <= par5 ? k1 >= -par5 && k1 <= par5 : false;
    }

    /**
     * Update which chunks the player needs info on.
     */
    public void updatePlayerPertinentChunks(EntityPlayerMP par1EntityPlayerMP)
    {
        int i = (int)par1EntityPlayerMP.posX >> 4;
        int j = (int)par1EntityPlayerMP.posZ >> 4;
        double d0 = par1EntityPlayerMP.managedPosX - par1EntityPlayerMP.posX;
        double d1 = par1EntityPlayerMP.managedPosZ - par1EntityPlayerMP.posZ;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D)
        {
            int k = (int)par1EntityPlayerMP.managedPosX >> 4;
            int l = (int)par1EntityPlayerMP.managedPosZ >> 4;
            int i1 = this.playerViewRadius;
            int j1 = i - k;
            int k1 = j - l;

            if (j1 != 0 || k1 != 0)
            {
                for (int l1 = i - i1; l1 <= i + i1; ++l1)
                {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2)
                    {
                        if (!this.overlaps(l1, i2, k, l, i1))
                        {
                            this.getOrCreateChunkWatcher(l1, i2, true).addPlayer(par1EntityPlayerMP);
                        }

                        if (!this.overlaps(l1 - j1, i2 - k1, i, j, i1))
                        {
                            PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(l1 - j1, i2 - k1, false);

                            if (playerinstance != null)
                            {
                                playerinstance.removePlayer(par1EntityPlayerMP);
                            }
                        }
                    }
                }

                this.filterChunkLoadQueue(par1EntityPlayerMP);
                par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
                par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
            }
        }
    }

    public boolean isPlayerWatchingChunk(EntityPlayerMP par1EntityPlayerMP, int par2, int par3)
    {
        PlayerManager.PlayerInstance playerinstance = this.getOrCreateChunkWatcher(par2, par3, false);
        return playerinstance == null ? false : playerinstance.playersWatchingChunk.contains(par1EntityPlayerMP) && !par1EntityPlayerMP.loadedChunks.contains(playerinstance.chunkLocation);
    }

    /**
     * Get the furthest viewable block given player's view distance
     */
    public static int getFurthestViewableBlock(int par0)
    {
        return par0 * 16 - 16;
    }

    class PlayerInstance
    {
        private final List playersWatchingChunk = new ArrayList();
        /** note: this is final */
        private final ChunkCoordIntPair chunkLocation;
        private short[] locationOfBlockChange = new short[64];
        private int numberOfTilesToUpdate;
        /**
         * Integer field where each bit means to make update 16x16x16 division of chunk (from bottom).
         */
        private int flagsYAreasToUpdate;
        /** time what is using when chunk InhabitedTime is being calculated */
        private long previousWorldTime;
        private static final String __OBFID = "CL_00001435";

        public PlayerInstance(int par2, int par3)
        {
            this.chunkLocation = new ChunkCoordIntPair(par2, par3);
            PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(par2, par3);
        }

        public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
        {
            if (this.playersWatchingChunk.contains(par1EntityPlayerMP))
            {
                throw new IllegalStateException("Failed to add player. " + par1EntityPlayerMP + " already is in chunk " + this.chunkLocation.chunkXPos + ", " + this.chunkLocation.chunkZPos);
            }
            else
            {
                if (this.playersWatchingChunk.isEmpty())
                {
                    this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
                }

                this.playersWatchingChunk.add(par1EntityPlayerMP);
                par1EntityPlayerMP.loadedChunks.add(this.chunkLocation);
            }
        }

        public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
        {
            if (this.playersWatchingChunk.contains(par1EntityPlayerMP))
            {
                Chunk chunk = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);

                if (chunk.func_150802_k())
                {
                    par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunk, true, 0));
                }

                this.playersWatchingChunk.remove(par1EntityPlayerMP);
                par1EntityPlayerMP.loadedChunks.remove(this.chunkLocation);

                MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(chunkLocation, par1EntityPlayerMP));

                if (this.playersWatchingChunk.isEmpty())
                {
                    long i = (long)this.chunkLocation.chunkXPos + 2147483647L | (long)this.chunkLocation.chunkZPos + 2147483647L << 32;
                    this.increaseInhabitedTime(chunk);
                    PlayerManager.this.playerInstances.remove(i);
                    PlayerManager.this.playerInstanceList.remove(this);

                    if (this.numberOfTilesToUpdate > 0)
                    {
                        PlayerManager.this.chunkWatcherWithPlayers.remove(this);
                    }

                    PlayerManager.this.getWorldServer().theChunkProviderServer.unloadChunksIfNotNearSpawn(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);
                }
            }
        }

        /**
         * This method currently only increases chunk inhabited time. Extension is possible in next versions
         */
        public void processChunk()
        {
            this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos));
        }

        /**
         * Increases chunk inhabited time every 8000 ticks
         */
        private void increaseInhabitedTime(Chunk par1Chunk)
        {
            par1Chunk.inhabitedTime += PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime;
            this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
        }

        public void flagChunkForUpdate(int p_151253_1_, int p_151253_2_, int p_151253_3_)
        {
            if (this.numberOfTilesToUpdate == 0)
            {
                PlayerManager.this.chunkWatcherWithPlayers.add(this);
            }

            this.flagsYAreasToUpdate |= 1 << (p_151253_2_ >> 4);

            //if (this.numberOfTilesToUpdate < 64) //Forge; Cache everything, so always run
            {
                short short1 = (short)(p_151253_1_ << 12 | p_151253_3_ << 8 | p_151253_2_);

                for (int l = 0; l < this.numberOfTilesToUpdate; ++l)
                {
                    if (this.locationOfBlockChange[l] == short1)
                    {
                        return;
                    }
                }

                if (numberOfTilesToUpdate == locationOfBlockChange.length)
                {
                    locationOfBlockChange = Arrays.copyOf(locationOfBlockChange, locationOfBlockChange.length << 1);
                }
                this.locationOfBlockChange[this.numberOfTilesToUpdate++] = short1;
            }
        }

        public void sendToAllPlayersWatchingChunk(Packet p_151251_1_)
        {
            for (int i = 0; i < this.playersWatchingChunk.size(); ++i)
            {
                EntityPlayerMP entityplayermp = (EntityPlayerMP)this.playersWatchingChunk.get(i);

                if (!entityplayermp.loadedChunks.contains(this.chunkLocation))
                {
                    entityplayermp.playerNetServerHandler.sendPacket(p_151251_1_);
                }
            }
        }

        public void sendChunkUpdate()
        {
            if (this.numberOfTilesToUpdate != 0)
            {
                int i;
                int j;
                int k;

                if (this.numberOfTilesToUpdate == 1)
                {
                    i = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[0] >> 12 & 15);
                    j = this.locationOfBlockChange[0] & 255;
                    k = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[0] >> 8 & 15);
                    this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(i, j, k, PlayerManager.this.theWorldServer));

                    if (PlayerManager.this.theWorldServer.getBlock(i, j, k).hasTileEntity(PlayerManager.this.theWorldServer.getBlockMetadata(i, j, k)))
                    {
                        this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(i, j, k));
                    }
                }
                else
                {
                    int l;

                    if (this.numberOfTilesToUpdate == ForgeModContainer.clumpingThreshold)
                    {
                        i = this.chunkLocation.chunkXPos * 16;
                        j = this.chunkLocation.chunkZPos * 16;
                        this.sendToAllPlayersWatchingChunk(new S21PacketChunkData(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), false, this.flagsYAreasToUpdate));

                        // Forge: Grabs ALL tile entities is costly on a modded server, only send needed ones
                        for (k = 0; false && k < 16; ++k)
                        {
                            if ((this.flagsYAreasToUpdate & 1 << k) != 0)
                            {
                                l = k << 4;
                                List list = PlayerManager.this.theWorldServer.func_147486_a(i, l, j, i + 16, l + 16, j + 16);

                                for (int i1 = 0; i1 < list.size(); ++i1)
                                {
                                    this.sendTileToAllPlayersWatchingChunk((TileEntity)list.get(i1));
                                }
                            }
                        }
                    }
                    else
                    {
                        this.sendToAllPlayersWatchingChunk(new S22PacketMultiBlockChange(this.numberOfTilesToUpdate, this.locationOfBlockChange, PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos)));
                    }
                    
                    { //Forge: Send only the tile entities that are updated, Adding this brace lets us keep the indent and the patch small
                        WorldServer world = PlayerManager.this.theWorldServer;
                        for (i = 0; i < this.numberOfTilesToUpdate; ++i)
                        {
                            j = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[i] >> 12 & 15);
                            k = this.locationOfBlockChange[i] & 255;
                            l = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[i] >> 8 & 15);

                            if (world.getBlock(j, k, l).hasTileEntity(world.getBlockMetadata(j, k, l)))
                            {
                                this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(j, k, l));
                            }
                        }
                    }
                }

                this.numberOfTilesToUpdate = 0;
                this.flagsYAreasToUpdate = 0;
            }
        }

        private void sendTileToAllPlayersWatchingChunk(TileEntity p_151252_1_)
        {
            if (p_151252_1_ != null)
            {
                Packet packet = p_151252_1_.getDescriptionPacket();

                if (packet != null)
                {
                    this.sendToAllPlayersWatchingChunk(packet);
                }
            }
        }
    }
}