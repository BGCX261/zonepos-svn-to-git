package com.soulstrewn.zonepos.Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: jeff
 * Date: Mar 2, 2009
 * Time: 6:20:48 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */

public class Item implements Serializable {
    public String Name;
    public String Description;

    public static enum ItemTypes {FOOD, ALCOHOL, OTHER}

    public ItemTypes type = ItemTypes.FOOD;
    
    public int Price; // in cents, of course. or percent.
    public List<Item> SubItems; // if we have subitems, we're a category, and we aren't a valid entry
    public List<Modifiers> Mods;

    public boolean isDiscount;
    public boolean isPercent;

    public int basedOn;
    public int basedOnCat; // for keeping track of derivative items

    public Item()
    {
        SubItems = new ArrayList<Item>();
        Mods = new ArrayList<Modifiers>();
    }

    public static Item copyItem(Item i) // deeeeeeep copy
    {
        Item ni = new Item();
        ni.Name = i.Name;
        ni.Description = i.Description;
        ni.type = i.type;
        ni.Price = i.Price;

        for(Item ii : i.SubItems)
            ni.SubItems.add(copyItem(ii));

        for(Modifiers m : i.Mods)
            ni.Mods.add(m);

        ni.isPercent = i.isPercent;
        ni.isDiscount = i.isDiscount;
        ni.basedOn = i.basedOn;
        ni.basedOnCat = i.basedOnCat;

        return ni;

    }

    public int getTotalPrice()
    {
        int i = 0;
        i += Price;
        for(Modifiers m : Mods)
        for(Option o : m.Options)
        {
            i+=o.Price;
        }

        return i;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if(SubItems.size()>0)
            sb.append("\n").append(Name).append("\n[").append(Description).append("]");
        else
        {
            if(Price > 0)
                sb.append("\t").append(Name).append("\n\t\t").append(Description).append("\t(").append((float)Price/100).append(")\n");
            else
                sb.append("\t").append(Name).append("\n\t\t").append(Description).append("\n");
        }
        for(Modifiers m : Mods)
        {
            sb.append("\n\t\t").append(m.Name).append('(').append(m.Type).append(")\n\t\t\t");
            for(Option o : m.Options)
            {
                if(o.Price > 0)
                {
                    sb.append(o.Name).append(" (").append((float)o.Price/100).append(')').append(", ");
                    
                } else {
                    sb.append(o.Name).append(", ");

                }
            }
        }
        sb.append("\n\n");        
        for(Item i : SubItems)
        {
            sb.append(i);
        }

        return sb.toString();

    }
}
