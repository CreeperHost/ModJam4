package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IProgressUpdate
{
    /**
     * "Saving level", or the loading,or downloading equivelent
     */
    void displayProgressMessage(String var1);

    /**
     * this string, followed by "working..." and then the "% complete" are the 3 lines shown. This resets progress to 0,
     * and the WorkingString to "working...".
     */
    @SideOnly(Side.CLIENT)
    void resetProgressAndMessage(String var1);

    /**
     * This is called with "Working..." by resetProgressAndMessage
     */
    void resetProgresAndWorkingMessage(String var1);

    /**
     * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
     */
    void setLoadingProgress(int var1);

    @SideOnly(Side.CLIENT)
    void func_146586_a();
}