package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.mco.ExceptionMcoService;
import net.minecraft.client.mco.McoClient;
import net.minecraft.client.mco.McoServer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiScreenReamlsTOS extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private final GuiScreen field_146770_f;
    private final McoServer field_146771_g;
    private GuiButton field_146774_h;
    private boolean field_146775_i = false;
    private String field_146772_r = "https://minecraft.net/realms/terms";
    private static final String __OBFID = "CL_00000809";

    public GuiScreenReamlsTOS(GuiScreen p_i45045_1_, McoServer p_i45045_2_)
    {
        this.field_146770_f = p_i45045_1_;
        this.field_146771_g = p_i45045_2_;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {}

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        int i = this.width / 4;
        int j = this.width / 4 - 2;
        int k = this.width / 2 + 4;
        this.buttonList.add(this.field_146774_h = new GuiButton(1, i, this.height / 5 + 96 + 22, j, 20, I18n.format("mco.terms.buttons.agree", new Object[0])));
        this.buttonList.add(new GuiButton(2, k, this.height / 5 + 96 + 22, j, 20, I18n.format("mco.terms.buttons.disagree", new Object[0])));
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.enabled)
        {
            if (p_146284_1_.id == 2)
            {
                this.mc.displayGuiScreen(this.field_146770_f);
            }
            else if (p_146284_1_.id == 1)
            {
                this.func_146768_g();
            }
        }
    }

    private void func_146768_g()
    {
        Session session = this.mc.getSession();
        McoClient mcoclient = new McoClient(session.getSessionID(), session.getUsername(), "1.7.2", Minecraft.getMinecraft().getProxy());

        try
        {
            mcoclient.func_148714_h();
            GuiScreenLongRunningTask guiscreenlongrunningtask = new GuiScreenLongRunningTask(this.mc, this, new TaskOnlineConnect(this, this.field_146771_g));
            guiscreenlongrunningtask.func_146902_g();
            this.mc.displayGuiScreen(guiscreenlongrunningtask);
        }
        catch (ExceptionMcoService exceptionmcoservice)
        {
            logger.error("Couldn\'t agree to TOS");
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        if (this.field_146775_i)
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(this.field_146772_r), (ClipboardOwner)null);
            this.func_146769_a(this.field_146772_r);
        }
    }

    private void func_146769_a(String p_146769_1_)
    {
        try
        {
            URI uri = new URI(p_146769_1_);
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
        }
        catch (Throwable throwable)
        {
            logger.error("Couldn\'t open link");
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("mco.terms.title", new Object[0]), this.width / 2, 17, 16777215);
        this.drawString(this.fontRendererObj, I18n.format("mco.terms.sentence.1", new Object[0]), this.width / 2 - 120, 87, 16777215);
        int k = this.fontRendererObj.getStringWidth(I18n.format("mco.terms.sentence.1", new Object[0]));
        int l = 3368635;
        int i1 = 7107012;
        int j1 = this.width / 2 - 121 + k;
        byte b0 = 86;
        int k1 = j1 + this.fontRendererObj.getStringWidth("mco.terms.sentence.2") + 1;
        int l1 = 87 + this.fontRendererObj.FONT_HEIGHT;

        if (j1 <= par1 && par1 <= k1 && b0 <= par2 && par2 <= l1)
        {
            this.field_146775_i = true;
            this.drawString(this.fontRendererObj, " " + I18n.format("mco.terms.sentence.2", new Object[0]), this.width / 2 - 120 + k, 87, i1);
        }
        else
        {
            this.field_146775_i = false;
            this.drawString(this.fontRendererObj, " " + I18n.format("mco.terms.sentence.2", new Object[0]), this.width / 2 - 120 + k, 87, l);
        }

        super.drawScreen(par1, par2, par3);
    }
}