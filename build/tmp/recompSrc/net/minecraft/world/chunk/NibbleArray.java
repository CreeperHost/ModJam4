package net.minecraft.world.chunk;

public class NibbleArray
{
    /**
     * Byte array of data stored in this holder. Possibly a light map or some chunk data. Data is accessed in 4-bit
     * pieces.
     */
    public final byte[] data;
    /**
     * Log base 2 of the chunk height (128); applied as a shift on Z coordinate
     */
    private final int depthBits;
    /**
     * Log base 2 of the chunk height (128) * width (16); applied as a shift on X coordinate
     */
    private final int depthBitsPlusFour;
    private static final String __OBFID = "CL_00000371";

    public NibbleArray(int par1, int par2)
    {
        this.data = new byte[par1 >> 1];
        this.depthBits = par2;
        this.depthBitsPlusFour = par2 + 4;
    }

    public NibbleArray(byte[] par1ArrayOfByte, int par2)
    {
        this.data = par1ArrayOfByte;
        this.depthBits = par2;
        this.depthBitsPlusFour = par2 + 4;
    }

    /**
     * Returns the nibble of data corresponding to the passed in x, y, z. y is at most 6 bits, z is at most 4.
     */
    public int get(int par1, int par2, int par3)
    {
        int l = par2 << this.depthBitsPlusFour | par3 << this.depthBits | par1;
        int i1 = l >> 1;
        int j1 = l & 1;
        return j1 == 0 ? this.data[i1] & 15 : this.data[i1] >> 4 & 15;
    }

    /**
     * Arguments are x, y, z, val. Sets the nibble of data at x << 11 | z << 7 | y to val.
     */
    public void set(int par1, int par2, int par3, int par4)
    {
        int i1 = par2 << this.depthBitsPlusFour | par3 << this.depthBits | par1;
        int j1 = i1 >> 1;
        int k1 = i1 & 1;

        if (k1 == 0)
        {
            this.data[j1] = (byte)(this.data[j1] & 240 | par4 & 15);
        }
        else
        {
            this.data[j1] = (byte)(this.data[j1] & 15 | (par4 & 15) << 4);
        }
    }
}