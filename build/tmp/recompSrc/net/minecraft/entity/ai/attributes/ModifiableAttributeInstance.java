package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance
{
    /** The BaseAttributeMap this attributeInstance can be found in */
    private final BaseAttributeMap attributeMap;
    /** The Attribute this is an instance of */
    private final IAttribute genericAttribute;
    private final Map mapByOperation = Maps.newHashMap();
    private final Map mapByName = Maps.newHashMap();
    private final Map mapByUUID = Maps.newHashMap();
    private double baseValue;
    private boolean needsUpdate = true;
    private double cachedValue;
    private static final String __OBFID = "CL_00001567";

    public ModifiableAttributeInstance(BaseAttributeMap par1BaseAttributeMap, IAttribute par2Attribute)
    {
        this.attributeMap = par1BaseAttributeMap;
        this.genericAttribute = par2Attribute;
        this.baseValue = par2Attribute.getDefaultValue();

        for (int i = 0; i < 3; ++i)
        {
            this.mapByOperation.put(Integer.valueOf(i), new HashSet());
        }
    }

    /**
     * Get the Attribute this is an instance of
     */
    public IAttribute getAttribute()
    {
        return this.genericAttribute;
    }

    public double getBaseValue()
    {
        return this.baseValue;
    }

    public void setBaseValue(double par1)
    {
        if (par1 != this.getBaseValue())
        {
            this.baseValue = par1;
            this.flagForUpdate();
        }
    }

    public Collection getModifiersByOperation(int par1)
    {
        return (Collection)this.mapByOperation.get(Integer.valueOf(par1));
    }

    public Collection func_111122_c()
    {
        HashSet hashset = new HashSet();

        for (int i = 0; i < 3; ++i)
        {
            hashset.addAll(this.getModifiersByOperation(i));
        }

        return hashset;
    }

    /**
     * Returns attribute modifier, if any, by the given UUID
     */
    public AttributeModifier getModifier(UUID par1UUID)
    {
        return (AttributeModifier)this.mapByUUID.get(par1UUID);
    }

    public void applyModifier(AttributeModifier par1AttributeModifier)
    {
        if (this.getModifier(par1AttributeModifier.getID()) != null)
        {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        else
        {
            Object object = (Set)this.mapByName.get(par1AttributeModifier.getName());

            if (object == null)
            {
                object = new HashSet();
                this.mapByName.put(par1AttributeModifier.getName(), object);
            }

            ((Set)this.mapByOperation.get(Integer.valueOf(par1AttributeModifier.getOperation()))).add(par1AttributeModifier);
            ((Set)object).add(par1AttributeModifier);
            this.mapByUUID.put(par1AttributeModifier.getID(), par1AttributeModifier);
            this.flagForUpdate();
        }
    }

    private void flagForUpdate()
    {
        this.needsUpdate = true;
        this.attributeMap.addAttributeInstance(this);
    }

    public void removeModifier(AttributeModifier par1AttributeModifier)
    {
        for (int i = 0; i < 3; ++i)
        {
            Set set = (Set)this.mapByOperation.get(Integer.valueOf(i));
            set.remove(par1AttributeModifier);
        }

        Set set1 = (Set)this.mapByName.get(par1AttributeModifier.getName());

        if (set1 != null)
        {
            set1.remove(par1AttributeModifier);

            if (set1.isEmpty())
            {
                this.mapByName.remove(par1AttributeModifier.getName());
            }
        }

        this.mapByUUID.remove(par1AttributeModifier.getID());
        this.flagForUpdate();
    }

    @SideOnly(Side.CLIENT)
    public void removeAllModifiers()
    {
        Collection collection = this.func_111122_c();

        if (collection != null)
        {
            ArrayList arraylist = new ArrayList(collection);
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext())
            {
                AttributeModifier attributemodifier = (AttributeModifier)iterator.next();
                this.removeModifier(attributemodifier);
            }
        }
    }

    public double getAttributeValue()
    {
        if (this.needsUpdate)
        {
            this.cachedValue = this.computeValue();
            this.needsUpdate = false;
        }

        return this.cachedValue;
    }

    private double computeValue()
    {
        double d0 = this.getBaseValue();
        AttributeModifier attributemodifier;

        for (Iterator iterator = this.getModifiersByOperation(0).iterator(); iterator.hasNext(); d0 += attributemodifier.getAmount())
        {
            attributemodifier = (AttributeModifier)iterator.next();
        }

        double d1 = d0;
        Iterator iterator1;
        AttributeModifier attributemodifier1;

        for (iterator1 = this.getModifiersByOperation(1).iterator(); iterator1.hasNext(); d1 += d0 * attributemodifier1.getAmount())
        {
            attributemodifier1 = (AttributeModifier)iterator1.next();
        }

        for (iterator1 = this.getModifiersByOperation(2).iterator(); iterator1.hasNext(); d1 *= 1.0D + attributemodifier1.getAmount())
        {
            attributemodifier1 = (AttributeModifier)iterator1.next();
        }

        return this.genericAttribute.clampValue(d1);
    }
}