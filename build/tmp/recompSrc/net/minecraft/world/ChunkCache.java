package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

public class ChunkCache implements IBlockAccess
{
    private int chunkX;
    private int chunkZ;
    private Chunk[][] chunkArray;
    /** True if the chunk cache is empty. */
    private boolean isEmpty;
    /** Reference to the World object. */
    private World worldObj;
    private static final String __OBFID = "CL_00000155";

    public ChunkCache(World par1World, int par2, int par3, int par4, int par5, int par6, int par7, int par8)
    {
        this.worldObj = par1World;
        this.chunkX = par2 - par8 >> 4;
        this.chunkZ = par4 - par8 >> 4;
        int l1 = par5 + par8 >> 4;
        int i2 = par7 + par8 >> 4;
        this.chunkArray = new Chunk[l1 - this.chunkX + 1][i2 - this.chunkZ + 1];
        this.isEmpty = true;
        int j2;
        int k2;
        Chunk chunk;

        for (j2 = this.chunkX; j2 <= l1; ++j2)
        {
            for (k2 = this.chunkZ; k2 <= i2; ++k2)
            {
                chunk = par1World.getChunkFromChunkCoords(j2, k2);

                if (chunk != null)
                {
                    this.chunkArray[j2 - this.chunkX][k2 - this.chunkZ] = chunk;
                }
            }
        }

        for (j2 = par2 >> 4; j2 <= par5 >> 4; ++j2)
        {
            for (k2 = par4 >> 4; k2 <= par7 >> 4; ++k2)
            {
                chunk = this.chunkArray[j2 - this.chunkX][k2 - this.chunkZ];

                if (chunk != null && !chunk.getAreLevelsEmpty(par3, par6))
                {
                    this.isEmpty = false;
                }
            }
        }
    }

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    @SideOnly(Side.CLIENT)
    public boolean extendedLevelsInChunkCache()
    {
        return this.isEmpty;
    }

    public Block getBlock(int p_147439_1_, int p_147439_2_, int p_147439_3_)
    {
        Block block = Blocks.air;

        if (p_147439_2_ >= 0 && p_147439_2_ < 256)
        {
            int l = (p_147439_1_ >> 4) - this.chunkX;
            int i1 = (p_147439_3_ >> 4) - this.chunkZ;

            if (l >= 0 && l < this.chunkArray.length && i1 >= 0 && i1 < this.chunkArray[l].length)
            {
                Chunk chunk = this.chunkArray[l][i1];

                if (chunk != null)
                {
                    block = chunk.getBlock(p_147439_1_ & 15, p_147439_2_, p_147439_3_ & 15);
                }
            }
        }

        return block;
    }

    public TileEntity getTileEntity(int p_147438_1_, int p_147438_2_, int p_147438_3_)
    {
        int l = (p_147438_1_ >> 4) - this.chunkX;
        int i1 = (p_147438_3_ >> 4) - this.chunkZ;
        if (l < 0 || l >= chunkArray.length || i1 < 0 || i1 >= chunkArray[l].length) return null;
        if (chunkArray[l][i1] == null) return null;
        return this.chunkArray[l][i1].func_150806_e(p_147438_1_ & 15, p_147438_2_, p_147438_3_ & 15);
    }

