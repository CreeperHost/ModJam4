package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

@SideOnly(Side.CLIENT)
public class ServerData
{
    public String serverName;
    public String serverIP;
    /**
     * the string indicating number of players on and capacity of the server that is shown on the server browser (i.e.
     * "5/20" meaning 5 slots used out of 20 slots total)
     */
    public String populationInfo;
    /**
     * (better variable name would be 'hostname') server name as displayed in the server browser's second line (grey
     * text)
     */
    public String serverMOTD;
    /** last server ping that showed up in the server browser */
    public long pingToServer;
    public int field_82821_f = 4;
    /** Game version for this server. */
    public String gameVersion = "1.7.2";
    public boolean field_78841_f;
    public String field_147412_i;
    private boolean field_78842_g = true;
    private boolean acceptsTextures;
    /** Whether to hide the IP address for this server. */
    private boolean hideAddress;
    private String field_147411_m;
    private static final String __OBFID = "CL_00000890";

    public ServerData(String par1Str, String par2Str)
    {
        this.serverName = par1Str;
        this.serverIP = par2Str;
    }

    /**
     * Returns an NBTTagCompound with the server's name, IP and maybe acceptTextures.
     */
    public NBTTagCompound getNBTCompound()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("name", this.serverName);
        nbttagcompound.setString("ip", this.serverIP);
        nbttagcompound.setBoolean("hideAddress", this.hideAddress);

        if (this.field_147411_m != null)
        {
            nbttagcompound.setString("icon", this.field_147411_m);
        }

        if (!this.field_78842_g)
        {
            nbttagcompound.setBoolean("acceptTextures", this.acceptsTextures);
        }

        return nbttagcompound;
    }

    public boolean func_147408_b()
    {
        return this.acceptsTextures;
    }

    public boolean func_147410_c()
    {
        return this.field_78842_g;
    }

    public void setAcceptsTextures(boolean par1)
    {
        this.acceptsTextures = par1;
        this.field_78842_g = false;
    }

    public boolean isHidingAddress()
    {
        return this.hideAddress;
    }

    public void setHideAddress(boolean par1)
    {
        this.hideAddress = par1;
    }

    /**
     * Takes an NBTTagCompound with 'name' and 'ip' keys, returns a ServerData instance.
     */
    public static ServerData getServerDataFromNBTCompound(NBTTagCompound par0NBTTagCompound)
    {
        ServerData serverdata = new ServerData(par0NBTTagCompound.getString("name"), par0NBTTagCompound.getString("ip"));
        serverdata.hideAddress = par0NBTTagCompound.getBoolean("hideAddress");

        if (par0NBTTagCompound.hasKey("icon", 8))
        {
            serverdata.func_147407_a(par0NBTTagCompound.getString("icon"));
        }

        if (par0NBTTagCompound.hasKey("acceptTextures", 99))
        {
            serverdata.setAcceptsTextures(par0NBTTagCompound.getBoolean("acceptTextures"));
        }

        return serverdata;
    }

    /**
     * Returns the base-64 encoded representation of the server's icon, or null if not available
     */
    public String getBase64EncodedIconData()
    {
        return this.field_147411_m;
    }

    public void func_147407_a(String p_147407_1_)
    {
        this.field_147411_m = p_147407_1_;
    }
}