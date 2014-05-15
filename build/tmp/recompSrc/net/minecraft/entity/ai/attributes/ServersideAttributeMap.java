package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap
{
    private final Set attributeInstanceSet = Sets.newHashSet();
    protected final Map descriptionToAttributeInstanceMap = new LowerStringMap();
    private static final String __OBFID = "CL_00001569";

    public ModifiableAttributeInstance getAttributeInstance(IAttribute par1Attribute)
    {
        return (ModifiableAttributeInstance)super.getAttributeInstance(par1Attribute);
    }

    public ModifiableAttributeInstance getAttributeInstanceByName(String par1Str)
    {
        IAttributeInstance iattributeinstance = super.getAttributeInstanceByName(par1Str);

        if (iattributeinstance == null)
        {
            iattributeinstance = (IAttributeInstance)this.descriptionToAttributeInstanceMap.get(par1Str);
        }

        return (ModifiableAttributeInstance)iattributeinstance;
    }

    /**
     * Registers an attribute with this AttributeMap, returns a modifiable AttributeInstance associated with this map
     */
    public IAttributeInstance registerAttribute(IAttribute par1Attribute)
    {
        if (this.attributesByName.containsKey(par1Attribute.getAttributeUnlocalizedName()))
        {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        else
        {
            ModifiableAttributeInstance modifiableattributeinstance = new ModifiableAttributeInstance(this, par1Attribute);
            this.attributesByName.put(par1Attribute.getAttributeUnlocalizedName(), modifiableattributeinstance);

            if (par1Attribute instanceof RangedAttribute && ((RangedAttribute)par1Attribute).getDescription() != null)
            {
                this.descriptionToAttributeInstanceMap.put(((RangedAttribute)par1Attribute).getDescription(), modifiableattributeinstance);
            }

            this.attributes.put(par1Attribute, modifiableattributeinstance);
            return modifiableattributeinstance;
        }
    }

    public void addAttributeInstance(ModifiableAttributeInstance par1ModifiableAttributeInstance)
    {
        if (par1ModifiableAttributeInstance.getAttribute().getShouldWatch())
        {
            this.attributeInstanceSet.add(par1ModifiableAttributeInstance);
        }
    }

    public Set getAttributeInstanceSet()
    {
        return this.attributeInstanceSet;
    }

    public Collection getWatchedAttributes()
    {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = this.getAllAttributes().iterator();

        while (iterator.hasNext())
        {
            IAttributeInstance iattributeinstance = (IAttributeInstance)iterator.next();

            if (iattributeinstance.getAttribute().getShouldWatch())
            {
                hashset.add(iattributeinstance);
            }
        }

        return hashset;
    }
}