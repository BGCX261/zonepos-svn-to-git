package com.soulstrewn.zonepos;

import com.soulstrewn.zonepos.Objects.Item;
import com.soulstrewn.zonepos.Objects.Order;
import com.soulstrewn.zonepos.Objects.Tab;
import com.soulstrewn.zonepos.Utility.CreditCard;
import com.soulstrewn.zonepos.Utility.Database;
import com.soulstrewn.zonepos.Utility.Log;

import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * User: jeff
 * Date: Mar 3, 2009
 * Time: 9:17:48 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class ServerMain implements Exchange {
    static List<Order> Orders;
    static Tab[] tabs = new Tab[50];

    ServerMain()
    {

        Log.Log(Log.LogLevel.NOTE, "\t[    ] Loading Items...");

        new Database("items.xml");
        Log.Log(Log.LogLevel.NOTE, "\t[*   ] Items loaded.");


        /*Log.Log(Log.LogLevel.NOTE, "\t[*  ] Checking License Data");

        FileInputStream fis = new FileInputStream("notapos.license");
        ObjectInputStream ois = new ObjectInputStream(fis);
        License l = (License) ois.readObject();

        if (l.business_name.equals(config.getString("license.business_name"))) {
            Log.Log(Log.LogLevel.NOTE, "\t[*  ] \tBusiness Name Matches");
        } else {
            Log.Log(LogLevel.WARNING, "\t[*  ] \tBusiness Name Mis-match, exiting");
            System.exit(-1);
        }

        if (l.contact_number.equals(config.getString("license.contact_number"))) {
            Log.Log(LogLevel.NOTE, "\t[*  ] \tContact Number Matches");
        } else {
            Log.Log(LogLevel.WARNING, "\t[*  ] \tContact Number Mis-match, exiting");
            System.exit(-1);
        }

        if (l.expiry.after(new Date())) {
            Log.Log(LogLevel.NOTE, "\t[*  ] \tValid Expiration Date: " + l.expiry);
        } else {
            Log.Log(LogLevel.WARNING, "\t[*  ] \tLicense has expired. (expired on " + l.expiry + ")");
            System.exit(-1);
        }

        Log.Log(LogLevel.NOTE, "\t[*  ] \tLicensed for " + l.location_limit + " location(s), " + l.terminal_limit + " terminal(s).");
        */

        /*
     Log.Log(LogLevel.NOTE, "\t[** ] Checking Database Connection");

     try {
         Class.forName(config.getString("database.connector"));
         conn = DriverManager.getConnection(config.getString("database.url"));

         Log.Log(LogLevel.NOTE, "\t[** ] \tDatabase Connection Successful (" + conn.toString() + ")");

     } catch (Exception E) {
         Log.Log(LogLevel.CRITICAL, "\t[** ] Exception thrown while connecting to database: " + E.toString());
     }
         \
        */
        Log.Log(Log.LogLevel.NOTE, "\t[*   ] Starting Network... ");

        try {
            ServerMain obj = this;
            Exchange stub = (Exchange) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            registry.rebind("ZonePOS", stub);

            Log.Log(Log.LogLevel.NOTE, "\t[**  ] Ready and Listening. ");
        } catch (Exception e) {
            Log.Log(Log.LogLevel.NOTE, "\t[!!  ] Failure " + e.toString());
            System.exit(-1);
        }
        Log.Log(Log.LogLevel.NOTE, "\t[**  ] Allocating Orders...");

        Orders = new ArrayList<Order>();
        for(int i = 0; i < 50; i++)
        {
            tabs[i] = new Tab();
            tabs[i].open = false;
        }
        Log.Log(Log.LogLevel.NOTE, "\t[*** ] Order Allocation Complete.");


        Log.Log(Log.LogLevel.NOTE, "\t[*** ] Re-saving database... ");

        try {
            saveDatabase();
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void main(String[] args)
    {
        Log.Log(Log.LogLevel.NOTE, "ZonePOS Server " + Log.Version + " starting up.");
        ServerMain myMain = new ServerMain();
        Log.Log(Log.LogLevel.NOTE, "ZonePOS Server " + Log.Version + " started.");

    }

    public List<Order> getOrders() throws RemoteException
    {
        Log.Log(Log.LogLevel.NOTE, "\t[***] Client Asked For All Orders ");
        List<Order> t = new ArrayList<Order>();
        for (Order o : Orders) {
            t.add(o);
        }
        return t;
    }

    public List<Order> getOrders(Date start, Date end) throws RemoteException
    {
        Log.Log(Log.LogLevel.NOTE, "\t[***] Client Asked For Orders between " + start + " and " + end);
        List<Order> t = new ArrayList<Order>();
        for (Order o : Orders) {
           if(o.Created.before(end) && o.Created.after(start))
            t.add(o);
        }
        return t;
    }

    public boolean addOrder(Order o) throws RemoteException
    {
        Log.Log(Log.LogLevel.NOTE, "\t[****] Received New Order ");
        Log.Log(Log.LogLevel.NONE, '\n'+ o.toString());
        Orders.add(o);
        return true;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean addOrder(Order o, int tab) throws RemoteException
    {
        try { tabs[tab].orders.add(o);
        tabs[tab].open = true;} 
        catch ( Exception E) {
            return false;
        }
        return true;
    }

    public boolean modOrder(Order o, Order no) throws RemoteException
    {

        Orders.remove(o);
        Orders.add(no);
        return true;
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean processCreditCard(boolean swiped, String track1, String track2, String number, String expdate, int amount) throws RemoteException
    {

        Log.Log(Log.LogLevel.NOTE, "\t[****] Client ran credit card ");

        return CreditCard.runCard(swiped, track1, track2, number, expdate, amount);
        //return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Item> getAvailableItems() throws RemoteException
    {
        Log.Log(Log.LogLevel.NOTE, "\t[****] Client Asked For All Items ");
        return Database.root.SubItems;
    }

    public boolean addItemToDatabase(Item c, Item i) throws RemoteException
    {
        if (c == null) // category
        {
            try {
                Database.root.SubItems.add(i);
                Log.Log(Log.LogLevel.NOTE, "\t[****] Added category to database ");

                return true;
            } catch (Exception e) {
                return false;
            }

        } else {
            try {
                List<Item> categories = Database.root.SubItems;
                categories.get(categories.indexOf(c)).SubItems.add(i);
                Log.Log(Log.LogLevel.NOTE, "\t[****] Added item to database ");

                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public boolean removeItemFromDatabase(Item i) throws RemoteException
    {
        try {
            for (Item e : Database.root.SubItems)
                if (e.equals(i)) {
                    Database.root.SubItems.remove(i);
                    Log.Log(Log.LogLevel.WARNING, "\t[!!!!] Removed category from database, and all items beneath it ");
                    return true;
                }

            for (Item ii : i.SubItems) {
                if (ii.equals(i)) {
                    i.SubItems.remove(i);
                    Log.Log(Log.LogLevel.NOTE, "\t[*!!*] Removed item from database");
                    return true;

                }
            }
            return true;
        } catch (Exception E) {
            return false;
        }
    }

    public boolean saveDatabase() throws RemoteException
    {
        FileOutputStream FOS = null;
        try {
            FOS = new FileOutputStream("items.xml");
            Database.SaveDatabase(FOS, Database.root);
            Log.Log(Log.LogLevel.NOTE, "\t[****] Database Saving Complete. ");

        } catch (Exception e) {
            Log.Log(Log.LogLevel.CRITICAL, "\t[!!!!] Error Saving Database! ");

            return false;
        }

        return true;


    }

    public Tab[] getOpenTabs() throws RemoteException
    {
        Log.Log(Log.LogLevel.NOTE, "\t[****] Client asked for tabs. ");
        
        return tabs;
    }

    public void ClearTab(int tabid) throws RemoteException
    {
        tabs[tabid] = new Tab();
    }
}
