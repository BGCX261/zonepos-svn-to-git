package com.soulstrewn.zonepos.Objects;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * User: jeff
 * Date: Mar 6, 2009
 * Time: 9:32:14 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class Tab implements Serializable {
    public List<Order> orders;
    public boolean open;
    public int id;

    public Tab()
    {
        orders = new ArrayList<Order>();
        open = false;
    }

    public String getAggregateOrder()
    {
        Order o = new Order();
                o.id = "TAB";
                o.OType = Order.OrderType.BAR;
                for (Order oo : orders) {
                    o.Created = oo.Created;
                    for (Item i : oo.Contents) {
                        o.Contents.add(i);
                    }
                }

        return o.toString();
    }
}
