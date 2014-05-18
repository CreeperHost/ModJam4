package net.creeperhost.harken.MCBridge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;

/**
 * Created by Acer on 18/05/2014.
 */
public class MCInformation {

    public enum WeatherType {SUNNY, PRECIPITATION, STORM}
    public enum DimensionType {OVERWORLD, NETHER, END, OTHER}
    public static String biome;

    public static DimensionType dimension = DimensionType.OVERWORLD;

    public static WeatherType weather = WeatherType.SUNNY; //default weather

    public static int x;
    public static int y;
    public static int z;

    public static Minecraft mc = Minecraft.getMinecraft();

    public static void gatherInformation()
    {
        EntityPlayer player = mc.thePlayer;

        if (player != null)
        {
            x = MathHelper.floor_double(player.posX);
            y = MathHelper.floor_double(player.posY);
            z = MathHelper.floor_double(player.posZ);
        }


        if (mc.theWorld != null)
        {
            if (mc.theWorld.blockExists(x, y, z))
            {
                Chunk chunk = mc.theWorld.getChunkFromBlockCoords(x, z);
                biome = chunk.getBiomeGenForWorldCoords(x & 15, z & 15, mc.theWorld.getWorldChunkManager()).biomeName;
            }

            if (mc.theWorld.getWorldInfo().isRaining())
            {
                weather = mc.theWorld.getWorldInfo().isThundering() ? WeatherType.STORM : WeatherType.PRECIPITATION;
            } else {
                weather = WeatherType.SUNNY;
            }

            if (mc.theWorld.provider != null)
            {
                switch (mc.theWorld.provider.dimensionId)
                {
                    case 0:
                        dimension = DimensionType.OVERWORLD;
                        break;
                    case 1:
                        dimension = DimensionType.END;
                        break;
                    case -1:
                        dimension = DimensionType.NETHER;
                        break;
                    default:
                        dimension = DimensionType.OTHER;
                        break;
                }
            }
        }


    }
}
