package net.minecraft.client.util;

import com.google.common.collect.ComparisonChain;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Comparator;
import net.minecraft.client.renderer.RenderList;

@SideOnly(Side.CLIENT)
public class RenderDistanceSorter implements Comparator
{
    private static final String __OBFID = "CL_00000945";

    public int compare(RenderList p_147714_1_, RenderList p_147714_2_)
    {
        int i = p_147714_2_.renderChunkX * p_147714_2_.renderChunkX + p_147714_2_.renderChunkY * p_147714_2_.renderChunkY + p_147714_2_.renderChunkZ * p_147714_2_.renderChunkZ;
        int j = p_147714_1_.renderChunkX * p_147714_1_.renderChunkX + p_147714_1_.renderChunkY * p_147714_1_.renderChunkY + p_147714_1_.renderChunkZ * p_147714_1_.renderChunkZ;
        return ComparisonChain.start().compare(i, j).result();
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.compare((RenderList)par1Obj, (RenderList)par2Obj);
    }
}