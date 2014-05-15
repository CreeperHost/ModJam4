package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;

public class MathHelper
{
    /**
     * A table of sin values computed from 0 (inclusive) to 2*pi (exclusive), with steps of 2*PI / 65536.
     */
    private static float[] SIN_TABLE = new float[65536];
    /**
     * Though it looks like an array, this is really more like a mapping.  Key (index of this array) is the upper 5 bits
     * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531.  Value
     * (value stored in the array) is the unique index (from the right) of the leftmost one-bit in a 32-bit unsigned
     * integer that can cause the upper 5 bits to get that value.  Used for highly optimized "find the log-base-2 of
     * this number" calculations.
     */
    private static final int[] multiplyDeBruijnBitPosition;
    private static final String __OBFID = "CL_00001496";

    /**
     * sin looked up in a table
     */
    public static final float sin(float par0)
    {
        return SIN_TABLE[(int)(par0 * 10430.378F) & 65535];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static final float cos(float par0)
    {
        return SIN_TABLE[(int)(par0 * 10430.378F + 16384.0F) & 65535];
    }

    public static final float sqrt_float(float par0)
    {
        return (float)Math.sqrt((double)par0);
    }

    public static final float sqrt_double(double par0)
    {
        return (float)Math.sqrt(par0);
    }

    /**
     * Returns the greatest integer less than or equal to the float argument
     */
    public static int floor_float(float par0)
    {
        int i = (int)par0;
        return par0 < (float)i ? i - 1 : i;
    }

    /**
     * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
     */
    @SideOnly(Side.CLIENT)
    public static int truncateDoubleToInt(double par0)
    {
        return (int)(par0 + 1024.0D) - 1024;
    }

    /**
     * Returns the greatest integer less than or equal to the double argument
     */
    public static int floor_double(double par0)
    {
        int i = (int)par0;
        return par0 < (double)i ? i - 1 : i;
    }

    /**
     * Long version of floor_double
     */
    public static long floor_double_long(double par0)
    {
        long i = (long)par0;
        return par0 < (double)i ? i - 1L : i;
    }

    public static float abs(float par0)
    {
        return par0 >= 0.0F ? par0 : -par0;
    }

    /**
     * Returns the unsigned value of an int.
     */
    public static int abs_int(int par0)
    {
        return par0 >= 0 ? par0 : -par0;
    }

    public static int ceiling_float_int(float par0)
    {
        int i = (int)par0;
        return par0 > (float)i ? i + 1 : i;
    }

    public static int ceiling_double_int(double par0)
    {
        int i = (int)par0;
        return par0 > (double)i ? i + 1 : i;
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters.
     */
    public static int clamp_int(int par0, int par1, int par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters
     */
    public static float clamp_float(float par0, float par1, float par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    public static double clamp_double(double p_151237_0_, double p_151237_2_, double p_151237_4_)
    {
        return p_151237_0_ < p_151237_2_ ? p_151237_2_ : (p_151237_0_ > p_151237_4_ ? p_151237_4_ : p_151237_0_);
    }

    public static double denormalizeClamp(double p_151238_0_, double p_151238_2_, double p_151238_4_)
    {
        return p_151238_4_ < 0.0D ? p_151238_0_ : (p_151238_4_ > 1.0D ? p_151238_2_ : p_151238_0_ + (p_151238_2_ - p_151238_0_) * p_151238_4_);
    }

    /**
     * Maximum of the absolute value of two numbers.
     */
    public static double abs_max(double par0, double par2)
    {
        if (par0 < 0.0D)
        {
            par0 = -par0;
        }

        if (par2 < 0.0D)
        {
            par2 = -par2;
        }

        return par0 > par2 ? par0 : par2;
    }

    /**
     * Buckets an integer with specifed bucket sizes.  Args: i, bucketSize
     */
    @SideOnly(Side.CLIENT)
    public static int bucketInt(int par0, int par1)
    {
        return par0 < 0 ? -((-par0 - 1) / par1) - 1 : par0 / par1;
    }

    /**
     * Tests if a string is null or of length zero
     */
    @SideOnly(Side.CLIENT)
    public static boolean stringNullOrLengthZero(String par0Str)
    {
        return par0Str == null || par0Str.length() == 0;
    }

    public static int getRandomIntegerInRange(Random par0Random, int par1, int par2)
    {
        return par1 >= par2 ? par1 : par0Random.nextInt(par2 - par1 + 1) + par1;
    }

    public static float randomFloatClamp(Random p_151240_0_, float p_151240_1_, float p_151240_2_)
    {
        return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
    }

    public static double getRandomDoubleInRange(Random par0Random, double par1, double par3)
    {
        return par1 >= par3 ? par1 : par0Random.nextDouble() * (par3 - par1) + par1;
    }

    public static double average(long[] par0ArrayOfLong)
    {
        long i = 0L;
        long[] along1 = par0ArrayOfLong;
        int j = par0ArrayOfLong.length;

        for (int k = 0; k < j; ++k)
        {
            long l = along1[k];
            i += l;
        }

        return (double)i / (double)par0ArrayOfLong.length;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static float wrapAngleTo180_float(float par0)
    {
        par0 %= 360.0F;

        if (par0 >= 180.0F)
        {
            par0 -= 360.0F;
        }

        if (par0 < -180.0F)
        {
            par0 += 360.0F;
        }

        return par0;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static double wrapAngleTo180_double(double par0)
    {
        par0 %= 360.0D;

        if (par0 >= 180.0D)
        {
            par0 -= 360.0D;
        }

        if (par0 < -180.0D)
        {
            par0 += 360.0D;
        }

        return par0;
    }

    /**
     * parses the string as integer or returns the second parameter if it fails
     */
    public static int parseIntWithDefault(String par0Str, int par1)
    {
        int j = par1;

        try
        {
            j = Integer.parseInt(par0Str);
        }
        catch (Throwable throwable)
        {
            ;
        }

        return j;
    }

    /**
     * parses the string as integer or returns the second parameter if it fails. this value is capped to par2
     */
    public static int parseIntWithDefaultAndMax(String par0Str, int par1, int par2)
    {
        int k = par1;

        try
        {
            k = Integer.parseInt(par0Str);
        }
        catch (Throwable throwable)
        {
            ;
        }

        if (k < par2)
        {
            k = par2;
        }

        return k;
    }

    /**
     * parses the string as double or returns the second parameter if it fails.
     */
    public static double parseDoubleWithDefault(String par0Str, double par1)
    {
        double d1 = par1;

        try
        {
            d1 = Double.parseDouble(par0Str);
        }
        catch (Throwable throwable)
        {
            ;
        }

        return d1;
    }

    public static double parseDoubleWithDefaultAndMax(String par0Str, double par1, double par3)
    {
        double d2 = par1;

        try
        {
            d2 = Double.parseDouble(par0Str);
        }
        catch (Throwable throwable)
        {
            ;
        }

        if (d2 < par3)
        {
            d2 = par3;
        }

        return d2;
    }

    /**
     * Returns the input value rounded up to the next highest power of two.
     */
    @SideOnly(Side.CLIENT)
    public static int roundUpToPowerOfTwo(int p_151236_0_)
    {
        int j = p_151236_0_ - 1;
        j |= j >> 1;
        j |= j >> 2;
        j |= j >> 4;
        j |= j >> 8;
        j |= j >> 16;
        return j + 1;
    }

    /**
     * Is the given value a power of two?  (1, 2, 4, 8, 16, ...)
     */
    @SideOnly(Side.CLIENT)
    private static boolean isPowerOfTwo(int p_151235_0_)
    {
        return p_151235_0_ != 0 && (p_151235_0_ & p_151235_0_ - 1) == 0;
    }

    /**
     * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given
     * value.  Optimized for cases where the input value is a power-of-two.  If the input value is not a power-of-two,
     * then subtract 1 from the return value.
     */
    @SideOnly(Side.CLIENT)
    private static int calculateLogBaseTwoDeBruijn(int p_151241_0_)
    {
        p_151241_0_ = isPowerOfTwo(p_151241_0_) ? p_151241_0_ : roundUpToPowerOfTwo(p_151241_0_);
        return multiplyDeBruijnBitPosition[(int)((long)p_151241_0_ * 125613361L >> 27) & 31];
    }

    /**
     * Efficiently calculates the floor of the base-2 log of an integer value.  This is effectively the index of the
     * highest bit that is set.  For example, if the number in binary is 0...100101, this will return 5.
     */
    @SideOnly(Side.CLIENT)
    public static int calculateLogBaseTwo(int p_151239_0_)
    {
        /**
         * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given
         * value.  Optimized for cases where the input value is a power-of-two.  If the input value is not a power-of-
         * two, then subtract 1 from the return value.
         */
        return calculateLogBaseTwoDeBruijn(p_151239_0_) - (isPowerOfTwo(p_151239_0_) ? 0 : 1);
    }

    static
    {
        for (int var0 = 0; var0 < 65536; ++var0)
        {
            SIN_TABLE[var0] = (float)Math.sin((double)var0 * Math.PI * 2.0D / 65536.0D);
        }

        multiplyDeBruijnBitPosition = new int[] {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    }
}