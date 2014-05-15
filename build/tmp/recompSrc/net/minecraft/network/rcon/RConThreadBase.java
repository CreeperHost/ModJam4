package net.minecraft.network.rcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.SERVER)
public abstract class RConThreadBase implements Runnable
{
    private static final AtomicInteger field_164004_h = new AtomicInteger(0);
    /** True if the Thread is running, false otherwise */
    protected boolean running;
    /** Reference to the IServer object. */
    protected IServer server;
    protected final String field_164003_c;
    /** Thread for this runnable class */
    protected Thread rconThread;
    protected int field_72615_d = 5;
    /** A list of registered DatagramSockets */
    protected List socketList = new ArrayList();
    /** A list of registered ServerSockets */
    protected List serverSocketList = new ArrayList();
    private static final String __OBFID = "CL_00001801";

    protected RConThreadBase(IServer p_i45300_1_, String p_i45300_2_)
    {
        this.server = p_i45300_1_;
        this.field_164003_c = p_i45300_2_;

        if (this.server.isDebuggingEnabled())
        {
            this.logWarning("Debugging is enabled, performance maybe reduced!");
        }
    }

    /**
     * Creates a new Thread object from this class and starts running
     */
    public synchronized void startThread()
    {
        this.rconThread = new Thread(this, this.field_164003_c + " #" + field_164004_h.incrementAndGet());
        this.rconThread.start();
        this.running = true;
    }

    /**
     * Returns true if the Thread is running, false otherwise
     */
    public boolean isRunning()
    {
        return this.running;
    }

    /**
     * Log debug message
     */
    protected void logDebug(String par1Str)
    {
        this.server.logDebug(par1Str);
    }

    /**
     * Log information message
     */
    protected void logInfo(String par1Str)
    {
        this.server.logInfo(par1Str);
    }

    /**
     * Log warning message
     */
    protected void logWarning(String par1Str)
    {
        this.server.logWarning(par1Str);
    }

    /**
     * Log severe error message
     */
    protected void logSevere(String par1Str)
    {
        this.server.logSevere(par1Str);
    }

    /**
     * Returns the number of players on the server
     */
    protected int getNumberOfPlayers()
    {
        return this.server.getCurrentPlayerCount();
    }

    /**
     * Registers a DatagramSocket with this thread
     */
    protected void registerSocket(DatagramSocket par1DatagramSocket)
    {
        this.logDebug("registerSocket: " + par1DatagramSocket);
        this.socketList.add(par1DatagramSocket);
    }

    /**
     * Closes the specified DatagramSocket
     */
    protected boolean closeSocket(DatagramSocket par1DatagramSocket, boolean par2)
    {
        this.logDebug("closeSocket: " + par1DatagramSocket);

        if (null == par1DatagramSocket)
        {
            return false;
        }
        else
        {
            boolean flag1 = false;

            if (!par1DatagramSocket.isClosed())
            {
                par1DatagramSocket.close();
                flag1 = true;
            }

            if (par2)
            {
                this.socketList.remove(par1DatagramSocket);
            }

            return flag1;
        }
    }

    /**
     * Closes the specified ServerSocket
     */
    protected boolean closeServerSocket(ServerSocket par1ServerSocket)
    {
        return this.closeServerSocket_do(par1ServerSocket, true);
    }

    /**
     * Closes the specified ServerSocket
     */
    protected boolean closeServerSocket_do(ServerSocket par1ServerSocket, boolean par2)
    {
        this.logDebug("closeSocket: " + par1ServerSocket);

        if (null == par1ServerSocket)
        {
            return false;
        }
        else
        {
            boolean flag1 = false;

            try
            {
                if (!par1ServerSocket.isClosed())
                {
                    par1ServerSocket.close();
                    flag1 = true;
                }
            }
            catch (IOException ioexception)
            {
                this.logWarning("IO: " + ioexception.getMessage());
            }

            if (par2)
            {
                this.serverSocketList.remove(par1ServerSocket);
            }

            return flag1;
        }
    }

    /**
     * Closes all of the opened sockets
     */
    protected void closeAllSockets()
    {
        this.closeAllSockets_do(false);
    }

    /**
     * Closes all of the opened sockets
     */
    protected void closeAllSockets_do(boolean par1)
    {
        int i = 0;
        Iterator iterator = this.socketList.iterator();

        while (iterator.hasNext())
        {
            DatagramSocket datagramsocket = (DatagramSocket)iterator.next();

            if (this.closeSocket(datagramsocket, false))
            {
                ++i;
            }
        }

        this.socketList.clear();
        iterator = this.serverSocketList.iterator();

        while (iterator.hasNext())
        {
            ServerSocket serversocket = (ServerSocket)iterator.next();

            if (this.closeServerSocket_do(serversocket, false))
            {
                ++i;
            }
        }

        this.serverSocketList.clear();

        if (par1 && 0 < i)
        {
            this.logWarning("Force closed " + i + " sockets");
        }
    }
}