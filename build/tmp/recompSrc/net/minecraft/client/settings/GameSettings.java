package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

@SideOnly(Side.CLIENT)
public class GameSettings
{
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new Gson();
    private static final ParameterizedType typeListString = new ParameterizedType()
    {
        private static final String __OBFID = "CL_00000651";
        public Type[] getActualTypeArguments()
        {
            return new Type[] {String.class};
        }
        public Type getRawType()
        {
            return List.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };
    /** GUI scale values */
    private static final String[] GUISCALES = new String[] {"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] PARTICLES = new String[] {"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[] {"options.ao.off", "options.ao.min", "options.ao.max"};
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    /** Advanced OpenGL */
    public boolean advancedOpengl;
    public boolean fboEnable = true;
    public int limitFramerate = 120;
    public boolean fancyGraphics = true;
    /** Smooth Lighting */
    public int ambientOcclusion = 2;
    /** Clouds flag */
    public boolean clouds = true;
    public List resourcePacks = new ArrayList();
    public EntityPlayer.EnumChatVisibility chatVisibility;
    public boolean chatColours;
    public boolean chatLinks;
    public boolean chatLinksPrompt;
    public float chatOpacity;
    public boolean serverTextures;
    public boolean snooperEnabled;
    public boolean fullScreen;
    public boolean enableVsync;
    public boolean hideServerAddress;
    /**
     * Whether to show advanced information on item tooltips, toggled by F3+H
     */
    public boolean advancedItemTooltips;
    /** Whether to pause when the game loses focus, toggled by F3+P */
    public boolean pauseOnLostFocus;
    /** Whether to show your cape */
    public boolean showCape;
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips;
    public float chatScale;
    public float chatWidth;
    public float chatHeightUnfocused;
    public float chatHeightFocused;
    public boolean showInventoryAchievementHint;
    public int mipmapLevels;
    public int anisotropicFiltering;
    private Map mapSoundLevels;
    public KeyBinding keyBindForward;
    public KeyBinding keyBindLeft;
    public KeyBinding keyBindBack;
    public KeyBinding keyBindRight;
    public KeyBinding keyBindJump;
    public KeyBinding keyBindSneak;
    public KeyBinding keyBindInventory;
    public KeyBinding keyBindUseItem;
    public KeyBinding keyBindDrop;
    public KeyBinding keyBindAttack;
    public KeyBinding keyBindPickBlock;
    public KeyBinding keyBindSprint;
    public KeyBinding keyBindChat;
    public KeyBinding keyBindPlayerList;
    public KeyBinding keyBindCommand;
    public KeyBinding keyBindScreenshot;
    public KeyBinding keyBindTogglePerspective;
    public KeyBinding keyBindSmoothCamera;
    public KeyBinding[] keyBindsHotbar;
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;
    /** true if debug info should be displayed instead of version */
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    /** The lastServer string. */
    public String lastServer;
    /** No clipping for singleplayer */
    public boolean noclip;
    /** Smooth Camera Toggle */
    public boolean smoothCamera;
    public boolean debugCamEnable;
    /** No clipping movement rate */
    public float noclipRate;
    /** Change rate for debug camera */
    public float debugCamRate;
    public float fovSetting;
    public float gammaSetting;
    public float saturation;
    /** GUI scale */
    public int guiScale;
    /** Determines amount of particles. 0 = All, 1 = Decreased, 2 = Minimal */
    public int particleSetting;
    /** Game settings language */
    public String language;
    public boolean forceUnicodeFont;
    private static final String __OBFID = "CL_00000650";

    public GameSettings(Minecraft par1Minecraft, File par2File)
    {
        this.chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
        this.chatColours = true;
        this.chatLinks = true;
        this.chatLinksPrompt = true;
        this.chatOpacity = 1.0F;
        this.serverTextures = true;
        this.snooperEnabled = true;
        this.enableVsync = true;
        this.pauseOnLostFocus = true;
        this.showCape = true;
        this.heldItemTooltips = true;
        this.chatScale = 1.0F;
        this.chatWidth = 1.0F;
        this.chatHeightUnfocused = 0.44366196F;
        this.chatHeightFocused = 1.0F;
        this.showInventoryAchievementHint = true;
        this.mipmapLevels = 4;
        this.anisotropicFiltering = 1;
        this.mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
        this.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
        this.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
        this.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
        this.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
        this.keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
        this.keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
        this.keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
        this.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
        this.keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
        this.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
        this.keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
        this.keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.gameplay");
        this.keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
        this.keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
        this.keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
        this.keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
        this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
        this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
        this.keyBindsHotbar = new KeyBinding[] {new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
        this.keyBindings = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindSprint}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.noclipRate = 1.0F;
        this.debugCamRate = 1.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.mc = par1Minecraft;
        this.optionsFile = new File(par2File, "options.txt");
        GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
        this.renderDistanceChunks = par1Minecraft.isJava64bit() ? 12 : 8;
        this.loadOptions();
    }

    public GameSettings()
    {
        this.chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
        this.chatColours = true;
        this.chatLinks = true;
        this.chatLinksPrompt = true;
        this.chatOpacity = 1.0F;
        this.serverTextures = true;
        this.snooperEnabled = true;
        this.enableVsync = true;
        this.pauseOnLostFocus = true;
        this.showCape = true;
        this.heldItemTooltips = true;
        this.chatScale = 1.0F;
        this.chatWidth = 1.0F;
        this.chatHeightUnfocused = 0.44366196F;
        this.chatHeightFocused = 1.0F;
        this.showInventoryAchievementHint = true;
        this.mipmapLevels = 4;
        this.anisotropicFiltering = 1;
        this.mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
        this.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
        this.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
        this.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
        this.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
        this.keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
        this.keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
        this.keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
        this.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
        this.keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
        this.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
        this.keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
        this.keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.gameplay");
        this.keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
        this.keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
        this.keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
        this.keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
        this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
        this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
        this.keyBindsHotbar = new KeyBinding[] {new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
        this.keyBindings = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[] {this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindSprint}, this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.noclipRate = 1.0F;
        this.debugCamRate = 1.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
    }

    /**
     * Represents a key or mouse button as a string. Args: key
     */
    public static String getKeyDisplayString(int par0)
    {
        return par0 < 0 ? I18n.format("key.mouseButton", new Object[] {Integer.valueOf(par0 + 101)}): Keyboard.getKeyName(par0);
    }

    /**
     * Returns whether the specified key binding is currently being pressed.
     */
    public static boolean isKeyDown(KeyBinding par0KeyBinding)
    {
        return par0KeyBinding.getKeyCode() == 0 ? false : (par0KeyBinding.getKeyCode() < 0 ? Mouse.isButtonDown(par0KeyBinding.getKeyCode() + 100) : Keyboard.isKeyDown(par0KeyBinding.getKeyCode()));
    }

    /**
     * Sets a key binding and then saves all settings.
     */
    public void setOptionKeyBinding(KeyBinding p_151440_1_, int p_151440_2_)
    {
        p_151440_1_.setKeyCode(p_151440_2_);
        this.saveOptions();
    }

    /**
     * If the specified option is controlled by a slider (float value), this will set the float value.
     */
    public void setOptionFloatValue(GameSettings.Options par1EnumOptions, float par2)
    {
        if (par1EnumOptions == GameSettings.Options.SENSITIVITY)
        {
            this.mouseSensitivity = par2;
        }

        if (par1EnumOptions == GameSettings.Options.FOV)
        {
            this.fovSetting = par2;
        }

        if (par1EnumOptions == GameSettings.Options.GAMMA)
        {
            this.gammaSetting = par2;
        }

        if (par1EnumOptions == GameSettings.Options.FRAMERATE_LIMIT)
        {
            this.limitFramerate = (int)par2;
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_OPACITY)
        {
            this.chatOpacity = par2;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_FOCUSED)
        {
            this.chatHeightFocused = par2;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED)
        {
            this.chatHeightUnfocused = par2;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_WIDTH)
        {
            this.chatWidth = par2;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_SCALE)
        {
            this.chatScale = par2;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        int i;

        if (par1EnumOptions == GameSettings.Options.ANISOTROPIC_FILTERING)
        {
            i = this.anisotropicFiltering;
            this.anisotropicFiltering = (int)par2;

            if ((float)i != par2)
            {
                this.mc.getTextureMapBlocks().setAnisotropicFiltering(this.anisotropicFiltering);
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (par1EnumOptions == GameSettings.Options.MIPMAP_LEVELS)
        {
            i = this.mipmapLevels;
            this.mipmapLevels = (int)par2;

            if ((float)i != par2)
            {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (par1EnumOptions == GameSettings.Options.RENDER_DISTANCE)
        {
            this.renderDistanceChunks = (int)par2;
        }
    }

    /**
     * For non-float options. Toggles the option on/off, or cycles through the list i.e. render distances.
     */
    public void setOptionValue(GameSettings.Options par1EnumOptions, int par2)
    {
        if (par1EnumOptions == GameSettings.Options.INVERT_MOUSE)
        {
            this.invertMouse = !this.invertMouse;
        }

        if (par1EnumOptions == GameSettings.Options.GUI_SCALE)
        {
            this.guiScale = this.guiScale + par2 & 3;
        }

        if (par1EnumOptions == GameSettings.Options.PARTICLES)
        {
            this.particleSetting = (this.particleSetting + par2) % 3;
        }

        if (par1EnumOptions == GameSettings.Options.VIEW_BOBBING)
        {
            this.viewBobbing = !this.viewBobbing;
        }

        if (par1EnumOptions == GameSettings.Options.RENDER_CLOUDS)
        {
            this.clouds = !this.clouds;
        }

        if (par1EnumOptions == GameSettings.Options.FORCE_UNICODE_FONT)
        {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRenderer.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }

        if (par1EnumOptions == GameSettings.Options.ADVANCED_OPENGL)
        {
            this.advancedOpengl = !this.advancedOpengl;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.FBO_ENABLE)
        {
            this.fboEnable = !this.fboEnable;
        }

        if (par1EnumOptions == GameSettings.Options.ANAGLYPH)
        {
            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }

        if (par1EnumOptions == GameSettings.Options.DIFFICULTY)
        {
            this.difficulty = EnumDifficulty.getDifficultyEnum(this.difficulty.getDifficultyId() + par2 & 3);
        }

        if (par1EnumOptions == GameSettings.Options.GRAPHICS)
        {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.AMBIENT_OCCLUSION)
        {
            this.ambientOcclusion = (this.ambientOcclusion + par2) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_VISIBILITY)
        {
            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + par2) % 3);
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_COLOR)
        {
            this.chatColours = !this.chatColours;
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_LINKS)
        {
            this.chatLinks = !this.chatLinks;
        }

        if (par1EnumOptions == GameSettings.Options.CHAT_LINKS_PROMPT)
        {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }

        if (par1EnumOptions == GameSettings.Options.USE_SERVER_TEXTURES)
        {
            this.serverTextures = !this.serverTextures;
        }

        if (par1EnumOptions == GameSettings.Options.SNOOPER_ENABLED)
        {
            this.snooperEnabled = !this.snooperEnabled;
        }

        if (par1EnumOptions == GameSettings.Options.SHOW_CAPE)
        {
            this.showCape = !this.showCape;
        }

        if (par1EnumOptions == GameSettings.Options.TOUCHSCREEN)
        {
            this.touchscreen = !this.touchscreen;
        }

        if (par1EnumOptions == GameSettings.Options.USE_FULLSCREEN)
        {
            this.fullScreen = !this.fullScreen;

            if (this.mc.isFullScreen() != this.fullScreen)
            {
                this.mc.toggleFullscreen();
            }
        }

        if (par1EnumOptions == GameSettings.Options.ENABLE_VSYNC)
        {
            this.enableVsync = !this.enableVsync;
            Display.setVSyncEnabled(this.enableVsync);
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(GameSettings.Options par1EnumOptions)
    {
        return par1EnumOptions == GameSettings.Options.FOV ? this.fovSetting : (par1EnumOptions == GameSettings.Options.GAMMA ? this.gammaSetting : (par1EnumOptions == GameSettings.Options.SATURATION ? this.saturation : (par1EnumOptions == GameSettings.Options.SENSITIVITY ? this.mouseSensitivity : (par1EnumOptions == GameSettings.Options.CHAT_OPACITY ? this.chatOpacity : (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? this.chatHeightFocused : (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? this.chatHeightUnfocused : (par1EnumOptions == GameSettings.Options.CHAT_SCALE ? this.chatScale : (par1EnumOptions == GameSettings.Options.CHAT_WIDTH ? this.chatWidth : (par1EnumOptions == GameSettings.Options.FRAMERATE_LIMIT ? (float)this.limitFramerate : (par1EnumOptions == GameSettings.Options.ANISOTROPIC_FILTERING ? (float)this.anisotropicFiltering : (par1EnumOptions == GameSettings.Options.MIPMAP_LEVELS ? (float)this.mipmapLevels : (par1EnumOptions == GameSettings.Options.RENDER_DISTANCE ? (float)this.renderDistanceChunks : 0.0F))))))))))));
    }

    public boolean getOptionOrdinalValue(GameSettings.Options par1EnumOptions)
    {
        switch (GameSettings.SwitchOptions.optionIds[par1EnumOptions.ordinal()])
        {
            case 1:
                return this.invertMouse;
            case 2:
                return this.viewBobbing;
            case 3:
                return this.anaglyph;
            case 4:
                return this.advancedOpengl;
            case 5:
                return this.fboEnable;
            case 6:
                return this.clouds;
            case 7:
                return this.chatColours;
            case 8:
                return this.chatLinks;
            case 9:
                return this.chatLinksPrompt;
            case 10:
                return this.serverTextures;
            case 11:
                return this.snooperEnabled;
            case 12:
                return this.fullScreen;
            case 13:
                return this.enableVsync;
            case 14:
                return this.showCape;
            case 15:
                return this.touchscreen;
            case 16:
                return this.forceUnicodeFont;
            default:
                return false;
        }
    }

    /**
     * Returns the translation of the given index in the given String array. If the index is smaller than 0 or greater
     * than/equal to the length of the String array, it is changed to 0.
     */
    private static String getTranslation(String[] par0ArrayOfStr, int par1)
    {
        if (par1 < 0 || par1 >= par0ArrayOfStr.length)
        {
            par1 = 0;
        }

        return I18n.format(par0ArrayOfStr[par1], new Object[0]);
    }

    /**
     * Gets a key binding.
     */
    public String getKeyBinding(GameSettings.Options par1EnumOptions)
    {
        String s = I18n.format(par1EnumOptions.getEnumString(), new Object[0]) + ": ";

        if (par1EnumOptions.getEnumFloat())
        {
            float f1 = this.getOptionFloatValue(par1EnumOptions);
            float f = par1EnumOptions.normalizeValue(f1);
            return par1EnumOptions == GameSettings.Options.SENSITIVITY ? (f == 0.0F ? s + I18n.format("options.sensitivity.min", new Object[0]) : (f == 1.0F ? s + I18n.format("options.sensitivity.max", new Object[0]) : s + (int)(f * 200.0F) + "%")) : (par1EnumOptions == GameSettings.Options.FOV ? (f == 0.0F ? s + I18n.format("options.fov.min", new Object[0]) : (f == 1.0F ? s + I18n.format("options.fov.max", new Object[0]) : s + (int)(70.0F + f * 40.0F))) : (par1EnumOptions == GameSettings.Options.FRAMERATE_LIMIT ? (f1 == par1EnumOptions.valueMax ? s + I18n.format("options.framerateLimit.max", new Object[0]) : s + (int)f1 + " fps") : (par1EnumOptions == GameSettings.Options.GAMMA ? (f == 0.0F ? s + I18n.format("options.gamma.min", new Object[0]) : (f == 1.0F ? s + I18n.format("options.gamma.max", new Object[0]) : s + "+" + (int)(f * 100.0F) + "%")) : (par1EnumOptions == GameSettings.Options.SATURATION ? s + (int)(f * 400.0F) + "%" : (par1EnumOptions == GameSettings.Options.CHAT_OPACITY ? s + (int)(f * 90.0F + 10.0F) + "%" : (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED ? s + GuiNewChat.func_146243_b(f) + "px" : (par1EnumOptions == GameSettings.Options.CHAT_HEIGHT_FOCUSED ? s + GuiNewChat.func_146243_b(f) + "px" : (par1EnumOptions == GameSettings.Options.CHAT_WIDTH ? s + GuiNewChat.func_146233_a(f) + "px" : (par1EnumOptions == GameSettings.Options.RENDER_DISTANCE ? s + (int)f1 + " chunks" : (par1EnumOptions == GameSettings.Options.ANISOTROPIC_FILTERING ? (f1 == 1.0F ? s + I18n.format("options.off", new Object[0]) : s + (int)f1) : (par1EnumOptions == GameSettings.Options.MIPMAP_LEVELS ? (f1 == 0.0F ? s + I18n.format("options.off", new Object[0]) : s + (int)f1) : (f == 0.0F ? s + I18n.format("options.off", new Object[0]) : s + (int)(f * 100.0F) + "%"))))))))))));
        }
        else if (par1EnumOptions.getEnumBoolean())
        {
            boolean flag = this.getOptionOrdinalValue(par1EnumOptions);
            return flag ? s + I18n.format("options.on", new Object[0]) : s + I18n.format("options.off", new Object[0]);
        }
        else if (par1EnumOptions == GameSettings.Options.DIFFICULTY)
        {
            return s + I18n.format(this.difficulty.getDifficultyResourceKey(), new Object[0]);
        }
        else if (par1EnumOptions == GameSettings.Options.GUI_SCALE)
        {
            return s + getTranslation(GUISCALES, this.guiScale);
        }
        else if (par1EnumOptions == GameSettings.Options.CHAT_VISIBILITY)
        {
            return s + I18n.format(this.chatVisibility.getResourceKey(), new Object[0]);
        }
        else if (par1EnumOptions == GameSettings.Options.PARTICLES)
        {
            return s + getTranslation(PARTICLES, this.particleSetting);
        }
        else if (par1EnumOptions == GameSettings.Options.AMBIENT_OCCLUSION)
        {
            return s + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
        }
        else if (par1EnumOptions == GameSettings.Options.GRAPHICS)
        {
            if (this.fancyGraphics)
            {
                return s + I18n.format("options.graphics.fancy", new Object[0]);
            }
            else
            {
                String s1 = "options.graphics.fast";
                return s + I18n.format("options.graphics.fast", new Object[0]);
            }
        }
        else
        {
            return s;
        }
    }

    /**
     * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
     */
    public void loadOptions()
    {
        try
        {
            if (!this.optionsFile.exists())
            {
                return;
            }

            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.optionsFile));
            String s = "";
            this.mapSoundLevels.clear();

            while ((s = bufferedreader.readLine()) != null)
            {
                try
                {
                    String[] astring = s.split(":");

                    if (astring[0].equals("mouseSensitivity"))
                    {
                        this.mouseSensitivity = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("fov"))
                    {
                        this.fovSetting = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("gamma"))
                    {
                        this.gammaSetting = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("saturation"))
                    {
                        this.saturation = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("invertYMouse"))
                    {
                        this.invertMouse = astring[1].equals("true");
                    }

                    if (astring[0].equals("renderDistance"))
                    {
                        this.renderDistanceChunks = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("guiScale"))
                    {
                        this.guiScale = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("particles"))
                    {
                        this.particleSetting = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("bobView"))
                    {
                        this.viewBobbing = astring[1].equals("true");
                    }

                    if (astring[0].equals("anaglyph3d"))
                    {
                        this.anaglyph = astring[1].equals("true");
                    }

                    if (astring[0].equals("advancedOpengl"))
                    {
                        this.advancedOpengl = astring[1].equals("true");
                    }

                    if (astring[0].equals("maxFps"))
                    {
                        this.limitFramerate = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("fboEnable"))
                    {
                        this.fboEnable = astring[1].equals("true");
                    }

                    if (astring[0].equals("difficulty"))
                    {
                        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(astring[1]));
                    }

                    if (astring[0].equals("fancyGraphics"))
                    {
                        this.fancyGraphics = astring[1].equals("true");
                    }

                    if (astring[0].equals("ao"))
                    {
                        if (astring[1].equals("true"))
                        {
                            this.ambientOcclusion = 2;
                        }
                        else if (astring[1].equals("false"))
                        {
                            this.ambientOcclusion = 0;
                        }
                        else
                        {
                            this.ambientOcclusion = Integer.parseInt(astring[1]);
                        }
                    }

                    if (astring[0].equals("clouds"))
                    {
                        this.clouds = astring[1].equals("true");
                    }

                    if (astring[0].equals("resourcePacks"))
                    {
                        this.resourcePacks = (List)gson.fromJson(s.substring(s.indexOf(58) + 1), typeListString);

                        if (this.resourcePacks == null)
                        {
                            this.resourcePacks = new ArrayList();
                        }
                    }

                    if (astring[0].equals("lastServer") && astring.length >= 2)
                    {
                        this.lastServer = s.substring(s.indexOf(58) + 1);
                    }

                    if (astring[0].equals("lang") && astring.length >= 2)
                    {
                        this.language = astring[1];
                    }

                    if (astring[0].equals("chatVisibility"))
                    {
                        this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(astring[1]));
                    }

                    if (astring[0].equals("chatColors"))
                    {
                        this.chatColours = astring[1].equals("true");
                    }

                    if (astring[0].equals("chatLinks"))
                    {
                        this.chatLinks = astring[1].equals("true");
                    }

                    if (astring[0].equals("chatLinksPrompt"))
                    {
                        this.chatLinksPrompt = astring[1].equals("true");
                    }

                    if (astring[0].equals("chatOpacity"))
                    {
                        this.chatOpacity = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("serverTextures"))
                    {
                        this.serverTextures = astring[1].equals("true");
                    }

                    if (astring[0].equals("snooperEnabled"))
                    {
                        this.snooperEnabled = astring[1].equals("true");
                    }

                    if (astring[0].equals("fullscreen"))
                    {
                        this.fullScreen = astring[1].equals("true");
                    }

                    if (astring[0].equals("enableVsync"))
                    {
                        this.enableVsync = astring[1].equals("true");
                    }

                    if (astring[0].equals("hideServerAddress"))
                    {
                        this.hideServerAddress = astring[1].equals("true");
                    }

                    if (astring[0].equals("advancedItemTooltips"))
                    {
                        this.advancedItemTooltips = astring[1].equals("true");
                    }

                    if (astring[0].equals("pauseOnLostFocus"))
                    {
                        this.pauseOnLostFocus = astring[1].equals("true");
                    }

                    if (astring[0].equals("showCape"))
                    {
                        this.showCape = astring[1].equals("true");
                    }

                    if (astring[0].equals("touchscreen"))
                    {
                        this.touchscreen = astring[1].equals("true");
                    }

                    if (astring[0].equals("overrideHeight"))
                    {
                        this.overrideHeight = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("overrideWidth"))
                    {
                        this.overrideWidth = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("heldItemTooltips"))
                    {
                        this.heldItemTooltips = astring[1].equals("true");
                    }

                    if (astring[0].equals("chatHeightFocused"))
                    {
                        this.chatHeightFocused = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("chatHeightUnfocused"))
                    {
                        this.chatHeightUnfocused = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("chatScale"))
                    {
                        this.chatScale = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("chatWidth"))
                    {
                        this.chatWidth = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("showInventoryAchievementHint"))
                    {
                        this.showInventoryAchievementHint = astring[1].equals("true");
                    }

                    if (astring[0].equals("mipmapLevels"))
                    {
                        this.mipmapLevels = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("anisotropicFiltering"))
                    {
                        this.anisotropicFiltering = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("forceUnicodeFont"))
                    {
                        this.forceUnicodeFont = astring[1].equals("true");
                    }

                    KeyBinding[] akeybinding = this.keyBindings;
                    int i = akeybinding.length;
                    int j;

                    for (j = 0; j < i; ++j)
                    {
                        KeyBinding keybinding = akeybinding[j];

                        if (astring[0].equals("key_" + keybinding.getKeyDescription()))
                        {
                            keybinding.setKeyCode(Integer.parseInt(astring[1]));
                        }
                    }

                    SoundCategory[] asoundcategory = SoundCategory.values();
                    i = asoundcategory.length;

                    for (j = 0; j < i; ++j)
                    {
                        SoundCategory soundcategory = asoundcategory[j];

                        if (astring[0].equals("soundCategory_" + soundcategory.getCategoryName()))
                        {
                            this.mapSoundLevels.put(soundcategory, Float.valueOf(this.parseFloat(astring[1])));
                        }
                    }
                }
                catch (Exception exception)
                {
                    logger.warn("Skipping bad option: " + s);
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedreader.close();
        }
        catch (Exception exception1)
        {
            logger.error("Failed to load options", exception1);
        }
    }

    /**
     * Parses a string into a float.
     */
    private float parseFloat(String par1Str)
    {
        return par1Str.equals("true") ? 1.0F : (par1Str.equals("false") ? 0.0F : Float.parseFloat(par1Str));
    }

    /**
     * Saves the options to the options file.
     */
    public void saveOptions()
    {
        if (FMLClientHandler.instance().isLoading()) return;
        try
        {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.optionsFile));
            printwriter.println("invertYMouse:" + this.invertMouse);
            printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
            printwriter.println("fov:" + this.fovSetting);
            printwriter.println("gamma:" + this.gammaSetting);
            printwriter.println("saturation:" + this.saturation);
            printwriter.println("renderDistance:" + this.renderDistanceChunks);
            printwriter.println("guiScale:" + this.guiScale);
            printwriter.println("particles:" + this.particleSetting);
            printwriter.println("bobView:" + this.viewBobbing);
            printwriter.println("anaglyph3d:" + this.anaglyph);
            printwriter.println("advancedOpengl:" + this.advancedOpengl);
            printwriter.println("maxFps:" + this.limitFramerate);
            printwriter.println("fboEnable:" + this.fboEnable);
            printwriter.println("difficulty:" + this.difficulty.getDifficultyId());
            printwriter.println("fancyGraphics:" + this.fancyGraphics);
            printwriter.println("ao:" + this.ambientOcclusion);
            printwriter.println("clouds:" + this.clouds);
            printwriter.println("resourcePacks:" + gson.toJson(this.resourcePacks));
            printwriter.println("lastServer:" + this.lastServer);
            printwriter.println("lang:" + this.language);
            printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            printwriter.println("chatColors:" + this.chatColours);
            printwriter.println("chatLinks:" + this.chatLinks);
            printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
            printwriter.println("chatOpacity:" + this.chatOpacity);
            printwriter.println("serverTextures:" + this.serverTextures);
            printwriter.println("snooperEnabled:" + this.snooperEnabled);
            printwriter.println("fullscreen:" + this.fullScreen);
            printwriter.println("enableVsync:" + this.enableVsync);
            printwriter.println("hideServerAddress:" + this.hideServerAddress);
            printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printwriter.println("showCape:" + this.showCape);
            printwriter.println("touchscreen:" + this.touchscreen);
            printwriter.println("overrideWidth:" + this.overrideWidth);
            printwriter.println("overrideHeight:" + this.overrideHeight);
            printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
            printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
            printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            printwriter.println("chatScale:" + this.chatScale);
            printwriter.println("chatWidth:" + this.chatWidth);
            printwriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
            printwriter.println("mipmapLevels:" + this.mipmapLevels);
            printwriter.println("anisotropicFiltering:" + this.anisotropicFiltering);
            printwriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
            KeyBinding[] akeybinding = this.keyBindings;
            int i = akeybinding.length;
            int j;

            for (j = 0; j < i; ++j)
            {
                KeyBinding keybinding = akeybinding[j];
                printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode());
            }

            SoundCategory[] asoundcategory = SoundCategory.values();
            i = asoundcategory.length;

            for (j = 0; j < i; ++j)
            {
                SoundCategory soundcategory = asoundcategory[j];
                printwriter.println("soundCategory_" + soundcategory.getCategoryName() + ":" + this.getSoundLevel(soundcategory));
            }

            printwriter.close();
        }
        catch (Exception exception)
        {
            logger.error("Failed to save options", exception);
        }

        this.sendSettingsToServer();
    }

    public float getSoundLevel(SoundCategory p_151438_1_)
    {
        return this.mapSoundLevels.containsKey(p_151438_1_) ? ((Float)this.mapSoundLevels.get(p_151438_1_)).floatValue() : 1.0F;
    }

    public void setSoundLevel(SoundCategory p_151439_1_, float p_151439_2_)
    {
        this.mc.getSoundHandler().setSoundLevel(p_151439_1_, p_151439_2_);
        this.mapSoundLevels.put(p_151439_1_, Float.valueOf(p_151439_2_));
    }

    /**
     * Send a client info packet with settings information to the server
     */
    public void sendSettingsToServer()
    {
        if (this.mc.thePlayer != null)
        {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, this.difficulty, this.showCape));
        }
    }

    /**
     * Should render clouds
     */
    public boolean shouldRenderClouds()
    {
        return this.renderDistanceChunks >= 4 && this.clouds;
    }

    @SideOnly(Side.CLIENT)
    public static enum Options
    {
        INVERT_MOUSE("options.invertMouse", false, true),
        SENSITIVITY("options.sensitivity", true, false),
        FOV("options.fov", true, false),
        GAMMA("options.gamma", true, false),
        SATURATION("options.saturation", true, false),
        RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
        VIEW_BOBBING("options.viewBobbing", false, true),
        ANAGLYPH("options.anaglyph", false, true),
        ADVANCED_OPENGL("options.advancedOpengl", false, true),
        FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0F, 260.0F, 10.0F),
        FBO_ENABLE("options.fboEnable", false, true),
        DIFFICULTY("options.difficulty", false, false),
        GRAPHICS("options.graphics", false, false),
        AMBIENT_OCCLUSION("options.ao", false, false),
        GUI_SCALE("options.guiScale", false, false),
        RENDER_CLOUDS("options.renderClouds", false, true),
        PARTICLES("options.particles", false, false),
        CHAT_VISIBILITY("options.chat.visibility", false, false),
        CHAT_COLOR("options.chat.color", false, true),
        CHAT_LINKS("options.chat.links", false, true),
        CHAT_OPACITY("options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
        USE_SERVER_TEXTURES("options.serverTextures", false, true),
        SNOOPER_ENABLED("options.snooper", false, true),
        USE_FULLSCREEN("options.fullscreen", false, true),
        ENABLE_VSYNC("options.vsync", false, true),
        SHOW_CAPE("options.showCape", false, true),
        TOUCHSCREEN("options.touchscreen", false, true),
        CHAT_SCALE("options.chat.scale", true, false),
        CHAT_WIDTH("options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
        ANISOTROPIC_FILTERING("options.anisotropicFiltering", true, false, 1.0F, 16.0F, 0.0F)
        {
            private static final String __OBFID = "CL_00000654";
            protected float snapToStep(float p_148264_1_)
            {
                return (float)MathHelper.roundUpToPowerOfTwo((int)p_148264_1_);
            }
        },
        FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true);
        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private final float valueStep;
        private float valueMin;
        private float valueMax;

        private static final String __OBFID = "CL_00000653";

        public static GameSettings.Options getEnumOptions(int par0)
        {
            GameSettings.Options[] aoptions = values();
            int j = aoptions.length;

            for (int k = 0; k < j; ++k)
            {
                GameSettings.Options options = aoptions[k];

                if (options.returnEnumOrdinal() == par0)
                {
                    return options;
                }
            }

            return null;
        }

        private Options(String par3Str, boolean par4, boolean par5)
        {
            this(par3Str, par4, par5, 0.0F, 1.0F, 0.0F);
        }

        private Options(String p_i45004_3_, boolean p_i45004_4_, boolean p_i45004_5_, float p_i45004_6_, float p_i45004_7_, float p_i45004_8_)
        {
            this.enumString = p_i45004_3_;
            this.enumFloat = p_i45004_4_;
            this.enumBoolean = p_i45004_5_;
            this.valueMin = p_i45004_6_;
            this.valueMax = p_i45004_7_;
            this.valueStep = p_i45004_8_;
        }

        public boolean getEnumFloat()
        {
            return this.enumFloat;
        }

        public boolean getEnumBoolean()
        {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal()
        {
            return this.ordinal();
        }

        public String getEnumString()
        {
            return this.enumString;
        }

        public float getValueMax()
        {
            return this.valueMax;
        }

        public void setValueMax(float p_148263_1_)
        {
            this.valueMax = p_148263_1_;
        }

        public float normalizeValue(float p_148266_1_)
        {
            return MathHelper.clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(float p_148262_1_)
        {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(p_148262_1_, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float p_148268_1_)
        {
            p_148268_1_ = this.snapToStep(p_148268_1_);
            return MathHelper.clamp_float(p_148268_1_, this.valueMin, this.valueMax);
        }

        protected float snapToStep(float p_148264_1_)
        {
            if (this.valueStep > 0.0F)
            {
                p_148264_1_ = this.valueStep * (float)Math.round(p_148264_1_ / this.valueStep);
            }

            return p_148264_1_;
        }

        Options(String p_i45005_3_, boolean p_i45005_4_, boolean p_i45005_5_, float p_i45005_6_, float p_i45005_7_, float p_i45005_8_, Object p_i45005_9_)
        {
            this(p_i45005_3_, p_i45005_4_, p_i45005_5_, p_i45005_6_, p_i45005_7_, p_i45005_8_);
        }
    }

    @SideOnly(Side.CLIENT)

    static final class SwitchOptions
        {
            static final int[] optionIds = new int[GameSettings.Options.values().length];
            private static final String __OBFID = "CL_00000652";

            static
            {
                try
                {
                    optionIds[GameSettings.Options.INVERT_MOUSE.ordinal()] = 1;
                }
                catch (NoSuchFieldError var16)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.VIEW_BOBBING.ordinal()] = 2;
                }
                catch (NoSuchFieldError var15)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.ANAGLYPH.ordinal()] = 3;
                }
                catch (NoSuchFieldError var14)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.ADVANCED_OPENGL.ordinal()] = 4;
                }
                catch (NoSuchFieldError var13)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.FBO_ENABLE.ordinal()] = 5;
                }
                catch (NoSuchFieldError var12)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.RENDER_CLOUDS.ordinal()] = 6;
                }
                catch (NoSuchFieldError var11)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.CHAT_COLOR.ordinal()] = 7;
                }
                catch (NoSuchFieldError var10)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.CHAT_LINKS.ordinal()] = 8;
                }
                catch (NoSuchFieldError var9)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.CHAT_LINKS_PROMPT.ordinal()] = 9;
                }
                catch (NoSuchFieldError var8)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.USE_SERVER_TEXTURES.ordinal()] = 10;
                }
                catch (NoSuchFieldError var7)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.SNOOPER_ENABLED.ordinal()] = 11;
                }
                catch (NoSuchFieldError var6)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.USE_FULLSCREEN.ordinal()] = 12;
                }
                catch (NoSuchFieldError var5)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.ENABLE_VSYNC.ordinal()] = 13;
                }
                catch (NoSuchFieldError var4)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.SHOW_CAPE.ordinal()] = 14;
                }
                catch (NoSuchFieldError var3)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.TOUCHSCREEN.ordinal()] = 15;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    optionIds[GameSettings.Options.FORCE_UNICODE_FONT.ordinal()] = 16;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }
}