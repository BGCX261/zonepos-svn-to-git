package com.soulstrewn.zonepos.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.Serializable;

/**
 * User: jeff
 * Date: Mar 3, 2009
 * Time: 11:07:03 AM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class Order implements Serializable {

    public static enum OrderType { DELIVERY, PICKUP, BAR, TAB, OTHER }
    public static enum PaymentType{ CASH, CREDIT, CHECK, TAB, OTHER }
    public static enum Statuses { PENDING, OPEN, MID, BACK, TAB, CLOSE }

    public List<Item> Contents;

    public Statuses status;
    public OrderType OType;
    public PaymentType PType;

    public String[] DeliveryInfo;

    public Date Created;
    public String id;

    public int paddle_id;
    public int tab_id;

    public Order()
    {
        Contents = new ArrayList<Item>();
        Created = new Date();
        id = "#";//new Random().nextInt();
        DeliveryInfo = new String[10]; // 4 + custom
        status = Statuses.PENDING;

    }
    public int getOrderTotal()
    {
        int i = 0;
        int tp = 0;
        int pct = 0;
        for (Item s : Contents) {
            if (s.isDiscount)
            {
                if(s.isPercent)
                {
                    if (pct < s.Price)
                        pct = s.Price;
                    continue;
                }
                tp -= s.Price;
                continue;
            }
            tp += s.getTotalPrice();
        }
        if (pct > 0)
            tp = tp - (int)(tp * (pct / (float)100));
        return tp;
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        // receipt printers are 40 chars per line
        // 0123456789012345678901234567890123456789
        // ----------------------------------------
        //
        sb.append(String.format("----------------------------------------\n"));
        sb.append(String.format("Order %8.8s       %.19s\n", id, Created.toString()));
        sb.append(String.format("%-20.20s     %15.15s\n", PType, OType));
        sb.append(String.format("----------------------------------------\n"));

        if(OType == OrderType.DELIVERY)
        {
            for(String s : DeliveryInfo)
            {
                if (s.equals("")) continue;
                sb.append(String.format(" %-38.38s \n", s));
            }
            sb.append(String.format("----------------------------------------\n"));           
        }

        int pct = 0;
        String pct_desc = "";
        int Total = 0;
        for(Item i : Contents)
        {
            if (i.isDiscount)
            {

                if(i.isPercent)
                {
                    if (pct < i.Price)
                    {
                        pct = i.Price;
                        pct_desc = i.Name;
                    }
                    continue;
                }
                sb.append(String.format("***** %-24.24s % 9.2f\n", i.Name, -1 * (float)i.Price/100));

                Total -= i.Price;
                continue;
            }

            sb.append(String.format("%-30.30s % 9.2f\n", i.Name, (float)i.Price/100));
            Total += i.Price;

            String lastCat = "";
            for(Modifiers m : i.Mods)
            {
                for (Option o : m.Options)
                {

                    if (o.Price != 0)
                        sb.append(String.format("    * %27.27s %+6.2f\n", o.Name, (float)o.Price/100));
                    else
                        sb.append(String.format("    * %27.27s          \n", o.Name));

                    Total += o.Price;
                }                
            }
        }
        if(pct > 0)
        {
            sb.append(String.format("***** Discount: %11.11s %3d%%  %+6.2f\n", pct_desc, pct, -1*((float)Total*(pct/(float)100))/100));
            Total = Total - (int)(Total*((float)pct/100));
        }

        if (this.getOrderTotal() != Total)
            System.out.println("Order totals don't agree...");

        sb.append(String.format("----------------------------------------\n"));
        sb.append(String.format("Total: %33.2f\n", (float)Total/100));        
        sb.append(String.format("----------------------------------------\n"));


        return sb.toString();
    }
}
