package net.minecraft.network.rcon;

import com.google.common.base.Charsets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class RConUtils
{
    /** Translation array of decimal to hex digits */
    public static char[] hexDigits = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String __OBFID = "CL_00001799";

    /**
     * Read a null-terminated string from the given byte array
     */
    public static String getBytesAsString(byte[] par0ArrayOfByte, int par1, int par2)
    {
        int k = par2 - 1;
        int l;

        for (l = par1 > k ? k : par1; 0 != par0ArrayOfByte[l] && l < k; ++l)
        {
            ;
        }

        return new String(par0ArrayOfByte, par1, l - par1, Charsets.UTF_8);
    }

    /**
     * Read 4 bytes from the
     */
    public static int getRemainingBytesAsLEInt(byte[] par0ArrayOfByte, int par1)
    {
        /**
         * Read 4 bytes from the given array in little-endian format and return them as an int
         */
        return getBytesAsLEInt(par0ArrayOfByte, par1, par0ArrayOfByte.length);
    }

    /**
     * Read 4 bytes from the given array in little-endian format and return them as an int
     */
    public static int getBytesAsLEInt(byte[] par0ArrayOfByte, int par1, int par2)
    {
        return 0 > par2 - par1 - 4 ? 0 : par0ArrayOfByte[par1 + 3] << 24 | (par0ArrayOfByte[par1 + 2] & 255) << 16 | (par0ArrayOfByte[par1 + 1] & 255) << 8 | par0ArrayOfByte[par1] & 255;
    }

    /**
     * Read 4 bytes from the given array in big-endian format and return them as an int
     */
    public static int getBytesAsBEint(byte[] par0ArrayOfByte, int par1, int par2)
    {
        return 0 > par2 - par1 - 4 ? 0 : par0ArrayOfByte[par1] << 24 | (par0ArrayOfByte[par1 + 1] & 255) << 16 | (par0ArrayOfByte[par1 + 2] & 255) << 8 | par0ArrayOfByte[par1 + 3] & 255;
    }

    /**
     * Returns a String representation of the byte in hexadecimal format
     */
    public static String getByteAsHexString(byte par0)
    {
        return "" + hexDigits[(par0 & 240) >>> 4] + hexDigits[par0 & 15];
    }
}