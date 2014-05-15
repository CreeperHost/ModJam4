package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ChunkProviderClient implements IChunkProvider
{
    private static final Logger logger = LogManager.getLogger();
    /**
     * The completely empty chunk used by ChunkProviderClient when field_73236_b doesn't contain the requested
     * coordinates.
     */
    private Chunk blankChunk;
    /**
     * The mapping between ChunkCoordinates and Chunks that ChunkProviderClient maintains.
     */
    private LongHashMap chunkMapping = new LongHashMap();
    /**
     * This may have been intended to be an iterable version of all currently loaded chunks (MultiplayerChunkCache),
     * with identical contents to chunkMapping's values. However it is never actually added to.
     */
    private List chunkListing = new ArrayList();
    /** Reference to the World object. */
    private World worldObj;
    private static final String __OBFID = "CL_00000880";

    public ChunkProviderClient(World par1World)
    {
        this.blankChunk = new EmptyChunk(par1World, 0, 0);
        this.worldObj = par1World;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int par1, int par2)
    {
        return true;
    }

    /**
     * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set
     * to false
     */
    public void unloadChunk(int par1, int par2)
    {
        Chunk chunk = this.provideChunk(par1, par2);

        if (!chunk.isEmpty())
        {
            chunk.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        this.chunkListing.remove(chunk);
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int par1, int par2)
    {
        Chunk chunk = new Chunk(this.worldObj, par1, par2);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(par1, par2), chunk);
        this.chunkListing.add(chunk);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
        chunk.isChunkLoaded = true;
        return chunk;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int par1, int par2)
    {
        Chunk chunk = (Chunk)this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        return chunk == null ? this.blankChunk : chunk;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData() {}

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        long i = System.currentTimeMillis();
        Iterator iterator = this.chunkListing.iterator();

        while (iterator.hasNext())
        {
            Chunk chunk = (Chunk)iterator.next();
            chunk.func_150804_b(System.currentTimeMillis() - i > 5L);
        }

        if (System.currentTimeMillis() - i > 100L)
        {
            logger.info("Warning: Clientside chunk ticking took {} ms", new Object[] {Long.valueOf(System.currentTimeMillis() - i)});
        }

        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return false;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {}

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        return null;
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
    {
        return null;
    }

    public int getLoadedChunkCount()
    {
        return this.chunkListing.size();
    }

    public void recreateStructures(int par1, int par2) {}
}