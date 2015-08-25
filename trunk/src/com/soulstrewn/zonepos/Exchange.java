package com.soulstrewn.zonepos;

import com.soulstrewn.zonepos.Objects.Item;
import com.soulstrewn.zonepos.Objects.Order;
import com.soulstrewn.zonepos.Objects.Tab;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/**
 * User: jeff
 * Date: Mar 3, 2009
 * Time: 10:55:07 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public interface Exchange extends Remote {
    public List<Order> getOrders() throws RemoteException;
    public List<Order> getOrders(Date start, Date end) throws RemoteException;

    public boolean addOrder(Order o) throws RemoteException;
    public boolean addOrder(Order o, int tab) throws RemoteException;
    public boolean modOrder(Order o, Order new_o) throws RemoteException;

    public boolean processCreditCard(boolean swiped, String track1, String track2, String number, String expdate, int amount) throws RemoteException;

    public List<Item> getAvailableItems() throws RemoteException;
    public boolean addItemToDatabase(Item c, Item i) throws RemoteException;
    public boolean removeItemFromDatabase(Item i) throws RemoteException;
    public boolean saveDatabase() throws RemoteException;

    public Tab[] getOpenTabs() throws RemoteException;
    public void ClearTab(int tabid) throws RemoteException;

}
