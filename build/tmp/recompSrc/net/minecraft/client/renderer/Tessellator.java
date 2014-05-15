package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.PriorityQueue;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.client.util.QuadComparator;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Tessellator
{
    private static int nativeBufferSize = 0x200000;
    private static int trivertsInBuffer = (nativeBufferSize / 48) * 6;
    public static boolean renderingWorldRenderer = false;
    public boolean defaultTexture = false;
    private int rawBufferSize = 0;
    public int textureID = 0;

    /** The byte buffer used for GL allocation. */
    private static ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);
    /** The same memory as byteBuffer, but referenced as an integer buffer. */
    private static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    /** The same memory as byteBuffer, but referenced as an float buffer. */
    private static FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    /** The same memory as byteBuffer, but referenced as an short buffer. */
    private static ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
    /** Raw integer array. */
    private int[] rawBuffer;
    /**
     * The number of vertices to be drawn in the next draw call. Reset to 0 between draw calls.
     */
    private int vertexCount;
    /** The first coordinate to be used for the texture. */
    private double textureU;
    /** The second coordinate to be used for the texture. */
    private double textureV;
    private int brightness;
    /** The color (RGBA) value to be used for the following draw call. */
    private int color;
    /**
     * Whether the current draw object for this tessellator has color values.
     */
    private boolean hasColor;
    /**
     * Whether the current draw object for this tessellator has texture coordinates.
     */
    private boolean hasTexture;
    private boolean hasBrightness;
    /**
     * Whether the current draw object for this tessellator has normal values.
     */
    private boolean hasNormals;
    /** The index into the raw buffer to be used for the next data. */
    private int rawBufferIndex;
    /**
     * The number of vertices manually added to the given draw call. This differs from vertexCount because it adds extra
     * vertices when converting quads to triangles.
     */
    private int addedVertices;
    /** Disables all color information for the following draw call. */
    private boolean isColorDisabled;
    /** The draw mode currently being used by the tessellator. */
    private int drawMode;
    /**
     * An offset to be applied along the x-axis for all vertices in this draw call.
     */
    private double xOffset;
    /**
     * An offset to be applied along the y-axis for all vertices in this draw call.
     */
    private double yOffset;
    /**
     * An offset to be applied along the z-axis for all vertices in this draw call.
     */
    private double zOffset;
    /** The normal to be applied to the face being drawn. */
    private int normal;
    /** The static instance of the Tessellator. */
    public static final Tessellator instance = new Tessellator(2097152);
    /** Whether this tessellator is currently in draw mode. */
    private boolean isDrawing;
    /** The size of the buffers used (in integers). */
    private int bufferSize;
    private static final String __OBFID = "CL_00000960";

    private Tessellator(int par1)
    {
    }

    public Tessellator()
    {
    }

    static
    {
        instance.defaultTexture = true;
    }

    /**
     * Draws the data set up in this tessellator and resets the state to prepare for new drawing.
     */
    public int draw()
    {
        if (!this.isDrawing)
        {
            throw new IllegalStateException("Not tesselating!");
        }
        else
        {
            this.isDrawing = false;

            int offs = 0;
            while (offs < vertexCount)
            {
                int vtc = Math.min(vertexCount - offs, nativeBufferSize >> 5);
                this.intBuffer.clear();
                this.intBuffer.put(this.rawBuffer, offs * 8, vtc * 8);
                this.byteBuffer.position(0);
                this.byteBuffer.limit(vtc * 32);
                offs += vtc;

                if (this.hasTexture)
                {
                    this.floatBuffer.position(3);
                    GL11.glTexCoordPointer(2, 32, this.floatBuffer);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    this.shortBuffer.position(14);
                    GL11.glTexCoordPointer(2, 32, this.shortBuffer);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor)
                {
                    this.byteBuffer.position(20);
                    GL11.glColorPointer(4, true, 32, this.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals)
                {
                    this.byteBuffer.position(24);
                    GL11.glNormalPointer(32, this.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }

                this.floatBuffer.position(0);
                GL11.glVertexPointer(3, 32, this.floatBuffer);
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glDrawArrays(this.drawMode, 0, vtc);
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

                if (this.hasTexture)
                {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor)
                {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals)
                {
                    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            if (rawBufferSize > 0x20000 && rawBufferIndex < (rawBufferSize << 3))
            {
                rawBufferSize = 0x10000;
                rawBuffer = new int[rawBufferSize];
            }

            int i = this.rawBufferIndex * 4;
            this.reset();
            return i;
        }
    }

    public TesselatorVertexState getVertexState(float p_147564_1_, float p_147564_2_, float p_147564_3_)
    {
        int[] aint = new int[this.rawBufferIndex];
        PriorityQueue priorityqueue = new PriorityQueue(this.rawBufferIndex, new QuadComparator(this.rawBuffer, p_147564_1_ + (float)this.xOffset, p_147564_2_ + (float)this.yOffset, p_147564_3_ + (float)this.zOffset));
        byte b0 = 32;
        int i;

        for (i = 0; i < this.rawBufferIndex; i += b0)
        {
            priorityqueue.add(Integer.valueOf(i));
        }

        for (i = 0; !priorityqueue.isEmpty(); i += b0)
        {
            int j = ((Integer)priorityqueue.remove()).intValue();

            for (int k = 0; k < b0; ++k)
            {
                aint[i + k] = this.rawBuffer[j + k];
            }
        }

        System.arraycopy(aint, 0, this.rawBuffer, 0, aint.length);
        return new TesselatorVertexState(aint, this.rawBufferIndex, this.vertexCount, this.hasTexture, this.hasBrightness, this.hasNormals, this.hasColor);
    }

    public void setVertexState(TesselatorVertexState p_147565_1_)
    {
        while (p_147565_1_.getRawBuffer().length > rawBufferSize && rawBufferSize > 0)
        {
            rawBufferSize <<= 1;
        }
        if (rawBufferSize > rawBuffer.length)
        {
            rawBuffer = new int[rawBufferSize];
        }
        System.arraycopy(p_147565_1_.getRawBuffer(), 0, this.rawBuffer, 0, p_147565_1_.getRawBuffer().length);
        this.rawBufferIndex = p_147565_1_.getRawBufferIndex();
        this.vertexCount = p_147565_1_.getVertexCount();
        this.hasTexture = p_147565_1_.getHasTexture();
        this.hasBrightness = p_147565_1_.getHasBrightness();
        this.hasColor = p_147565_1_.getHasColor();
        this.hasNormals = p_147565_1_.getHasNormals();
    }

    /**
     * Clears the tessellator state in preparation for new drawing.
     */
    private void reset()
    {
        this.vertexCount = 0;
        this.byteBuffer.clear();
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
    }

    /**
     * Sets draw mode in the tessellator to draw quads.
     */
    public void startDrawingQuads()
    {
        this.startDrawing(7);
    }

    /**
     * Resets tessellator state and prepares for drawing (with the specified draw mode).
     */
    public void startDrawing(int par1)
    {
        if (this.isDrawing)
        {
            throw new IllegalStateException("Already tesselating!");
        }
        else
        {
            this.isDrawing = true;
            this.reset();
            this.drawMode = par1;
            this.hasNormals = false;
            this.hasColor = false;
            this.hasTexture = false;
            this.hasBrightness = false;
            this.isColorDisabled = false;
        }
    }

    /**
     * Sets the texture coordinates.
     */
    public void setTextureUV(double par1, double par3)
    {
        this.hasTexture = true;
        this.textureU = par1;
        this.textureV = par3;
    }

    public void setBrightness(int par1)
    {
        this.hasBrightness = true;
        this.brightness = par1;
    }

    /**
     * Sets the RGB values as specified, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorOpaque_F(float par1, float par2, float par3)
    {
        this.setColorOpaque((int)(par1 * 255.0F), (int)(par2 * 255.0F), (int)(par3 * 255.0F));
    }

    /**
     * Sets the RGBA values for the color, converting from floats between 0 and 1 to integers from 0-255.
     */
    public void setColorRGBA_F(float par1, float par2, float par3, float par4)
    {
        this.setColorRGBA((int)(par1 * 255.0F), (int)(par2 * 255.0F), (int)(par3 * 255.0F), (int)(par4 * 255.0F));
    }

    /**
     * Sets the RGB values as specified, and sets alpha to opaque.
     */
    public void setColorOpaque(int par1, int par2, int par3)
    {
        this.setColorRGBA(par1, par2, par3, 255);
    }

    /**
     * Sets the RGBA values for the color. Also clamps them to 0-255.
     */
    public void setColorRGBA(int par1, int par2, int par3, int par4)
    {
        if (!this.isColorDisabled)
        {
            if (par1 > 255)
            {
                par1 = 255;
            }

            if (par2 > 255)
            {
                par2 = 255;
            }

            if (par3 > 255)
            {
                par3 = 255;
            }

            if (par4 > 255)
            {
                par4 = 255;
            }

            if (par1 < 0)
            {
                par1 = 0;
            }

            if (par2 < 0)
            {
                par2 = 0;
            }

            if (par3 < 0)
            {
                par3 = 0;
            }

            if (par4 < 0)
            {
                par4 = 0;
            }

            this.hasColor = true;

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            {
                this.color = par4 << 24 | par3 << 16 | par2 << 8 | par1;
            }
            else
            {
                this.color = par1 << 24 | par2 << 16 | par3 << 8 | par4;
            }
        }
    }

    /**
     * Adds a vertex specifying both x,y,z and the texture u,v for it.
     */
    public void addVertexWithUV(double par1, double par3, double par5, double par7, double par9)
    {
        this.setTextureUV(par7, par9);
        this.addVertex(par1, par3, par5);
    }

    /**
     * Adds a vertex with the specified x,y,z to the current draw call. It will trigger a draw() if the buffer gets
     * full.
     */
    public void addVertex(double par1, double par3, double par5)
    {
        if (rawBufferIndex >= rawBufferSize - 32) 
        {
            if (rawBufferSize == 0)
            {
                rawBufferSize = 0x10000;
                rawBuffer = new int[rawBufferSize];
            }
            else
            {
                rawBufferSize *= 2;
                rawBuffer = Arrays.copyOf(rawBuffer, rawBufferSize);
            }
        }
        ++this.addedVertices;

        if (this.hasTexture)
        {
            this.rawBuffer[this.rawBufferIndex + 3] = Float.floatToRawIntBits((float)this.textureU);
            this.rawBuffer[this.rawBufferIndex + 4] = Float.floatToRawIntBits((float)this.textureV);
        }

        if (this.hasBrightness)
        {
            this.rawBuffer[this.rawBufferIndex + 7] = this.brightness;
        }

        if (this.hasColor)
        {
            this.rawBuffer[this.rawBufferIndex + 5] = this.color;
        }

        if (this.hasNormals)
        {
            this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
        }

        this.rawBuffer[this.rawBufferIndex + 0] = Float.floatToRawIntBits((float)(par1 + this.xOffset));
        this.rawBuffer[this.rawBufferIndex + 1] = Float.floatToRawIntBits((float)(par3 + this.yOffset));
        this.rawBuffer[this.rawBufferIndex + 2] = Float.floatToRawIntBits((float)(par5 + this.zOffset));
        this.rawBufferIndex += 8;
        ++this.vertexCount;
    }

    /**
     * Sets the color to the given opaque value (stored as byte values packed in an integer).
     */
    public void setColorOpaque_I(int par1)
    {
        int j = par1 >> 16 & 255;
        int k = par1 >> 8 & 255;
        int l = par1 & 255;
        this.setColorOpaque(j, k, l);
    }

    /**
     * Sets the color to the given color (packed as bytes in integer) and alpha values.
     */
    public void setColorRGBA_I(int par1, int par2)
    {
        int k = par1 >> 16 & 255;
        int l = par1 >> 8 & 255;
        int i1 = par1 & 255;
        this.setColorRGBA(k, l, i1, par2);
    }

    /**
     * Disables colors for the current draw call.
     */
    public void disableColor()
    {
        this.isColorDisabled = true;
    }

    /**
     * Sets the normal for the current draw call.
     */
    public void setNormal(float par1, float par2, float par3)
    {
        this.hasNormals = true;
        byte b0 = (byte)((int)(par1 * 127.0F));
        byte b1 = (byte)((int)(par2 * 127.0F));
        byte b2 = (byte)((int)(par3 * 127.0F));
        this.normal = b0 & 255 | (b1 & 255) << 8 | (b2 & 255) << 16;
    }

    /**
     * Sets the translation for all vertices in the current draw call.
     */
    public void setTranslation(double par1, double par3, double par5)
    {
        this.xOffset = par1;
        this.yOffset = par3;
        this.zOffset = par5;
    }

    /**
     * Offsets the translation for all vertices in the current draw call.
     */
    public void addTranslation(float par1, float par2, float par3)
    {
        this.xOffset += (double)par1;
        this.yOffset += (double)par2;
        this.zOffset += (double)par3;
    }
}