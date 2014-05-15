package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

@SideOnly(Side.CLIENT)
public class ActiveRenderInfo
{
    /** The calculated view object X coordinate */
    public static float objectX;
    /** The calculated view object Y coordinate */
    public static float objectY;
    /** The calculated view object Z coordinate */
    public static float objectZ;
    /** The current GL viewport */
    private static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    /** The current GL modelview matrix */
    private static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    /** The current GL projection matrix */
    private static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    /** The computed view object coordinates */
    private static FloatBuffer objectCoords = GLAllocation.createDirectFloatBuffer(3);
    /** The X component of the entity's yaw rotation */
    public static float rotationX;
    /** The combined X and Z components of the entity's pitch rotation */
    public static float rotationXZ;
    /** The Z component of the entity's yaw rotation */
    public static float rotationZ;
    /**
     * The Y component (scaled along the Z axis) of the entity's pitch rotation
     */
    public static float rotationYZ;
    /**
     * The Y component (scaled along the X axis) of the entity's pitch rotation
     */
    public static float rotationXY;
    private static final String __OBFID = "CL_00000626";

    /**
     * Updates the current render info and camera location based on entity look angles and 1st/3rd person view mode
     */
    public static void updateRenderInfo(EntityPlayer par0EntityPlayer, boolean par1)
    {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        float f = (float)((viewport.get(0) + viewport.get(2)) / 2);
        float f1 = (float)((viewport.get(1) + viewport.get(3)) / 2);
        GLU.gluUnProject(f, f1, 0.0F, modelview, projection, viewport, objectCoords);
        objectX = objectCoords.get(0);
        objectY = objectCoords.get(1);
        objectZ = objectCoords.get(2);
        int i = par1 ? 1 : 0;
        float f2 = par0EntityPlayer.rotationPitch;
        float f3 = par0EntityPlayer.rotationYaw;
        rotationX = MathHelper.cos(f3 * (float)Math.PI / 180.0F) * (float)(1 - i * 2);
        rotationZ = MathHelper.sin(f3 * (float)Math.PI / 180.0F) * (float)(1 - i * 2);
        rotationYZ = -rotationZ * MathHelper.sin(f2 * (float)Math.PI / 180.0F) * (float)(1 - i * 2);
        rotationXY = rotationX * MathHelper.sin(f2 * (float)Math.PI / 180.0F) * (float)(1 - i * 2);
        rotationXZ = MathHelper.cos(f2 * (float)Math.PI / 180.0F);
    }

    /**
     * Returns a vector representing the projection along the given entity's view for the given distance
     */
    public static Vec3 projectViewFromEntity(EntityLivingBase par0EntityLivingBase, double par1)
    {
        double d1 = par0EntityLivingBase.prevPosX + (par0EntityLivingBase.posX - par0EntityLivingBase.prevPosX) * par1;
        double d2 = par0EntityLivingBase.prevPosY + (par0EntityLivingBase.posY - par0EntityLivingBase.prevPosY) * par1 + (double)par0EntityLivingBase.getEyeHeight();
        double d3 = par0EntityLivingBase.prevPosZ + (par0EntityLivingBase.posZ - par0EntityLivingBase.prevPosZ) * par1;
        double d4 = d1 + (double)(objectX * 1.0F);
        double d5 = d2 + (double)(objectY * 1.0F);
        double d6 = d3 + (double)(objectZ * 1.0F);
        return par0EntityLivingBase.worldObj.getWorldVec3Pool().getVecFromPool(d4, d5, d6);
    }

    public static Block getBlockAtEntityViewpoint(World p_151460_0_, EntityLivingBase p_151460_1_, float p_151460_2_)
    {
        Vec3 vec3 = projectViewFromEntity(p_151460_1_, (double)p_151460_2_);
        ChunkPosition chunkposition = new ChunkPosition(vec3);
        Block block = p_151460_0_.getBlock(chunkposition.chunkPosX, chunkposition.chunkPosY, chunkposition.chunkPosZ);

        if (block.getMaterial().isLiquid())
        {
            float f1 = BlockLiquid.getLiquidHeightPercent(p_151460_0_.getBlockMetadata(chunkposition.chunkPosX, chunkposition.chunkPosY, chunkposition.chunkPosZ)) - 0.11111111F;
            float f2 = (float)(chunkposition.chunkPosY + 1) - f1;

            if (vec3.yCoord >= (double)f2)
            {
                block = p_151460_0_.getBlock(chunkposition.chunkPosX, chunkposition.chunkPosY + 1, chunkposition.chunkPosZ);
            }
        }

        return block;
    }
}