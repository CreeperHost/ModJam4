package net.minecraft.client.gui.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiScreenHorseInventory extends GuiContainer
{
    private static final ResourceLocation horseGuiTextures = new ResourceLocation("textures/gui/container/horse.png");
    private IInventory field_147030_v;
    private IInventory field_147029_w;
    private EntityHorse field_147034_x;
    private float field_147033_y;
    private float field_147032_z;
    private static final String __OBFID = "CL_00000760";

    public GuiScreenHorseInventory(IInventory par1IInventory, IInventory par2IInventory, EntityHorse par3EntityHorse)
    {
        super(new ContainerHorseInventory(par1IInventory, par2IInventory, par3EntityHorse));
        this.field_147030_v = par1IInventory;
        this.field_147029_w = par2IInventory;
        this.field_147034_x = par3EntityHorse;
        this.allowUserInput = false;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        this.fontRendererObj.drawString(this.field_147029_w.hasCustomInventoryName() ? this.field_147029_w.getInventoryName() : I18n.format(this.field_147029_w.getInventoryName(), new Object[0]), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.field_147030_v.hasCustomInventoryName() ? this.field_147030_v.getInventoryName() : I18n.format(this.field_147030_v.getInventoryName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(horseGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        if (this.field_147034_x.isChested())
        {
            this.drawTexturedModalRect(k + 79, l + 17, 0, this.ySize, 90, 54);
        }

        if (this.field_147034_x.func_110259_cr())
        {
            this.drawTexturedModalRect(k + 7, l + 35, 0, this.ySize + 54, 18, 18);
        }

        GuiInventory.func_147046_a(k + 51, l + 60, 17, (float)(k + 51) - this.field_147033_y, (float)(l + 75 - 50) - this.field_147032_z, this.field_147034_x);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.field_147033_y = (float)par1;
        this.field_147032_z = (float)par2;
        super.drawScreen(par1, par2, par3);
    }
}