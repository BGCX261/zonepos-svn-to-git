package com.soulstrewn.zonepos.UI;

import com.soulstrewn.zonepos.Exchange;
import com.soulstrewn.zonepos.Objects.Item;
import com.soulstrewn.zonepos.Objects.Modifiers;
import com.soulstrewn.zonepos.Objects.Option;
import com.soulstrewn.zonepos.Objects.Order;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: jeff
 * Date: Mar 4, 2009
 * Time: 3:40:20 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class ZonePOSClient {
    private JPanel panel1;
    private JList OrderList;
    private JButton button1;
    private JButton button2;
    private JButton deliveryCallButton;
    private JButton recentOrdersButton;
    private JButton tabsButton;
    private JButton deliveriesButton;
    private JPanel CategoryPanel;
    private JPanel ItemPanel;
    private JPanel OptionsPanel;
    private JPanel ReceiptPanel;
    private JLabel InstLabel;
    private JTabbedPane ChooseArea;
    private JLabel lblTime;
    private JLabel TotalPrice;
    private JButton newOrderButton;
    private JButton btnPay;
    private JButton dupbutton;
    private JPanel AlcoholPanel;
    private JPanel FoodPanel;
    //private JScrollPane FoodPanel;
    private JFrame frame;
    private static List<Item> items; // actually, to be specific, these are categories.

    public static ZonePOSClient singleton;
    public static Exchange stub;
    public static Order temp;
    private static javax.swing.Timer timeTimer;
    private static javax.swing.Timer ordersTimer;
    private static int lastcat;
    private static int lastitem;
    private static ArrayList<Modifiers> mods;

    private static Item[] curdisp;

    ZonePOSClient()
    {
        singleton = this;


        try {
            items = stub.getAvailableItems();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        frame = new JFrame("com.soulstrewn.zonepos.UI.ZonePOSClient");

        temp = new Order();
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.pack();

        timeTimer = new javax.swing.Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                lblTime.setText(new Date().toString());
            }
        });
        UpdateOrderList();


        timeTimer.start();
        //ItemPanel.setVisible(false);
        //OptionsPanel.setVisible(false);

        UpdateMySelectors();


        frame.setVisible(true);

