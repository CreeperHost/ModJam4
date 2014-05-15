package net.minecraft.world;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules
{
    private TreeMap theGameRules = new TreeMap();
    private static final String __OBFID = "CL_00000136";

    public GameRules()
    {
        this.addGameRule("doFireTick", "true");
        this.addGameRule("mobGriefing", "true");
        this.addGameRule("keepInventory", "false");
        this.addGameRule("doMobSpawning", "true");
        this.addGameRule("doMobLoot", "true");
        this.addGameRule("doTileDrops", "true");
        this.addGameRule("commandBlockOutput", "true");
        this.addGameRule("naturalRegeneration", "true");
        this.addGameRule("doDaylightCycle", "true");
    }

    /**
     * Define a game rule and its default value.
     */
    public void addGameRule(String par1Str, String par2Str)
    {
        this.theGameRules.put(par1Str, new GameRules.Value(par2Str));
    }

    public void setOrCreateGameRule(String par1Str, String par2Str)
    {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(par1Str);

        if (value != null)
        {
            value.setValue(par2Str);
        }
        else
        {
            this.addGameRule(par1Str, par2Str);
        }
    }

    /**
     * Gets the string Game Rule value.
     */
    public String getGameRuleStringValue(String par1Str)
    {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(par1Str);
        return value != null ? value.getGameRuleStringValue() : "";
    }

    /**
     * Gets the boolean Game Rule value.
     */
    public boolean getGameRuleBooleanValue(String par1Str)
    {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(par1Str);
        return value != null ? value.getGameRuleBooleanValue() : false;
    }

    /**
     * Return the defined game rules as NBT.
     */
    public NBTTagCompound writeGameRulesToNBT()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.theGameRules.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            GameRules.Value value = (GameRules.Value)this.theGameRules.get(s);
            nbttagcompound.setString(s, value.getGameRuleStringValue());
        }

        return nbttagcompound;
    }

    /**
     * Set defined game rules from NBT.
     */
    public void readGameRulesFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        Set set = par1NBTTagCompound.func_150296_c();
        Iterator iterator = set.iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            String s1 = par1NBTTagCompound.getString(s);
            this.setOrCreateGameRule(s, s1);
        }
    }

    /**
     * Return the defined game rules.
     */
    public String[] getRules()
    {
        return (String[])this.theGameRules.keySet().toArray(new String[0]);
    }

    /**
     * Return whether the specified game rule is defined.
     */
    public boolean hasRule(String par1Str)
    {
        return this.theGameRules.containsKey(par1Str);
    }

    static class Value
        {
            private String valueString;
            private boolean valueBoolean;
            private int valueInteger;
            private double valueDouble;
            private static final String __OBFID = "CL_00000137";

            public Value(String par1Str)
            {
                this.setValue(par1Str);
            }

            /**
             * Set this game rule value.
             */
            public void setValue(String par1Str)
            {
                this.valueString = par1Str;
                this.valueBoolean = Boolean.parseBoolean(par1Str);

                try
                {
                    this.valueInteger = Integer.parseInt(par1Str);
                }
                catch (NumberFormatException numberformatexception1)
                {
                    ;
                }

                try
                {
                    this.valueDouble = Double.parseDouble(par1Str);
                }
                catch (NumberFormatException numberformatexception)
                {
                    ;
                }
            }

            /**
             * Gets the GameRule's value as String.
             */
            public String getGameRuleStringValue()
            {
                return this.valueString;
            }

            /**
             * Gets the GameRule's value as boolean.
             */
            public boolean getGameRuleBooleanValue()
            {
                return this.valueBoolean;
            }
        }
}