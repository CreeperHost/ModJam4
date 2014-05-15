package net.minecraft.profiler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper
{
    /** String map for report data */
    private Map dataMap = new HashMap();
    private final String uniqueID = UUID.randomUUID().toString();
    /** URL of the server to send the report to */
    private final URL serverUrl;
    private final IPlayerUsage playerStatsCollector;
    /** set to fire the snooperThread every 15 mins */
    private final Timer threadTrigger = new Timer("Snooper Timer", true);
    private final Object syncLock = new Object();
    private final long minecraftStartTimeMilis;
    private boolean isRunning;
    /** incremented on every getSelfCounterFor */
    private int selfCounter;
    private static final String __OBFID = "CL_00001515";

    public PlayerUsageSnooper(String par1Str, IPlayerUsage par2IPlayerUsage, long par3)
    {
        try
        {
            this.serverUrl = new URL("http://snoop.minecraft.net/" + par1Str + "?version=" + 1);
        }
        catch (MalformedURLException malformedurlexception)
        {
            throw new IllegalArgumentException();
        }

        this.playerStatsCollector = par2IPlayerUsage;
        this.minecraftStartTimeMilis = par3;
    }

    /**
     * Note issuing start multiple times is not an error.
     */
    public void startSnooper()
    {
        if (!this.isRunning)
        {
            this.isRunning = true;
            this.addBaseDataToSnooper();
            this.threadTrigger.schedule(new TimerTask()
            {
                private static final String __OBFID = "CL_00001516";
                public void run()
                {
                    if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled())
                    {
                        HashMap hashmap;

                        synchronized (PlayerUsageSnooper.this.syncLock)
                        {
                            hashmap = new HashMap(PlayerUsageSnooper.this.dataMap);
                            hashmap.put("snooper_count", Integer.valueOf(PlayerUsageSnooper.getSelfCounterFor(PlayerUsageSnooper.this)));
                        }

                        HttpUtil.func_151226_a(PlayerUsageSnooper.this.serverUrl, hashmap, true);
                    }
                }
            }, 0L, 900000L);
        }
    }

    private void addBaseDataToSnooper()
    {
        this.addJvmArgsToSnooper();
        this.addData("snooper_token", this.uniqueID);
        this.addData("os_name", System.getProperty("os.name"));
        this.addData("os_version", System.getProperty("os.version"));
        this.addData("os_architecture", System.getProperty("os.arch"));
        this.addData("java_version", System.getProperty("java.version"));
        this.addData("version", "1.7.2");
        this.playerStatsCollector.addServerTypeToSnooper(this);
    }

    private void addJvmArgsToSnooper()
    {
        RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
        List list = runtimemxbean.getInputArguments();
        int i = 0;
        Iterator iterator = list.iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            if (s.startsWith("-X"))
            {
                this.addData("jvm_arg[" + i++ + "]", s);
            }
        }

        this.addData("jvm_args", Integer.valueOf(i));
    }

    public void addMemoryStatsToSnooper()
    {
        this.addData("memory_total", Long.valueOf(Runtime.getRuntime().totalMemory()));
        this.addData("memory_max", Long.valueOf(Runtime.getRuntime().maxMemory()));
        this.addData("memory_free", Long.valueOf(Runtime.getRuntime().freeMemory()));
        this.addData("cpu_cores", Integer.valueOf(Runtime.getRuntime().availableProcessors()));
        this.playerStatsCollector.addServerStatsToSnooper(this);
    }

    /**
     * Adds information to the report
     */
    public void addData(String par1Str, Object par2Obj)
    {
        Object object1 = this.syncLock;

        synchronized (this.syncLock)
        {
            this.dataMap.put(par1Str, par2Obj);
        }
    }

    @SideOnly(Side.CLIENT)
    public Map getCurrentStats()
    {
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object object = this.syncLock;

        synchronized (this.syncLock)
        {
            this.addMemoryStatsToSnooper();
            Iterator iterator = this.dataMap.entrySet().iterator();

            while (iterator.hasNext())
            {
                Entry entry = (Entry)iterator.next();
                linkedhashmap.put(entry.getKey(), entry.getValue().toString());
            }

            return linkedhashmap;
        }
    }

    public boolean isSnooperRunning()
    {
        return this.isRunning;
    }

    public void stopSnooper()
    {
        this.threadTrigger.cancel();
    }

    @SideOnly(Side.CLIENT)
    public String getUniqueID()
    {
        return this.uniqueID;
    }

    /**
     * Returns the saved value of System#currentTimeMillis when the game started
     */
    public long getMinecraftStartTimeMillis()
    {
        return this.minecraftStartTimeMilis;
    }

    /**
     * returns a value indicating how many times this function has been run on the snooper
     */
    static int getSelfCounterFor(PlayerUsageSnooper par0PlayerUsageSnooper)
    {
        return par0PlayerUsageSnooper.selfCounter++;
    }
}