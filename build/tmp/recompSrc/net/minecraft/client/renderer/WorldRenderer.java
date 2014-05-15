package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class WorldRenderer
{
    private TesselatorVertexState vertexState;
    /** Reference to the World object. */
    public World worldObj;
    private int glRenderList = -1;
    //private static Tessellator tessellator = Tessellator.instance;
    public static int chunksUpdated;
    public int posX;
    public int posY;
    public int posZ;
    /** Pos X minus */
    public int posXMinus;
    /** Pos Y minus */
    public int posYMinus;
    /** Pos Z minus */
    public int posZMinus;
    /** Pos X clipped */
    public int posXClip;
    /** Pos Y clipped */
    public int posYClip;
    /** Pos Z clipped */
    public int posZClip;
    public boolean isInFrustum;
    /** Should this renderer skip this render pass */
    public boolean[] skipRenderPass = new boolean[2];
    /** Pos X plus */
    public int posXPlus;
    /** Pos Y plus */
    public int posYPlus;
    /** Pos Z plus */
    public int posZPlus;
    /** Boolean for whether this renderer needs to be updated or not */
    public boolean needsUpdate;
    /** Axis aligned bounding box */
    public AxisAlignedBB rendererBoundingBox;
    /** Chunk index */
    public int chunkIndex;
    /** Is this renderer visible according to the occlusion query */
    public boolean isVisible = true;
    /** Is this renderer waiting on the result of the occlusion query */
    public boolean isWaitingOnOcclusionQuery;
    /** OpenGL occlusion query */
    public int glOcclusionQuery;
    /** Is the chunk lit */
    public boolean isChunkLit;
    private boolean isInitialized;
    /** All the tile entities that have special rendering code for this chunk */
    public List tileEntityRenderers = new ArrayList();
    private List tileEntities;
    /** Bytes sent to the GPU */
    private int bytesDrawn;
    private static final String __OBFID = "CL_00000942";

    public WorldRenderer(World par1World, List par2List, int par3, int par4, int par5, int par6)
    {
        this.worldObj = par1World;
        this.vertexState = null;
        this.tileEntities = par2List;
        this.glRenderList = par6;
        this.posX = -999;
        this.setPosition(par3, par4, par5);
        this.needsUpdate = false;
    }

    /**
     * Sets a new position for the renderer and setting it up so it can be reloaded with the new data for that position
     */
    public void setPosition(int par1, int par2, int par3)
    {
        if (par1 != this.posX || par2 != this.posY || par3 != this.posZ)
        {
            this.setDontDraw();
            this.posX = par1;
            this.posY = par2;
            this.posZ = par3;
            this.posXPlus = par1 + 8;
            this.posYPlus = par2 + 8;
            this.posZPlus = par3 + 8;
            this.posXClip = par1 & 1023;
            this.posYClip = par2;
            this.posZClip = par3 & 1023;
            this.posXMinus = par1 - this.posXClip;
            this.posYMinus = par2 - this.posYClip;
            this.posZMinus = par3 - this.posZClip;
            float f = 6.0F;
            this.rendererBoundingBox = AxisAlignedBB.getBoundingBox((double)((float)par1 - f), (double)((float)par2 - f), (double)((float)par3 - f), (double)((float)(par1 + 16) + f), (double)((float)(par2 + 16) + f), (double)((float)(par3 + 16) + f));
            GL11.glNewList(this.glRenderList + 2, GL11.GL_COMPILE);
            RenderItem.renderAABB(AxisAlignedBB.getAABBPool().getAABB((double)((float)this.posXClip - f), (double)((float)this.posYClip - f), (double)((float)this.posZClip - f), (double)((float)(this.posXClip + 16) + f), (double)((float)(this.posYClip + 16) + f), (double)((float)(this.posZClip + 16) + f)));
            GL11.glEndList();
            this.markDirty();
        }
    }

    private void setupGLTranslation()
    {
        GL11.glTranslatef((float)this.posXClip, (float)this.posYClip, (float)this.posZClip);
    }

    /**
     * Will update this chunk renderer
     */
    public void updateRenderer(EntityLivingBase p_147892_1_)
    {
        if (this.needsUpdate)
        {
            this.needsUpdate = false;
            int i = this.posX;
            int j = this.posY;
            int k = this.posZ;
            int l = this.posX + 16;
            int i1 = this.posY + 16;
            int j1 = this.posZ + 16;

            for (int k1 = 0; k1 < 2; ++k1)
            {
                this.skipRenderPass[k1] = true;
            }

            Chunk.isLit = false;
            HashSet hashset = new HashSet();
            hashset.addAll(this.tileEntityRenderers);
            this.tileEntityRenderers.clear();
            Minecraft minecraft = Minecraft.getMinecraft();
            EntityLivingBase entitylivingbase1 = minecraft.renderViewEntity;
            int l1 = MathHelper.floor_double(entitylivingbase1.posX);
            int i2 = MathHelper.floor_double(entitylivingbase1.posY);
            int j2 = MathHelper.floor_double(entitylivingbase1.posZ);
            byte b0 = 1;
            ChunkCache chunkcache = new ChunkCache(this.worldObj, i - b0, j - b0, k - b0, l + b0, i1 + b0, j1 + b0, b0);

            if (!chunkcache.extendedLevelsInChunkCache())
            {
                ++chunksUpdated;
                RenderBlocks renderblocks = new RenderBlocks(chunkcache);
                this.bytesDrawn = 0;
                this.vertexState = null;

                for (int k2 = 0; k2 < 2; ++k2)
                {
                    boolean flag = false;
                    boolean flag1 = false;
                    boolean flag2 = false;

                    for (int l2 = j; l2 < i1; ++l2)
                    {
                        for (int i3 = k; i3 < j1; ++i3)
                        {
                            for (int j3 = i; j3 < l; ++j3)
                            {
                                Block block = chunkcache.getBlock(j3, l2, i3);

                                if (block.getMaterial() != Material.air)
                                {
                                    if (!flag2)
                                    {
                                        flag2 = true;
                                        this.preRenderBlocks(k2);
                                    }

                                    if (k2 == 0 && block.hasTileEntity(chunkcache.getBlockMetadata(j3, l2, i3)))
                                    {
                                        TileEntity tileentity = chunkcache.getTileEntity(j3, l2, i3);

                                        if (TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileentity))
                                        {
                                            this.tileEntityRenderers.add(tileentity);
                                        }
                                    }

                                    int k3 = block.getRenderBlockPass();

                                    if (k3 > k2)
                                    {
                                        flag = true;
                                    }

                                    if (!block.canRenderInPass(k2)) continue;

                                    {
                                        flag1 |= renderblocks.renderBlockByRenderType(block, j3, l2, i3);

                                        if (block.getRenderType() == 0 && j3 == l1 && l2 == i2 && i3 == j2)
                                        {
                                            renderblocks.setRenderFromInside(true);
                                            renderblocks.setRenderAllFaces(true);
                                            renderblocks.renderBlockByRenderType(block, j3, l2, i3);
                                            renderblocks.setRenderFromInside(false);
                                            renderblocks.setRenderAllFaces(false);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (flag1)
                    {
                        this.skipRenderPass[k2] = false;
                    }

                    if (flag2)
                    {
                        this.postRenderBlocks(k2, p_147892_1_);
                    }
                    else
                    {
                        flag1 = false;
                    }

                    if (!flag)
                    {
                        break;
                    }
                }
            }

            HashSet hashset1 = new HashSet();
            hashset1.addAll(this.tileEntityRenderers);
            hashset1.removeAll(hashset);
            this.tileEntities.addAll(hashset1);
            hashset.removeAll(this.tileEntityRenderers);
            this.tileEntities.removeAll(hashset);
            this.isChunkLit = Chunk.isLit;
            this.isInitialized = true;
        }
    }

    private void preRenderBlocks(int p_147890_1_)
    {
        GL11.glNewList(this.glRenderList + p_147890_1_, GL11.GL_COMPILE);
        GL11.glPushMatrix();
        this.setupGLTranslation();
        float f = 1.000001F;
        GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
        GL11.glScalef(f, f, f);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setTranslation((double)(-this.posX), (double)(-this.posY), (double)(-this.posZ));
    }

    private void postRenderBlocks(int p_147891_1_, EntityLivingBase p_147891_2_)
    {
        if (p_147891_1_ == 1 && !this.skipRenderPass[p_147891_1_])
        {
            this.vertexState = Tessellator.instance.getVertexState((float)p_147891_2_.posX, (float)p_147891_2_.posY, (float)p_147891_2_.posZ);
        }

        //ForgeHooksClient.afterRenderPass(l1); Noop fo now, TODO: Event if anyone needs
        this.bytesDrawn += Tessellator.instance.draw();
        GL11.glPopMatrix();
        GL11.glEndList();
        Tessellator.instance.setTranslation(0.0D, 0.0D, 0.0D);
    }

    public void updateRendererSort(EntityLivingBase p_147889_1_)
    {
        if (this.vertexState != null && !this.skipRenderPass[1])
        {
            this.preRenderBlocks(1);
            Tessellator.instance.setVertexState(this.vertexState);
            this.postRenderBlocks(1, p_147889_1_);
        }
    }

    /**
     * Returns the distance of this chunk renderer to the entity without performing the final normalizing square root,
     * for performance reasons.
     */
    public float distanceToEntitySquared(Entity par1Entity)
    {
        float f = (float)(par1Entity.posX - (double)this.posXPlus);
        float f1 = (float)(par1Entity.posY - (double)this.posYPlus);
        float f2 = (float)(par1Entity.posZ - (double)this.posZPlus);
        return f * f + f1 * f1 + f2 * f2;
    }

    /**
     * When called this renderer won't draw anymore until its gets initialized again
     */
    public void setDontDraw()
    {
        for (int i = 0; i < 2; ++i)
        {
            this.skipRenderPass[i] = true;
        }

        this.isInFrustum = false;
        this.isInitialized = false;
        this.vertexState = null;
    }

    public void stopRendering()
    {
        this.setDontDraw();
        this.worldObj = null;
    }

    /**
     * Takes in the pass the call list is being requested for. Args: renderPass
     */
    public int getGLCallListForPass(int par1)
    {
        return !this.isInFrustum ? -1 : (!this.skipRenderPass[par1] ? this.glRenderList + par1 : -1);
    }

    public void updateInFrustum(ICamera par1ICamera)
    {
        this.isInFrustum = par1ICamera.isBoundingBoxInFrustum(this.rendererBoundingBox);
    }

    /**
     * Renders the occlusion query GL List
     */
    public void callOcclusionQueryList()
    {
        GL11.glCallList(this.glRenderList + 2);
    }

    /**
     * Checks if all render passes are to be skipped. Returns false if the renderer is not initialized
     */
    public boolean skipAllRenderPasses()
    {
        return !this.isInitialized ? false : this.skipRenderPass[0] && this.skipRenderPass[1];
    }

    /**
     * Marks the current renderer data as dirty and needing to be updated.
     */
    public void markDirty()
    {
        this.needsUpdate = true;
    }
}