    /**
     * Any Light rendered on a 1.8 Block goes through here
     */
    @SideOnly(Side.CLIENT)
    public int getLightBrightnessForSkyBlocks(int par1, int par2, int par3, int par4)
    {
        int i1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, par1, par2, par3);
        int j1 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, par1, par2, par3);

        if (j1 < par4)
        {
            j1 = par4;
        }

        return i1 << 20 | j1 << 4;
    }

    /**
     * Returns the block metadata at coords x,y,z
     */
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        if (par2 < 0)
        {
            return 0;
        }
        else if (par2 >= 256)
        {
            return 0;
        }
        else
        {
            int l = (par1 >> 4) - this.chunkX;
            int i1 = (par3 >> 4) - this.chunkZ;
            if (l < 0 || l >= chunkArray.length || i1 < 0 || i1 >= chunkArray[l].length) return 0;
            if (chunkArray[l][i1] == null) return 0;
            return this.chunkArray[l][i1].getBlockMetadata(par1 & 15, par2, par3 & 15);
        }
    }

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    @SideOnly(Side.CLIENT)
    public BiomeGenBase getBiomeGenForCoords(int par1, int par2)
    {
        return this.worldObj.getBiomeGenForCoords(par1, par2);
    }

    /**
     * Return the Vec3Pool object for this world.
     */
    public Vec3Pool getWorldVec3Pool()
    {
        return this.worldObj.getWorldVec3Pool();
    }

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    public boolean isAirBlock(int p_147437_1_, int p_147437_2_, int p_147437_3_)
    {
        return this.getBlock(p_147437_1_, p_147437_2_, p_147437_3_).isAir(this, p_147437_1_, p_147437_2_, p_147437_3_);
    }

    /**
     * Brightness for SkyBlock.Sky is clear white and (through color computing it is assumed) DEPENDENT ON DAYTIME.
     * Brightness for SkyBlock.Block is yellowish and independent.
     */
    @SideOnly(Side.CLIENT)
    public int getSkyBlockTypeBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (par3 < 0)
        {
            par3 = 0;
        }

        if (par3 >= 256)
        {
            par3 = 255;
        }

        if (par3 >= 0 && par3 < 256 && par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 <= 30000000)
        {
            if (par1EnumSkyBlock == EnumSkyBlock.Sky && this.worldObj.provider.hasNoSky)
            {
                return 0;
            }
            else
            {
                int l;
                int i1;

                if (this.getBlock(par2, par3, par4).getUseNeighborBrightness())
                {
                    l = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3 + 1, par4);
                    i1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2 + 1, par3, par4);
                    int j1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2 - 1, par3, par4);
                    int k1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3, par4 + 1);
                    int l1 = this.getSpecialBlockBrightness(par1EnumSkyBlock, par2, par3, par4 - 1);

                    if (i1 > l)
                    {
                        l = i1;
                    }

                    if (j1 > l)
                    {
                        l = j1;
                    }

                    if (k1 > l)
                    {
                        l = k1;
                    }

                    if (l1 > l)
                    {
                        l = l1;
                    }

                    return l;
                }
                else
                {
                    l = (par2 >> 4) - this.chunkX;
                    i1 = (par4 >> 4) - this.chunkZ;
                    return this.chunkArray[l][i1].getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
                }
            }
        }
        else
        {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    /**
     * is only used on stairs and tilled fields
     */
    @SideOnly(Side.CLIENT)
    public int getSpecialBlockBrightness(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        if (par3 < 0)
        {
            par3 = 0;
        }

        if (par3 >= 256)
        {
            par3 = 255;
        }

        if (par3 >= 0 && par3 < 256 && par2 >= -30000000 && par4 >= -30000000 && par2 < 30000000 && par4 <= 30000000)
        {
            int l = (par2 >> 4) - this.chunkX;
            int i1 = (par4 >> 4) - this.chunkZ;
            return this.chunkArray[l][i1].getSavedLightValue(par1EnumSkyBlock, par2 & 15, par3, par4 & 15);
        }
        else
        {
            return par1EnumSkyBlock.defaultLightValue;
        }
    }

    /**
     * Returns current world height.
     */
    @SideOnly(Side.CLIENT)
    public int getHeight()
    {
        return 256;
    }

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    public int isBlockProvidingPowerTo(int par1, int par2, int par3, int par4)
    {
        return this.getBlock(par1, par2, par3).isProvidingStrongPower(this, par1, par2, par3, par4);
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default)
    {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000)
        {
            return _default;
        }

        return getBlock(x, y, z).isSideSolid(this, x, y, z, side);
    }
}