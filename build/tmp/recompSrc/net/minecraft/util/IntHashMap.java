package net.minecraft.util;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap
{
    /** An array of HashEntries representing the heads of hash slot lists */
    private transient IntHashMap.Entry[] slots = new IntHashMap.Entry[16];
    /** The number of items stored in this map */
    private transient int count;
    /** The grow threshold */
    private int threshold = 12;
    /** The scale factor used to determine when to grow the table */
    private final float growFactor = 0.75F;
    /** A serial stamp used to mark changes */
    private transient volatile int versionStamp;
    /** The set of all the keys stored in this MCHash object */
    private Set keySet = new HashSet();
    private static final String __OBFID = "CL_00001490";

    /**
     * Makes the passed in integer suitable for hashing by a number of shifts
     */
    private static int computeHash(int par0)
    {
        par0 ^= par0 >>> 20 ^ par0 >>> 12;
        return par0 ^ par0 >>> 7 ^ par0 >>> 4;
    }

    /**
     * Computes the index of the slot for the hash and slot count passed in.
     */
    private static int getSlotIndex(int par0, int par1)
    {
        return par0 & par1 - 1;
    }

    /**
     * Returns the object associated to a key
     */
    public Object lookup(int par1)
    {
        int j = computeHash(par1);

        for (IntHashMap.Entry entry = this.slots[getSlotIndex(j, this.slots.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.hashEntry == par1)
            {
                return entry.valueEntry;
            }
        }

        return null;
    }

    /**
     * Return true if an object is associated with the given key
     */
    public boolean containsItem(int par1)
    {
        return this.lookupEntry(par1) != null;
    }

    /**
     * Returns the key/object mapping for a given key as a MCHashEntry
     */
    final IntHashMap.Entry lookupEntry(int par1)
    {
        int j = computeHash(par1);

        for (IntHashMap.Entry entry = this.slots[getSlotIndex(j, this.slots.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.hashEntry == par1)
            {
                return entry;
            }
        }

        return null;
    }

    /**
     * Adds a key and associated value to this map
     */
    public void addKey(int par1, Object par2Obj)
    {
        this.keySet.add(Integer.valueOf(par1));
        int j = computeHash(par1);
        int k = getSlotIndex(j, this.slots.length);

        for (IntHashMap.Entry entry = this.slots[k]; entry != null; entry = entry.nextEntry)
        {
            if (entry.hashEntry == par1)
            {
                entry.valueEntry = par2Obj;
                return;
            }
        }

        ++this.versionStamp;
        this.insert(j, par1, par2Obj, k);
    }

    /**
     * Increases the number of hash slots
     */
    private void grow(int par1)
    {
        IntHashMap.Entry[] aentry = this.slots;
        int j = aentry.length;

        if (j == 1073741824)
        {
            this.threshold = Integer.MAX_VALUE;
        }
        else
        {
            IntHashMap.Entry[] aentry1 = new IntHashMap.Entry[par1];
            this.copyTo(aentry1);
            this.slots = aentry1;
            this.threshold = (int)((float)par1 * this.growFactor);
        }
    }

    /**
     * Copies the hash slots to a new array
     */
    private void copyTo(IntHashMap.Entry[] par1ArrayOfIntHashMapEntry)
    {
        IntHashMap.Entry[] aentry = this.slots;
        int i = par1ArrayOfIntHashMapEntry.length;

        for (int j = 0; j < aentry.length; ++j)
        {
            IntHashMap.Entry entry = aentry[j];

            if (entry != null)
            {
                aentry[j] = null;
                IntHashMap.Entry entry1;

                do
                {
                    entry1 = entry.nextEntry;
                    int k = getSlotIndex(entry.slotHash, i);
                    entry.nextEntry = par1ArrayOfIntHashMapEntry[k];
                    par1ArrayOfIntHashMapEntry[k] = entry;
                    entry = entry1;
                }
                while (entry1 != null);
            }
        }
    }

    /**
     * Removes the specified object from the map and returns it
     */
    public Object removeObject(int par1)
    {
        this.keySet.remove(Integer.valueOf(par1));
        IntHashMap.Entry entry = this.removeEntry(par1);
        return entry == null ? null : entry.valueEntry;
    }

    /**
     * Removes the specified entry from the map and returns it
     */
    final IntHashMap.Entry removeEntry(int par1)
    {
        int j = computeHash(par1);
        int k = getSlotIndex(j, this.slots.length);
        IntHashMap.Entry entry = this.slots[k];
        IntHashMap.Entry entry1;
        IntHashMap.Entry entry2;

        for (entry1 = entry; entry1 != null; entry1 = entry2)
        {
            entry2 = entry1.nextEntry;

            if (entry1.hashEntry == par1)
            {
                ++this.versionStamp;
                --this.count;

                if (entry == entry1)
                {
                    this.slots[k] = entry2;
                }
                else
                {
                    entry.nextEntry = entry2;
                }

                return entry1;
            }

            entry = entry1;
        }

        return entry1;
    }

    /**
     * Removes all entries from the map
     */
    public void clearMap()
    {
        ++this.versionStamp;
        IntHashMap.Entry[] aentry = this.slots;

        for (int i = 0; i < aentry.length; ++i)
        {
            aentry[i] = null;
        }

        this.count = 0;
    }

    /**
     * Adds an object to a slot
     */
    private void insert(int par1, int par2, Object par3Obj, int par4)
    {
        IntHashMap.Entry entry = this.slots[par4];
        this.slots[par4] = new IntHashMap.Entry(par1, par2, par3Obj, entry);

        if (this.count++ >= this.threshold)
        {
            this.grow(2 * this.slots.length);
        }
    }

    static class Entry
        {
            /** The hash code of this entry */
            final int hashEntry;
            /** The object stored in this entry */
            Object valueEntry;
            /** The next entry in this slot */
            IntHashMap.Entry nextEntry;
            /** The id of the hash slot computed from the hash */
            final int slotHash;
            private static final String __OBFID = "CL_00001491";

            Entry(int par1, int par2, Object par3Obj, IntHashMap.Entry par4IntHashMapEntry)
            {
                this.valueEntry = par3Obj;
                this.nextEntry = par4IntHashMapEntry;
                this.hashEntry = par2;
                this.slotHash = par1;
            }

            /**
             * Returns the hash code for this entry
             */
            public final int getHash()
            {
                return this.hashEntry;
            }

            /**
             * Returns the object stored in this entry
             */
            public final Object getValue()
            {
                return this.valueEntry;
            }

            public final boolean equals(Object par1Obj)
            {
                if (!(par1Obj instanceof IntHashMap.Entry))
                {
                    return false;
                }
                else
                {
                    IntHashMap.Entry entry = (IntHashMap.Entry)par1Obj;
                    Integer integer = Integer.valueOf(this.getHash());
                    Integer integer1 = Integer.valueOf(entry.getHash());

                    if (integer == integer1 || integer != null && integer.equals(integer1))
                    {
                        Object object1 = this.getValue();
                        Object object2 = entry.getValue();

                        if (object1 == object2 || object1 != null && object1.equals(object2))
                        {
                            return true;
                        }
                    }

                    return false;
                }
            }

            public final int hashCode()
            {
                return IntHashMap.computeHash(this.hashEntry);
            }

            public final String toString()
            {
                return this.getHash() + "=" + this.getValue();
            }
        }
}