package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GLAllocation
{
    private static final Map mapDisplayLists = new HashMap();
    private static final List listDummy = new ArrayList();
    private static final String __OBFID = "CL_00000630";

    /**
     * Generates the specified number of display lists and returns the first index.
     */
    public static synchronized int generateDisplayLists(int par0)
    {
        int j = GL11.glGenLists(par0);
        mapDisplayLists.put(Integer.valueOf(j), Integer.valueOf(par0));
        return j;
    }

    public static synchronized void deleteDisplayLists(int par0)
    {
        GL11.glDeleteLists(par0, ((Integer)mapDisplayLists.remove(Integer.valueOf(par0))).intValue());
    }

    /**
     * Deletes all textures and display lists. Called when Minecraft is shutdown to free up resources.
     */
    public static synchronized void deleteTexturesAndDisplayLists()
    {
        Iterator iterator = mapDisplayLists.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            GL11.glDeleteLists(((Integer)entry.getKey()).intValue(), ((Integer)entry.getValue()).intValue());
        }

        mapDisplayLists.clear();
    }

    /**
     * Creates and returns a direct byte buffer with the specified capacity. Applies native ordering to speed up access.
     */
    public static synchronized ByteBuffer createDirectByteBuffer(int par0)
    {
        return ByteBuffer.allocateDirect(par0).order(ByteOrder.nativeOrder());
    }

    /**
     * Creates and returns a direct int buffer with the specified capacity. Applies native ordering to speed up access.
     */
    public static IntBuffer createDirectIntBuffer(int par0)
    {
        /**
         * Creates and returns a direct byte buffer with the specified capacity. Applies native ordering to speed up
         * access.
         */
        return createDirectByteBuffer(par0 << 2).asIntBuffer();
    }

    /**
     * Creates and returns a direct float buffer with the specified capacity. Applies native ordering to speed up
     * access.
     */
    public static FloatBuffer createDirectFloatBuffer(int par0)
    {
        /**
         * Creates and returns a direct byte buffer with the specified capacity. Applies native ordering to speed up
         * access.
         */
        return createDirectByteBuffer(par0 << 2).asFloatBuffer();
    }
}