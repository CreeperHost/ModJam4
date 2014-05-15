package net.minecraft.entity.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities
{
    /** Disables player damage. */
    public boolean disableDamage;
    /** Sets/indicates whether the player is flying. */
    public boolean isFlying;
    /** whether or not to allow the player to fly when they double jump. */
    public boolean allowFlying;
    /**
     * Used to determine if creative mode is enabled, and therefore if items should be depleted on usage
     */
    public boolean isCreativeMode;
    /** Indicates whether the player is allowed to modify the surroundings */
    public boolean allowEdit = true;
    private float flySpeed = 0.05F;
    private float walkSpeed = 0.1F;
    private static final String __OBFID = "CL_00001708";

    public void writeCapabilitiesToNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setBoolean("invulnerable", this.disableDamage);
        nbttagcompound1.setBoolean("flying", this.isFlying);
        nbttagcompound1.setBoolean("mayfly", this.allowFlying);
        nbttagcompound1.setBoolean("instabuild", this.isCreativeMode);
        nbttagcompound1.setBoolean("mayBuild", this.allowEdit);
        nbttagcompound1.setFloat("flySpeed", this.flySpeed);
        nbttagcompound1.setFloat("walkSpeed", this.walkSpeed);
        par1NBTTagCompound.setTag("abilities", nbttagcompound1);
    }

    public void readCapabilitiesFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound.hasKey("abilities", 10))
        {
            NBTTagCompound nbttagcompound1 = par1NBTTagCompound.getCompoundTag("abilities");
            this.disableDamage = nbttagcompound1.getBoolean("invulnerable");
            this.isFlying = nbttagcompound1.getBoolean("flying");
            this.allowFlying = nbttagcompound1.getBoolean("mayfly");
            this.isCreativeMode = nbttagcompound1.getBoolean("instabuild");

            if (nbttagcompound1.hasKey("flySpeed", 99))
            {
                this.flySpeed = nbttagcompound1.getFloat("flySpeed");
                this.walkSpeed = nbttagcompound1.getFloat("walkSpeed");
            }

            if (nbttagcompound1.hasKey("mayBuild", 1))
            {
                this.allowEdit = nbttagcompound1.getBoolean("mayBuild");
            }
        }
    }

    public float getFlySpeed()
    {
        return this.flySpeed;
    }

    @SideOnly(Side.CLIENT)
    public void setFlySpeed(float par1)
    {
        this.flySpeed = par1;
    }

    public float getWalkSpeed()
    {
        return this.walkSpeed;
    }

    @SideOnly(Side.CLIENT)
    public void setPlayerWalkSpeed(float par1)
    {
        this.walkSpeed = par1;
    }
}