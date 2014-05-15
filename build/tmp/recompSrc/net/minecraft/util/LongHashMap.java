package net.minecraft.util;

public class LongHashMap
{
    /** the array of all elements in the hash */
    private transient LongHashMap.Entry[] hashArray = new LongHashMap.Entry[16];
    /** the number of elements in the hash array */
    private transient int numHashElements;
    /**
     * the maximum amount of elements in the hash (probably 3/4 the size due to meh hashing function)
     */
    private int capacity = 12;
    /**
     * percent of the hasharray that can be used without hash colliding probably
     */
    private final float percentUseable = 0.75F;
    /** count of times elements have been added/removed */
    private transient volatile int modCount;
    private static final String __OBFID = "CL_00001492";

    /**
     * returns the hashed key given the original key
     */
    private static int getHashedKey(long par0)
    {
        /**
         * the hash function
         */
        return hash((int)(par0 ^ par0 >>> 32));
    }

    /**
     * the hash function
     */
    private static int hash(int par0)
    {
        par0 ^= par0 >>> 20 ^ par0 >>> 12;
        return par0 ^ par0 >>> 7 ^ par0 >>> 4;
    }

    /**
     * gets the index in the hash given the array length and the hashed key
     */
    private static int getHashIndex(int par0, int par1)
    {
        return par0 & par1 - 1;
    }

    public int getNumHashElements()
    {
        return this.numHashElements;
    }

    /**
     * get the value from the map given the key
     */
    public Object getValueByKey(long par1)
    {
        int j = getHashedKey(par1);

        for (LongHashMap.Entry entry = this.hashArray[getHashIndex(j, this.hashArray.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == par1)
            {
                return entry.value;
            }
        }

        return null;
    }

    public boolean containsItem(long par1)
    {
        return this.getEntry(par1) != null;
    }

    final LongHashMap.Entry getEntry(long par1)
    {
        int j = getHashedKey(par1);

        for (LongHashMap.Entry entry = this.hashArray[getHashIndex(j, this.hashArray.length)]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == par1)
            {
                return entry;
            }
        }

        return null;
    }

    /**
     * Add a key-value pair.
     */
    public void add(long par1, Object par3Obj)
    {
        int j = getHashedKey(par1);
        int k = getHashIndex(j, this.hashArray.length);

        for (LongHashMap.Entry entry = this.hashArray[k]; entry != null; entry = entry.nextEntry)
        {
            if (entry.key == par1)
            {
                entry.value = par3Obj;
                return;
            }
        }

        ++this.modCount;
        this.createKey(j, par1, par3Obj, k);
    }

    /**
     * resizes the table
     */
    private void resizeTable(int par1)
    {
        LongHashMap.Entry[] aentry = this.hashArray;
        int j = aentry.length;

        if (j == 1073741824)
        {
            this.capacity = Integer.MAX_VALUE;
        }
        else
        {
            LongHashMap.Entry[] aentry1 = new LongHashMap.Entry[par1];
            this.copyHashTableTo(aentry1);
            this.hashArray = aentry1;
            this.capacity = (int)((float)par1 * this.percentUseable);
        }
    }

    /**
     * copies the hash table to the specified array
     */
    private void copyHashTableTo(LongHashMap.Entry[] par1ArrayOfLongHashMapEntry)
    {
        LongHashMap.Entry[] aentry = this.hashArray;
        int i = par1ArrayOfLongHashMapEntry.length;

        for (int j = 0; j < aentry.length; ++j)
        {
            LongHashMap.Entry entry = aentry[j];

            if (entry != null)
            {
                aentry[j] = null;
                LongHashMap.Entry entry1;

                do
                {
                    entry1 = entry.nextEntry;
                    int k = getHashIndex(entry.hash, i);
                    entry.nextEntry = par1ArrayOfLongHashMapEntry[k];
                    par1ArrayOfLongHashMapEntry[k] = entry;
                    entry = entry1;
                }
                while (entry1 != null);
            }
        }
    }

    /**
     * calls the removeKey method and returns removed object
     */
    public Object remove(long par1)
    {
        LongHashMap.Entry entry = this.removeKey(par1);
        return entry == null ? null : entry.value;
    }

    /**
     * removes the key from the hash linked list
     */
    final LongHashMap.Entry removeKey(long par1)
    {
        int j = getHashedKey(par1);
        int k = getHashIndex(j, this.hashArray.length);
        LongHashMap.Entry entry = this.hashArray[k];
        LongHashMap.Entry entry1;
        LongHashMap.Entry entry2;

        for (entry1 = entry; entry1 != null; entry1 = entry2)
        {
            entry2 = entry1.nextEntry;

            if (entry1.key == par1)
            {
                ++this.modCount;
                --this.numHashElements;

                if (entry == entry1)
                {
                    this.hashArray[k] = entry2;
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
     * creates the key in the hash table
     */
    private void createKey(int par1, long par2, Object par4Obj, int par5)
    {
        LongHashMap.Entry entry = this.hashArray[par5];
        this.hashArray[par5] = new LongHashMap.Entry(par1, par2, par4Obj, entry);

        if (this.numHashElements++ >= this.capacity)
        {
            this.resizeTable(2 * this.hashArray.length);
        }
    }

    static class Entry
        {
            /**
             * the key as a long (for playerInstances it is the x in the most significant 32 bits and then y)
             */
            final long key;
            /** the value held by the hash at the specified key */
            Object value;
            /** the next hashentry in the table */
            LongHashMap.Entry nextEntry;
            final int hash;
            private static final String __OBFID = "CL_00001493";

            Entry(int par1, long par2, Object par4Obj, LongHashMap.Entry par5LongHashMapEntry)
            {
                this.value = par4Obj;
                this.nextEntry = par5LongHashMapEntry;
                this.key = par2;
                this.hash = par1;
            }

            public final long getKey()
            {
                return this.key;
            }

            public final Object getValue()
            {
                return this.value;
            }

            public final boolean equals(Object par1Obj)
            {
                if (!(par1Obj instanceof LongHashMap.Entry))
                {
                    return false;
                }
                else
                {
                    LongHashMap.Entry entry = (LongHashMap.Entry)par1Obj;
                    Long olong = Long.valueOf(this.getKey());
                    Long olong1 = Long.valueOf(entry.getKey());

                    if (olong == olong1 || olong != null && olong.equals(olong1))
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
                return LongHashMap.getHashedKey(this.key);
            }

            public final String toString()
            {
                return this.getKey() + "=" + this.getValue();
            }
        }
}