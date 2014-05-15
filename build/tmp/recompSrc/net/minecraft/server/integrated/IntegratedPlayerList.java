package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

@SideOnly(Side.CLIENT)
public class IntegratedPlayerList extends ServerConfigurationManager
{
    /**
     * Holds the NBT data for the host player's save file, so this can be written to level.dat.
     */
    private NBTTagCompound hostPlayerData;
    private static final String __OBFID = "CL_00001128";

    public IntegratedPlayerList(IntegratedServer par1IntegratedServer)
    {
        super(par1IntegratedServer);
        this.viewDistance = 10;
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP par1EntityPlayerMP)
    {
        if (par1EntityPlayerMP.getCommandSenderName().equals(this.getServerInstance().getServerOwner()))
        {
            this.hostPlayerData = new NBTTagCompound();
            par1EntityPlayerMP.writeToNBT(this.hostPlayerData);
        }

        super.writePlayerData(par1EntityPlayerMP);
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String allowUserToConnect(SocketAddress p_148542_1_, GameProfile p_148542_2_)
    {
        return p_148542_2_.getName().equalsIgnoreCase(this.getServerInstance().getServerOwner()) && this.getPlayerForUsername(p_148542_2_.getName()) != null ? "That name is already taken." : super.allowUserToConnect(p_148542_1_, p_148542_2_);
    }

    public IntegratedServer getServerInstance()
    {
        return (IntegratedServer)super.getServerInstance();
    }

    /**
     * On integrated servers, returns the host's player data to be written to level.dat.
     */
    public NBTTagCompound getHostPlayerData()
    {
        return this.hostPlayerData;
    }
}