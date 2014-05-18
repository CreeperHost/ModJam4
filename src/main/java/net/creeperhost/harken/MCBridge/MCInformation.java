package net.creeperhost.harken.MCBridge;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Acer on 18/05/2014.
 */
public class MCInformation {

    public enum WeatherType {SUNNY, PRECIPITATION, STORM, SNOWY}
    public enum DimensionType {OVERWORLD, NETHER, END, OTHER}
    public static String biome;

    public static DimensionType dimension = DimensionType.OVERWORLD;

    public static WeatherType weather = WeatherType.SUNNY; //default weather

    private static Queue<MCTask> mcTaskQueue = new ConcurrentLinkedQueue<MCTask>();

    public static int x;
    public static int y;
    public static int z;

    public static Minecraft mc = Minecraft.getMinecraft();

    public static void gatherTick()
    {
        if (mcTaskQueue.isEmpty()) return;

        MCTask task = mcTaskQueue.poll();

        task.run();
    }

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

        if (mc.currentScreen != null && mc.currentScreen instanceof GuiMainMenu)
        {
            isMenu = true;
        } else {
            isMenu = false;
        }
    }

    private static boolean isMenu = true;

    public static Object getDelayedResult(MCTask task)
    {
        mcTaskQueue.add(task);
        while (!task.hasResult())
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        return task.getResult();
    }

    public static boolean isMainMenu()
    {
        return isMenu;
    }

    public static boolean canMineBlock() {

        return (Boolean) getDelayedResult(new MCTask()
        {

            @Override
            public void run()
            {

                EntityPlayer player = mc.thePlayer;

                MovingObjectPosition position = getMovingObjectPositionFromPlayer(mc.theWorld, player, false);

                if (position == null || position.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    result = false;
                    return;
                }

                Block block = mc.theWorld.getBlock(position.blockX, position.blockY, position.blockZ);

                result = block.canHarvestBlock(player, mc.theWorld.getBlockMetadata(position.blockX, position.blockY, position.blockZ));
            }

        });
    }

    public static byte mobSpawnChance()
    {
        System.out.println("test");
        return (Byte) getDelayedResult(new MCTask()
        {
            @Override
            public void run()
            {
                EntityPlayer player = mc.thePlayer;
                int startX = MathHelper.floor_double(player.posX) - 3;
                int endX = MathHelper.floor_double(player.posX) + 3;
                int startZ = MathHelper.floor_double(player.posZ) - 3;
                int endZ = MathHelper.floor_double(player.posZ) + 3;
                int Y = MathHelper.floor_double(player.posY - 1);

                for (int x = startX; x < endX; x++ )
                {
                    for (int z = startZ; z < endZ; z++)
                    {
                        int light = mc.theWorld.getFullBlockLightValue(x, y, z);
                        if (light < 8) {
                            result = (byte) 2;
                            return;
                        }
                        else if (light > 8) {
                            result = (byte) 0;
                            return;
                        }
                    }
                }
                result = (byte) 1;
            }
        });

    }

    protected static MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3) // this bit taken from Item
    {
        float f = 1.0F;
        float f1 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
        float f2 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
        double d0 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double)f;
        double d1 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double)f + (double)(par1World.isRemote ? par2EntityPlayer.getEyeHeight() - par2EntityPlayer.getDefaultEyeHeight() : par2EntityPlayer.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double)f;
        Vec3 vec3 = par1World.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (par2EntityPlayer instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP)par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return par1World.func_147447_a(vec3, vec31, par3, !par3, false);
    }
}
