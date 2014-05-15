package net.minecraft.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public interface IBlockAccess
{
    Block getBlock(int var1, int var2, int var3);

    TileEntity getTileEntity(int var1, int var2, int var3);

    /**
     * Any Light rendered on a 1.8 Block goes through here
     */
    @SideOnly(Side.CLIENT)
    int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4);

    /**
     * Returns the block metadata at coords x,y,z
     */
    int getBlockMetadata(int var1, int var2, int var3);

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    boolean isAirBlock(int var1, int var2, int var3);

    /**
     * Gets the biome for a given set of x/z coordinates
     */
    @SideOnly(Side.CLIENT)
    BiomeGenBase getBiomeGenForCoords(int var1, int var2);

    /**
     * Returns current world height.
     */
    @SideOnly(Side.CLIENT)
    int getHeight();

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    @SideOnly(Side.CLIENT)
    boolean extendedLevelsInChunkCache();

    /**
     * Return the Vec3Pool object for this world.
     */
    Vec3Pool getWorldVec3Pool();

    /**
     * Is this block powering in the specified direction Args: x, y, z, direction
     */
    int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4);

    /**
     * FORGE: isSideSolid, pulled up from {@link World}
     *
     * @param x X coord
     * @param y Y coord
     * @param z Z coord
     * @param side Side
     * @param _default default return value
     * @return if the block is solid on the side
     */
    boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default);
}