/*        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                // add button
                if (!OrderList.isSelectionEmpty())
                    RemoveItemAtIndex(OrderList.getSelectedIndex());
                AddItemFromUI();
                UpdateMySelectors();
            }
        });
        */
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                if (OrderList.isSelectionEmpty()) {
                    UpdateMySelectors();
                    return;
                }
                RemoveItemAtIndex(OrderList.getSelectedIndex());
                // remove button
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        OrderList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event)
            {
                if (OrderList.isSelectionEmpty())
                    return;
                if (event.getValueIsAdjusting())
                    PullItemFromIndex(OrderList.getSelectedIndex());
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        newOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                if (JOptionPane.showConfirmDialog(panel1,
                        "Are you sure you want to scrap this order?",
                        "Scrap Order?", JOptionPane.YES_NO_OPTION) == 0)
                {
                    UpdateMySelectors();
                    temp = new Order();
                    UpdateOrderList();
                }
            }

        });
        btnPay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                if (temp.Contents.size() == 0)
                    return;
                PaymentDialog dialog = new PaymentDialog(frame, temp);
                dialog.setAlwaysOnTop(true);
                dialog.setModal(true);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        tabsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                TabViewer dialog = new TabViewer(frame);
                dialog.setAlwaysOnTop(true);
                dialog.setModal(true);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.updateComponentTreeUI(frame);
        dupbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                Item i = new Item();
                Item oi = temp.Contents.get(OrderList.getSelectedIndex());
                i.Name = oi.Name;
                i.Price = oi.Price;
                for (Modifiers m : oi.Mods) {
                    i.Mods.add(m);
                }
                i.basedOn = oi.basedOn;
                i.basedOnCat = oi.basedOnCat;
                temp.Contents.add(i);
                UpdateOrderList();
            }
        });

    }

    void RemoveItemAtIndex(int i)
    {

        temp.Contents.remove(i);
        UpdateOrderList();

    }

    void PullItemFromIndex(int i)
    {
        //System.out.println("Selected: " + OrderList.getSelectedIndex());
        // set selection states inside of panels

        Item lookingfor = temp.Contents.get(i);

        UpdateMySelectors();

        if (items.get(lookingfor.basedOnCat).type.equals(Item.ItemTypes.ALCOHOL)) {
            ChooseArea.setSelectedIndex(0);
            return;
        }
        lastitem = lookingfor.basedOn;
        lastcat = lookingfor.basedOnCat;

        //SwitchItemList(lookingfor.basedOnCat);

        SwitchOptions(lookingfor.basedOnCat, lookingfor.basedOn);

        javax.swing.Timer te = new javax.swing.Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                ChooseArea.setSelectedIndex(2);
            }
        });
        te.setRepeats(false);
        te.start();

        ChooseArea.setSelectedIndex(1);

        ChooseArea.setEnabledAt(2, true);


    }

    void AddItemFromUI()
    {


        Item i2 = items.get(lastcat).SubItems.get(lastitem);
        Item i = Item.copyItem(i2);
        i.Mods = new ArrayList<Modifiers>();
        Modifiers m = new Modifiers();
        for (Modifiers om : mods)
            for (Option o : om.Options)
                m.Options.add(o);
        i.Mods.add(m);

        mods = new ArrayList<Modifiers>();
        i.basedOn = lastitem;
        i.basedOnCat = lastcat;
        lastitem = -1;
        lastcat = -1;
        temp.Contents.add(i);
        UpdateOrderList();


    }

    public void UpdateOrderList()
    {
        String[] listData = new String[temp.Contents.size()];

        OrderList.setCellRenderer(new OrderRenderer());
        OrderList.setListData(listData);
        OrderList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        TotalPrice.setText(String.format("$%3.2f", (float) temp.getOrderTotal() / 100));
    }


    public void UpdateMySelectors()
    {
        // AlcoholPanel.setVisible(false);
        //FoodPanel.setVisible(false);
        AlcoholPanel.removeAll();
               AlcoholPanel.setLayout(new BorderLayout());

               FoodPanel.removeAll();

               FoodPanel.setLayout(new BorderLayout());

/*               ChooseArea.setMaximumSize(new Dimension(475,2000));
               ChooseArea.setMinimumSize(new Dimension(475,200));
  */
               JPanel FoodInside = new JPanel();

               FoodInside.setLayout(new FlowLayout());

                JPanel AlcoholInside = new JPanel();
               FoodPanel.add(FoodInside, BorderLayout.CENTER);


        AlcoholInside.setLayout(new FlowLayout());
        AlcoholPanel.add(AlcoholInside, BorderLayout.CENTER);

        //AlcoholPanel.setMinimumSize(D);
        //FoodPanel.setMinimumSize(D);

        //AlcoholPanel.setMaximumSize(D2);
        //FoodPanel.setMaximumSize(D2);


        for (final Item i : items) {
            final int cat = items.indexOf(i);
            final boolean temp = i.type.equals(Item.ItemTypes.ALCOHOL);
            JButton nj = new JButton(i.Name);

            if (temp) {   // add all subitems
                for (final Item ii : i.SubItems) {
                    final int subitemnum = i.SubItems.indexOf(ii);
                    JButton njb = new JButton(ii.Name);
                    njb.setFont(new Font(null, 0, 22));
                    njb.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event)
                        {
                            Item iii = Item.copyItem(ii);
                            iii.basedOn = subitemnum;
                            iii.basedOnCat = cat;
                            ZonePOSClient.temp.Contents.add(iii);
                            UpdateOrderList();
                        }
                    });
                    AlcoholInside.add(njb);

                }
            } else {

                nj.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event)
                    {
                        SwitchItemList(i);
                    }
                });

            }
            nj.setFont(new Font(null, 0, 22));
            if (!i.type.equals(Item.ItemTypes.ALCOHOL)) {
                FoodInside.add(nj);
            }


        }

        /*

        CategoryPanel.removeAll();
        ItemPanel.removeAll();
        OptionsPanel.removeAll();
        ChooseArea.removeAll();
        ChooseArea.add("Category", CategoryPanel);
        ChooseArea.add("Item", ItemPanel);
        ChooseArea.add("Options", OptionsPanel);
        InstLabel.setText("First, Select A Category");

        ChooseArea.setTitleAt(1, "Item");
        ChooseArea.setEnabledAt(1, false);
        ChooseArea.setEnabledAt(2, false);
        ButtonGroup cbg = new ButtonGroup();

        CategoryPanel.setLayout(new GridLayout(items.size(), 1));

        */
        //AlcoholPanel.setVisible(true);
        //FoodPanel.setVisible(true);
        FoodInside.setBackground(new Color(50,50,50));
        AlcoholInside.setBackground(new Color(50,50,50));

        FoodPanel.updateUI();
        AlcoholPanel.updateUI();
        lastitem = -1;
        lastcat = -1;
    }

    void SwitchItemList(final Item i)
    {
        FoodPanel.setVisible(false);


               FoodPanel.removeAll();

               FoodPanel.setLayout(new BorderLayout());


               JPanel FoodInside = new JPanel();

               FoodInside.setLayout(new FlowLayout());

               FoodPanel.add(FoodInside, BorderLayout.CENTER);
        FoodInside.setBackground(new Color(50,50,50));

        Color red = new Color(210, 0, 0);
        JButton gb = new JButton("Go Back");
        gb.setBackground(red);
        gb.setForeground(new Color(255, 0, 0));
        gb.setFont(new Font(null, 0, 22));
        gb.setMinimumSize(new Dimension(290, 40));
        gb.setPreferredSize(new Dimension(290, 40));
        gb.setMaximumSize(new Dimension(290, 80));

        gb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                UpdateMySelectors();
                ChooseArea.setSelectedIndex(1);

            }
        });
           JLabel jl = new JLabel(i.Name);
        jl.setFont(new Font(null, 0, 22));
        JPanel LabelPanel = new JPanel();

        LabelPanel.setLayout(new FlowLayout());
        LabelPanel.add(jl);
        LabelPanel.setPreferredSize(new Dimension(400,30));
        LabelPanel.setBackground(new Color(50,50,50));
        jl.setForeground(new Color(255,255,255));


        FoodInside.add(LabelPanel);
        FoodInside.add(gb);
           

        final int catnum = items.indexOf(i);

        for (final Item ii : i.SubItems) {
            final int subitemnum = i.SubItems.indexOf(ii);
            JButton njb = new JButton(ii.Name);
            njb.setFont(new Font(null, 0, 22));
            njb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    if (union(ii.Mods, i.Mods).size() > 0)  // if we have mods
                    {
                        lastcat = catnum;
                        lastitem = subitemnum;
                        SwitchOptions(catnum, subitemnum);
                    } else {  // just add a button for it
                        Item iii = Item.copyItem(ii);
                        iii.basedOn = subitemnum;
                        iii.basedOnCat = catnum;
                        ZonePOSClient.temp.Contents.add(iii);
                        UpdateOrderList();
                        UpdateMySelectors();
                        ChooseArea.setSelectedIndex(1);
                    }


                }
            });
            FoodInside.add(njb);

        }


        FoodPanel.setVisible(true);

        /*
        ItemPanel.setVisible(false);
        ChooseArea.setEnabledAt(1, true);
        InstLabel.setText("Next, Select An Item");
        ChooseArea.setTitleAt(1, "Item");
        ChooseArea.setTitleAt(0, items.get(which).Name);
        ItemPanel.removeAll();
        OptionsPanel.removeAll();
        ButtonGroup ibg = new ButtonGroup();

        Item Category = items.get(which);

        ItemPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;

        int ii = 0;

        for (Item i : Category.SubItems) {
            final int pos = Category.SubItems.indexOf(i);
            final int sop = which;
            final String title = i.Name;
            JRadioButton nj;
            JLabel jl = new JLabel(i.Description);
            jl.setVerticalAlignment(JLabel.TOP);
            if (i.Price > 0)
                nj = new JRadioButton(i.Name + " (" + i.Price + ")", false);
            else
                nj = new JRadioButton(i.Name, false);


            ibg.add(nj);
            nj.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    SwitchOptions(sop, pos);
                }
            });
            nj.setFont(new Font(null, 0, 22));
            nj.setLayout(new FlowLayout());
            c.gridy = ii++;
            c.weighty = 0.3;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.LAST_LINE_START;

            ItemPanel.add(nj, c);
            /*
            c.gridy = ii++;
            c.weighty = 0.1;
            c.fill = GridBagConstraints.VERTICAL;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            ItemPanel.add(jl, c);


        }

        ItemPanel.setMaximumSize(new Dimension(100, 300));
        ItemPanel.setPreferredSize(new Dimension(100, 300));
        ItemPanel.setMinimumSize(new Dimension(100, 300));

        lastcat = which;
        ItemPanel.setVisible(true);
        mods = new ArrayList<Modifiers>();
        ChooseArea.setSelectedIndex(1);
                    */
    }



    void SwitchOptions(final int cat, final int item)
    {

        FoodPanel.setVisible(false);

               FoodPanel.removeAll();

               FoodPanel.setLayout(new BorderLayout());

               JPanel FoodInside = new JPanel();
               JScrollPane FoodInsideScroll = new JScrollPane(FoodInside);

               //FoodInside.setLayout(new FlowLayout());

                FoodInside.setLayout(new BoxLayout(FoodInside, BoxLayout.Y_AXIS));
               FoodPanel.add(FoodInsideScroll, BorderLayout.CENTER);

        FoodInside.setBackground(new Color(50,50,50));
        //  FoodPanel.setMinimumSize(D);
        // FoodPanel.setMaximumSize(D2);

        int aggHeight = 500;

        final Item itam = items.get(cat).SubItems.get(item);
        final Item caet = items.get(cat);


        List<Modifiers> mM = union(itam.Mods, caet.Mods);

        mods = new ArrayList<Modifiers>();

        for (Modifiers m : mM) {
            Modifiers t = new Modifiers();
            t.Name = m.Name;
            t.Type = m.Type;
            mods.add(t);
        }

        Color green = new Color(0, 210, 0);

        Color red = new Color(210, 0, 0);

        JPanel ButtonPanel = new JPanel();
        ButtonPanel.setLayout(new FlowLayout());

        JButton gb = new JButton("Go Back");
        gb.setBackground(red);
        gb.setForeground(new Color(255, 0, 0));

        gb.setFont(new Font(null, 0, 22));
        gb.setMinimumSize(new Dimension(175, 40));
        gb.setMaximumSize(new Dimension(175, 80));
        gb.setPreferredSize(new Dimension(175, 40));
        gb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                UpdateMySelectors();
                SwitchItemList(caet);
                ChooseArea.setSelectedIndex(1);

            }
        });

        JButton gb2 = new JButton("Add");
        gb2.setFont(new Font(null, 0, 22));
        gb2.setBackground(green);
        gb2.setForeground(new Color(0, 255, 0));

        gb2.setMinimumSize(new Dimension(175, 40));
        gb2.setPreferredSize(new Dimension(175, 40));
        gb2.setMaximumSize(new Dimension(175, 80));
        gb2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                              if (!OrderList.isSelectionEmpty())
                    RemoveItemAtIndex(OrderList.getSelectedIndex());
                AddItemFromUI();
                UpdateMySelectors();

            }
        });



        ButtonPanel.add(gb);
        ButtonPanel.add(gb2);

          JLabel jl = new JLabel(itam.Name);
        jl.setFont(new Font(null, 0, 22));
        JPanel LabelPanel = new JPanel();

        LabelPanel.setLayout(new FlowLayout());
        LabelPanel.add(jl);
        LabelPanel.setPreferredSize(new Dimension(400,30));


        FoodInside.add(ButtonPanel);
        FoodInside.add(LabelPanel);

        jl.setForeground(new Color(255,255,255));
                                   
        LabelPanel.setBackground(new Color(50,50,50));
        ButtonPanel.setBackground(new Color(50,50,50));




        for (final Modifiers m : mM) {

            JPanel f = new JPanel();
            f.setBorder(BorderFactory.createTitledBorder(m.Name));
            //f.setLayout(new BoxLayout(f,BoxLayout.Y_AXIS));
            f.setLayout(new GridLayout(m.Options.size()/2+1,2));
            //f.setLayout(new FlowLayout());
            f.setAlignmentY(JPanel.TOP_ALIGNMENT);
            // f.setPreferredSize(new Dimension(350,150));
            // f.setMaximumSize(new Dimension(400,450));
             // set max width 475
           // f.setMinimumSize(new Dimension(455,5));
            //f.setPreferredSize(new Dimension(455,150));


            f.updateUI();

            f.setName(m.Name);
            f.setBackground(new Color(255, 255, 0));
            final int n = mM.indexOf(m);
            ButtonGroup bg = new ButtonGroup();
            if (m.Type == Modifiers.types.one) {

                for (final Option o : m.Options) {

                    JRadioButton nj;
                    if (o.Price > 0)
                        nj = new JRadioButton(o.Name + "(" + o.Price + ")", false);
                    else
                        nj = new JRadioButton(o.Name, false);

                    //nj.setMaximumSize(new Dimension(200,100));

                    nj.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event)
                        {

                            OptionsClick(o, m, n); // omn nom nom

                        }

                    });
                    nj.setFont(new Font(null, 0, 18));

                    bg.add(nj);
                    f.add(nj);
                }

            } else {
                for (final Option o : m.Options) {

                    JCheckBox nj;
                    if (o.Price > 0)
                        nj = new JCheckBox(o.Name + "(" + o.Price + ")", false);
                    else
                        nj = new JCheckBox(o.Name, false);

                    //nj.setMaximumSize(new Dimension(200,100));

                    nj.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event)
                        {
                            OptionsClick(o, m, n); // omn nom nom
                        }

                    });
                    nj.setFont(new Font(null, 0, 18));

                    f.add(nj);
                }

            }
            aggHeight += f.getHeight();
            FoodInside.add(f);


        }

        lastitem = item;


        FoodPanel.setVisible(true);

        FoodInside.validate();
        

    }

    void OptionsClick(Option o, Modifiers t, int n)
    {

        Modifiers m = mods.get(n);
        o.CatName = m.Name;

        if (m.Type == Modifiers.types.one) {
            if (m.Options.contains(o)) {
                m.Options.clear();
            } else {
                m.Options.clear();
                m.Options.add(o);
            }

        } else {

            if (m.Options.contains(o)) {
                m.Options.remove(o);
            } else {
                m.Options.add(o);
            }

        }

    }

    public static <T> List<T> union(List<T> List1, List<T> List2)
    {
        List<T> temp = new ArrayList<T>();
        temp.addAll(List1);
        temp.addAll(List2);
        return temp;
    }

    public static void main(String[] args)
    {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1"); // todo: make config
            stub = (Exchange) registry.lookup("ZonePOS");

            items = stub.getAvailableItems();

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();

        }

        new ZonePOSClient();
    }


    private void createUIComponents()
    {
        // TODO: place custom component creation code here 
    }
}

class OrderRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean hasFocus)
    {

        JPanel panel = new JPanel();

        int count = 1;
        Item Itam = ZonePOSClient.temp.Contents.get(index);
        for (Modifiers m : Itam.Mods)
            count += m.Options.size();

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        JLabel njl = new JLabel(Itam.Name);
        JLabel njl2 = new JLabel(String.format("%.2f", (float)Itam.getTotalPrice()/100));

        njl.setFont(new Font(null, -1, 16));
        njl2.setFont(new Font(null, -1, 16));

        if (isSelected) {
            panel.setBackground(new Color(0, 0, 120));
            njl.setForeground(new Color(200, 200, 0));
            njl2.setForeground(njl.getForeground());
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        panel.add(njl, c);
        c.anchor = GridBagConstraints.PAGE_END;

        c.weightx = 0.1;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        panel.add(njl2, c);

        int i = 1;
        for (Modifiers m : Itam.Mods) {
            for (Option o : m.Options) {
                JLabel nj = new JLabel(o.Name);
                JLabel nj2 = new JLabel("");

                nj.setHorizontalAlignment(JLabel.RIGHT);
                c.fill = GridBagConstraints.HORIZONTAL;

                c.gridx = 0;
                c.gridy = i;
                c.weightx = 1.0;

                panel.add(nj, c);
                c.fill = GridBagConstraints.NONE;

                c.gridx = 1;
                c.gridy = i++;
                c.weightx = 0.1;

                panel.add(nj2, c);

                nj2.setForeground(njl.getForeground());
                nj.setForeground(njl.getForeground());


            }
        }


        return panel;

    }
}