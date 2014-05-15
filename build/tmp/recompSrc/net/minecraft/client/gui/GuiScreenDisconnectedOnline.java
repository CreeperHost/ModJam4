package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

@SideOnly(Side.CLIENT)
public class GuiScreenDisconnectedOnline extends GuiScreen
{
    private String field_146867_a;
    private IChatComponent field_146865_f;
    private List field_146866_g;
    private final GuiScreen field_146868_h;
    private static final String __OBFID = "CL_00000778";

    public GuiScreenDisconnectedOnline(GuiScreen p_i45037_1_, String p_i45037_2_, IChatComponent p_i45037_3_)
    {
        this.field_146868_h = p_i45037_1_;
        this.field_146867_a = I18n.format(p_i45037_2_, new Object[0]);
        this.field_146865_f = p_i45037_3_;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.back", new Object[0])));
        this.field_146866_g = this.fontRendererObj.listFormattedStringToWidth(this.field_146865_f.getFormattedText(), this.width - 50);
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.id == 0)
        {
            this.mc.displayGuiScreen(this.field_146868_h);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146867_a, this.width / 2, this.height / 2 - 50, 11184810);
        int k = this.height / 2 - 30;

        if (this.field_146866_g != null)
        {
            for (Iterator iterator = this.field_146866_g.iterator(); iterator.hasNext(); k += this.fontRendererObj.FONT_HEIGHT)
            {
                String s = (String)iterator.next();
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, k, 16777215);
            }
        }

        super.drawScreen(par1, par2, par3);
    }
}