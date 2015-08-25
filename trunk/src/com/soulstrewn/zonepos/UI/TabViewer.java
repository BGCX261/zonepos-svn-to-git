package com.soulstrewn.zonepos.UI;

import com.soulstrewn.zonepos.Objects.Tab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

/**
 * User: jeff
 * Date: Mar 6, 2009
 * Time: 10:04:19 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class TabViewer extends JDialog {
    private JPanel panel1;
    private JTextArea orderArea;
    private JButton backButton;
    private JButton PROCESSButton;
    private JPanel button_area;
    private static Tab[] tabs;
    public static int last = 0;
    public static TabViewer singleton;
    private JFrame owner;

    public TabViewer(JFrame own)
    {

         super(own, true);
        setUndecorated(true);

        this.setLocationRelativeTo(own);

        this.setLocation(0, 0);

        setContentPane(panel1);

        owner = own;
        singleton = this;
        try {
            tabs = ZonePOSClient.stub.getOpenTabs();
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        button_area.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        Font f = new Font(null, -1, 26);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 5;


        c.gridwidth = 1;

        final Tab[] tabs;
                Tab[] tabs1;
                try {
                    tabs1 = ZonePOSClient.stub.getOpenTabs();
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    tabs1 = new Tab[50];
                }
                tabs = tabs1;
        
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 10; y++) {
                final int num = y * 5 + x + 1;

                JButton b = new JButton(String.valueOf(num));
                if (tabs[num-1].open)
                {
                    b.setForeground(new Color(0, 200, 0));
                    b.setBackground(new Color(200, 0, 200));

                }
                else
                    b.setEnabled(false); 
                b.setFont(f);
                c.gridx = x;
                c.gridy = y + 1;

                button_area.add(b, c);

                b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event)
                    {
                        last = num-1;
                        orderArea.setText(tabs[num-1].getAggregateOrder());
                    }
                });

            }
        }
        JButton b;


        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                dispose();
            }
        });
        PROCESSButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                tabs[last].id = last;
                
                if(!tabs[last].open)
                return;
                try {
                    ZonePOSClient.stub.ClearTab(last);
                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                PaymentDialog dialog = new PaymentDialog(null, tabs[last]);
                dialog.setAlwaysOnTop(true);
                dialog.setModal(true);
                dialog.pack();
                dialog.setVisible(true);
                dispose();
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
}
