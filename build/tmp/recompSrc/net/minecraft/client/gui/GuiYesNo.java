package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class GuiYesNo extends GuiScreen
{
    /**
     * A reference to the screen object that created this. Used for navigating between screens.
     */
    protected GuiScreen parentScreen;
    protected String field_146351_f;
    private String field_146354_r;
    /** The text shown for the first button in GuiYesNo */
    protected String confirmButtonText;
    /** The text shown for the second button in GuiYesNo */
    protected String cancelButtonText;
    protected int field_146357_i;
    private int field_146353_s;
    private static final String __OBFID = "CL_00000684";

    public GuiYesNo(GuiScreen par1GuiScreen, String par2Str, String par3Str, int par4)
    {
        this.parentScreen = par1GuiScreen;
        this.field_146351_f = par2Str;
        this.field_146354_r = par3Str;
        this.field_146357_i = par4;
        this.confirmButtonText = I18n.format("gui.yes", new Object[0]);
        this.cancelButtonText = I18n.format("gui.no", new Object[0]);
    }

    public GuiYesNo(GuiScreen par1GuiScreen, String par2Str, String par3Str, String par4Str, String par5Str, int par6)
    {
        this.parentScreen = par1GuiScreen;
        this.field_146351_f = par2Str;
        this.field_146354_r = par3Str;
        this.confirmButtonText = par4Str;
        this.cancelButtonText = par5Str;
        this.field_146357_i = par6;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText));
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        this.parentScreen.confirmClicked(p_146284_1_.id == 0, this.field_146357_i);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146351_f, this.width / 2, 70, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.field_146354_r, this.width / 2, 90, 16777215);
        super.drawScreen(par1, par2, par3);
    }

    public void func_146350_a(int p_146350_1_)
    {
        this.field_146353_s = p_146350_1_;
        GuiButton guibutton;

        for (Iterator iterator = this.buttonList.iterator(); iterator.hasNext(); guibutton.enabled = false)
        {
            guibutton = (GuiButton)iterator.next();
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        GuiButton guibutton;

        if (--this.field_146353_s == 0)
        {
            for (Iterator iterator = this.buttonList.iterator(); iterator.hasNext(); guibutton.enabled = true)
            {
                guibutton = (GuiButton)iterator.next();
            }
        }
    }
}