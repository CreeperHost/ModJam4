package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ReportedException;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher
{
    private final Entity field_151511_a;
    /** When isBlank is true the DataWatcher is not watching any objects */
    private boolean isBlank = true;
    private static final HashMap dataTypes = new HashMap();
    private final Map watchedObjects = new HashMap();
    /** true if one or more object was changed */
    private boolean objectChanged;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final String __OBFID = "CL_00001559";

    public DataWatcher(Entity p_i45313_1_)
    {
        this.field_151511_a = p_i45313_1_;
    }

    /**
     * adds a new object to dataWatcher to watch, to update an already existing object see updateObject. Arguments: data
     * Value Id, Object to add
     */
    public void addObject(int par1, Object par2Obj)
    {
        Integer integer = (Integer)dataTypes.get(par2Obj.getClass());

        if (integer == null)
        {
            throw new IllegalArgumentException("Unknown data type: " + par2Obj.getClass());
        }
        else if (par1 > 31)
        {
            throw new IllegalArgumentException("Data value id is too big with " + par1 + "! (Max is " + 31 + ")");
        }
        else if (this.watchedObjects.containsKey(Integer.valueOf(par1)))
        {
            throw new IllegalArgumentException("Duplicate id value for " + par1 + "!");
        }
        else
        {
            DataWatcher.WatchableObject watchableobject = new DataWatcher.WatchableObject(integer.intValue(), par1, par2Obj);
            this.lock.writeLock().lock();
            this.watchedObjects.put(Integer.valueOf(par1), watchableobject);
            this.lock.writeLock().unlock();
            this.isBlank = false;
        }
    }

    /**
     * Add a new object for the DataWatcher to watch, using the specified data type.
     */
    public void addObjectByDataType(int par1, int par2)
    {
        DataWatcher.WatchableObject watchableobject = new DataWatcher.WatchableObject(par2, par1, (Object)null);
        this.lock.writeLock().lock();
        this.watchedObjects.put(Integer.valueOf(par1), watchableobject);
        this.lock.writeLock().unlock();
        this.isBlank = false;
    }

    /**
     * gets the bytevalue of a watchable object
     */
    public byte getWatchableObjectByte(int par1)
    {
        return ((Byte)this.getWatchedObject(par1).getObject()).byteValue();
    }

    public short getWatchableObjectShort(int par1)
    {
        return ((Short)this.getWatchedObject(par1).getObject()).shortValue();
    }

    /**
     * gets a watchable object and returns it as a Integer
     */
    public int getWatchableObjectInt(int par1)
    {
        return ((Integer)this.getWatchedObject(par1).getObject()).intValue();
    }

    public float getWatchableObjectFloat(int par1)
    {
        return ((Float)this.getWatchedObject(par1).getObject()).floatValue();
    }

    /**
     * gets a watchable object and returns it as a String
     */
    public String getWatchableObjectString(int par1)
    {
        return (String)this.getWatchedObject(par1).getObject();
    }

    /**
     * Get a watchable object as an ItemStack.
     */
    public ItemStack getWatchableObjectItemStack(int par1)
    {
        return (ItemStack)this.getWatchedObject(par1).getObject();
    }

    /**
     * is threadsafe, unless it throws an exception, then
     */
    private DataWatcher.WatchableObject getWatchedObject(int par1)
    {
        this.lock.readLock().lock();
        DataWatcher.WatchableObject watchableobject;

        try
        {
            watchableobject = (DataWatcher.WatchableObject)this.watchedObjects.get(Integer.valueOf(par1));
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting synched entity data");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Synched entity data");
            crashreportcategory.addCrashSection("Data ID", Integer.valueOf(par1));
            throw new ReportedException(crashreport);
        }

        this.lock.readLock().unlock();
        return watchableobject;
    }

    /**
     * updates an already existing object
     */
    public void updateObject(int par1, Object par2Obj)
    {
        DataWatcher.WatchableObject watchableobject = this.getWatchedObject(par1);

        if (ObjectUtils.notEqual(par2Obj, watchableobject.getObject()))
        {
            watchableobject.setObject(par2Obj);
            this.field_151511_a.func_145781_i(par1);
            watchableobject.setWatched(true);
            this.objectChanged = true;
        }
    }

    public void setObjectWatched(int par1)
    {
        this.getWatchedObject(par1).watched = true;
        this.objectChanged = true;
    }

    public boolean hasChanges()
    {
        return this.objectChanged;
    }

    /**
     * Writes the list of watched objects (entity attribute of type {byte, short, int, float, string, ItemStack,
     * ChunkCoordinates}) to the specified PacketBuffer
     */
    public static void writeWatchedListToPacketBuffer(List p_151507_0_, PacketBuffer p_151507_1_) throws IOException
    {
        if (p_151507_0_ != null)
        {
            Iterator iterator = p_151507_0_.iterator();

            while (iterator.hasNext())
            {
                DataWatcher.WatchableObject watchableobject = (DataWatcher.WatchableObject)iterator.next();
                writeWatchableObjectToPacketBuffer(p_151507_1_, watchableobject);
            }
        }

        p_151507_1_.writeByte(127);
    }

    public List getChanged()
    {
        ArrayList arraylist = null;

        if (this.objectChanged)
        {
            this.lock.readLock().lock();
            Iterator iterator = this.watchedObjects.values().iterator();

            while (iterator.hasNext())
            {
                DataWatcher.WatchableObject watchableobject = (DataWatcher.WatchableObject)iterator.next();

                if (watchableobject.isWatched())
                {
                    watchableobject.setWatched(false);

                    if (arraylist == null)
                    {
                        arraylist = new ArrayList();
                    }

                    arraylist.add(watchableobject);
                }
            }

            this.lock.readLock().unlock();
        }

        this.objectChanged = false;
        return arraylist;
    }

    public void func_151509_a(PacketBuffer p_151509_1_) throws IOException
    {
        this.lock.readLock().lock();
        Iterator iterator = this.watchedObjects.values().iterator();

        while (iterator.hasNext())
        {
            DataWatcher.WatchableObject watchableobject = (DataWatcher.WatchableObject)iterator.next();
            writeWatchableObjectToPacketBuffer(p_151509_1_, watchableobject);
        }

        this.lock.readLock().unlock();
        p_151509_1_.writeByte(127);
    }

    public List getAllWatched()
    {
        ArrayList arraylist = null;
        this.lock.readLock().lock();
        DataWatcher.WatchableObject watchableobject;

        for (Iterator iterator = this.watchedObjects.values().iterator(); iterator.hasNext(); arraylist.add(watchableobject))
        {
            watchableobject = (DataWatcher.WatchableObject)iterator.next();

            if (arraylist == null)
            {
                arraylist = new ArrayList();
            }
        }

        this.lock.readLock().unlock();
        return arraylist;
    }

    /**
     * Writes a watchable object (entity attribute of type {byte, short, int, float, string, ItemStack,
     * ChunkCoordinates}) to the specified PacketBuffer
     */
    private static void writeWatchableObjectToPacketBuffer(PacketBuffer p_151510_0_, DataWatcher.WatchableObject p_151510_1_) throws IOException
    {
        int i = (p_151510_1_.getObjectType() << 5 | p_151510_1_.getDataValueId() & 31) & 255;
        p_151510_0_.writeByte(i);

        switch (p_151510_1_.getObjectType())
        {
            case 0:
                p_151510_0_.writeByte(((Byte)p_151510_1_.getObject()).byteValue());
                break;
            case 1:
                p_151510_0_.writeShort(((Short)p_151510_1_.getObject()).shortValue());
                break;
            case 2:
                p_151510_0_.writeInt(((Integer)p_151510_1_.getObject()).intValue());
                break;
            case 3:
                p_151510_0_.writeFloat(((Float)p_151510_1_.getObject()).floatValue());
                break;
            case 4:
                p_151510_0_.writeStringToBuffer((String)p_151510_1_.getObject());
                break;
            case 5:
                ItemStack itemstack = (ItemStack)p_151510_1_.getObject();
                p_151510_0_.writeItemStackToBuffer(itemstack);
                break;
            case 6:
                ChunkCoordinates chunkcoordinates = (ChunkCoordinates)p_151510_1_.getObject();
                p_151510_0_.writeInt(chunkcoordinates.posX);
                p_151510_0_.writeInt(chunkcoordinates.posY);
                p_151510_0_.writeInt(chunkcoordinates.posZ);
        }
    }

    /**
     * Reads a list of watched objects (entity attribute of type {byte, short, int, float, string, ItemStack,
     * ChunkCoordinates}) from the supplied PacketBuffer
     */
    public static List readWatchedListFromPacketBuffer(PacketBuffer p_151508_0_) throws IOException
    {
        ArrayList arraylist = null;

        for (byte b0 = p_151508_0_.readByte(); b0 != 127; b0 = p_151508_0_.readByte())
        {
            if (arraylist == null)
            {
                arraylist = new ArrayList();
            }

            int i = (b0 & 224) >> 5;
            int j = b0 & 31;
            DataWatcher.WatchableObject watchableobject = null;

            switch (i)
            {
                case 0:
                    watchableobject = new DataWatcher.WatchableObject(i, j, Byte.valueOf(p_151508_0_.readByte()));
                    break;
                case 1:
                    watchableobject = new DataWatcher.WatchableObject(i, j, Short.valueOf(p_151508_0_.readShort()));
                    break;
                case 2:
                    watchableobject = new DataWatcher.WatchableObject(i, j, Integer.valueOf(p_151508_0_.readInt()));
                    break;
                case 3:
                    watchableobject = new DataWatcher.WatchableObject(i, j, Float.valueOf(p_151508_0_.readFloat()));
                    break;
                case 4:
                    watchableobject = new DataWatcher.WatchableObject(i, j, p_151508_0_.readStringFromBuffer(32767));
                    break;
                case 5:
                    watchableobject = new DataWatcher.WatchableObject(i, j, p_151508_0_.readItemStackFromBuffer());
                    break;
                case 6:
                    int k = p_151508_0_.readInt();
                    int l = p_151508_0_.readInt();
                    int i1 = p_151508_0_.readInt();
                    watchableobject = new DataWatcher.WatchableObject(i, j, new ChunkCoordinates(k, l, i1));
            }

            arraylist.add(watchableobject);
        }

        return arraylist;
    }

    @SideOnly(Side.CLIENT)
    public void updateWatchedObjectsFromList(List par1List)
    {
        this.lock.writeLock().lock();
        Iterator iterator = par1List.iterator();

        while (iterator.hasNext())
        {
            DataWatcher.WatchableObject watchableobject = (DataWatcher.WatchableObject)iterator.next();
            DataWatcher.WatchableObject watchableobject1 = (DataWatcher.WatchableObject)this.watchedObjects.get(Integer.valueOf(watchableobject.getDataValueId()));

            if (watchableobject1 != null)
            {
                watchableobject1.setObject(watchableobject.getObject());
                this.field_151511_a.func_145781_i(watchableobject.getDataValueId());
            }
        }

        this.lock.writeLock().unlock();
        this.objectChanged = true;
    }

    public boolean getIsBlank()
    {
        return this.isBlank;
    }

    public void func_111144_e()
    {
        this.objectChanged = false;
    }

    static
    {
        dataTypes.put(Byte.class, Integer.valueOf(0));
        dataTypes.put(Short.class, Integer.valueOf(1));
        dataTypes.put(Integer.class, Integer.valueOf(2));
        dataTypes.put(Float.class, Integer.valueOf(3));
        dataTypes.put(String.class, Integer.valueOf(4));
        dataTypes.put(ItemStack.class, Integer.valueOf(5));
        dataTypes.put(ChunkCoordinates.class, Integer.valueOf(6));
    }

    public static class WatchableObject
        {
            private final int objectType;
            /** id of max 31 */
            private final int dataValueId;
            private Object watchedObject;
            private boolean watched;
            private static final String __OBFID = "CL_00001560";

            public WatchableObject(int par1, int par2, Object par3Obj)
            {
                this.dataValueId = par2;
                this.watchedObject = par3Obj;
                this.objectType = par1;
                this.watched = true;
            }

            public int getDataValueId()
            {
                return this.dataValueId;
            }

            public void setObject(Object par1Obj)
            {
                this.watchedObject = par1Obj;
            }

            public Object getObject()
            {
                return this.watchedObject;
            }

            public int getObjectType()
            {
                return this.objectType;
            }

            public boolean isWatched()
            {
                return this.watched;
            }

            public void setWatched(boolean par1)
            {
                this.watched = par1;
            }
        }
}