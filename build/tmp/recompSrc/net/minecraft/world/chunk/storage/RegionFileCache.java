package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache
{
    /** A map containing Files as keys and RegionFiles as values */
    private static final Map regionsByFilename = new HashMap();
    private static final String __OBFID = "CL_00000383";

    public static synchronized RegionFile createOrLoadRegionFile(File par0File, int par1, int par2)
    {
        File file2 = new File(par0File, "region");
        File file3 = new File(file2, "r." + (par1 >> 5) + "." + (par2 >> 5) + ".mca");
        RegionFile regionfile = (RegionFile)regionsByFilename.get(file3);

        if (regionfile != null)
        {
            return regionfile;
        }
        else
        {
            if (!file2.exists())
            {
                file2.mkdirs();
            }

            if (regionsByFilename.size() >= 256)
            {
                clearRegionFileReferences();
            }

            RegionFile regionfile1 = new RegionFile(file3);
            regionsByFilename.put(file3, regionfile1);
            return regionfile1;
        }
    }

    /**
     * Saves the current Chunk Map Cache
     */
    public static synchronized void clearRegionFileReferences()
    {
        Iterator iterator = regionsByFilename.values().iterator();

        while (iterator.hasNext())
        {
            RegionFile regionfile = (RegionFile)iterator.next();

            try
            {
                if (regionfile != null)
                {
                    regionfile.close();
                }
            }
            catch (IOException ioexception)
            {
                ioexception.printStackTrace();
            }
        }

        regionsByFilename.clear();
    }

    /**
     * Returns an input stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataInputStream getChunkInputStream(File par0File, int par1, int par2)
    {
        RegionFile regionfile = createOrLoadRegionFile(par0File, par1, par2);
        return regionfile.getChunkDataInputStream(par1 & 31, par2 & 31);
    }

    /**
     * Returns an output stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataOutputStream getChunkOutputStream(File par0File, int par1, int par2)
    {
        RegionFile regionfile = createOrLoadRegionFile(par0File, par1, par2);
        return regionfile.getChunkDataOutputStream(par1 & 31, par2 & 31);
    }
}