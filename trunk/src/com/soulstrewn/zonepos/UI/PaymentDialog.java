package com.soulstrewn.zonepos.UI;

import com.soulstrewn.zonepos.Objects.Item;
import com.soulstrewn.zonepos.Objects.Order;
import com.soulstrewn.zonepos.Objects.Tab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

public class PaymentDialog extends JDialog {
    private JPanel contentPane;
    private JLabel lblTotal;
    private JPanel topPanel;
    private JTextArea orderTextArea;
    private JButton OOOPSButton;
    private Order myOrder;
    String tenderedMoney = "";

    private enum States {
        DESTINATION, PAYMENT_TYPE, CASH_END, PADDLE_NUMBER, TAB_SELECT, DELIVERY_INFO, CREDIT_CARD
    }

    States curState = States.DESTINATION;


    public PaymentDialog(JFrame owner, Order o)
    {
        super(owner, true);
        setUndecorated(true);

        myOrder = o;

        this.setLocationRelativeTo(owner);

        this.setLocation(0, 0);
        orderTextArea.setText(o.toString());
        orderTextArea.enableInputMethods(false);
        lblTotal.setText(String.format("$%.2f", (float) myOrder.getOrderTotal() / 100));

        setContentPane(contentPane);
        UpdateUI();


        OOOPSButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                if (curState.equals(States.DESTINATION))
                {
                    dispose();
                }
                curState = States.DESTINATION;
                UpdateUI();

            }
        });
    }

    public PaymentDialog(JFrame owner, Tab t)
    {
        super(owner, true);

        Order o = new Order();
        o.id = "TAB";
        o.OType = Order.OrderType.BAR;
        for (Order oo : t.orders) {
            o.Created = oo.Created;
            for (Item i : oo.Contents) {
                o.Contents.add(i);
            }
        }

        setUndecorated(true);

        myOrder = o;
        myOrder.tab_id = t.id;

        this.setLocationRelativeTo(owner);

        this.setLocation(0, 0);
        orderTextArea.setText(o.toString());
        orderTextArea.enableInputMethods(false);
        lblTotal.setText(String.format("$%.2f", (float) myOrder.getOrderTotal() / 100));

        setContentPane(contentPane);
        UpdateUI();
        OOOPSButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                if (curState.equals(States.DESTINATION))
                {
                    try {       // add it back
                        ZonePOSClient.stub.addOrder(myOrder, myOrder.tab_id);
                    } catch (RemoteException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    dispose();
                }
                curState = States.DESTINATION;
                UpdateUI();

            }
        });
    }

    void UpdateUI()
    {
        topPanel.setVisible(false);
        topPanel.removeAll();
        orderTextArea.setText(myOrder.toString());

        topPanel.setLayout(new GridBagLayout());

        switch (curState) {
            case CREDIT_CARD:
                CreateCreditCardForm();
                break;
            case DELIVERY_INFO:
                break;
            case DESTINATION:
                CreateDestinationForm();
                break;
            case PAYMENT_TYPE:
                CreatePaymentForm();
                break;
            case CASH_END:
                CreateCashForm();
                break;
            case TAB_SELECT:
            case PADDLE_NUMBER:
                OOOPSButton.setEnabled(false);
                CreateSelectionForm();
                break;
        }
        topPanel.setVisible(true);


    }

    void CreateCreditCardForm()
    {
        GridBagConstraints c = new GridBagConstraints();
        Font f = new Font(null, -1, 26);
        c.fill = GridBagConstraints.HORIZONTAL;

        final JLabel jl = new JLabel("Scan card now:");
        final JTextField jtf = new JTextField();
        jtf.setForeground(new Color(255, 255, 255));
        jtf.setBackground(new Color(255, 255, 255));
        jtf.setOpaque(true);


        jtf.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event)
            {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    jtf.setEnabled(false);
                    jtf.setBackground(new Color(0, 200, 0));
                    jtf.setForeground(new Color(0, 200, 0));
                    jtf.setDisabledTextColor(new Color(0, 200, 0));

                    String[] tracks = jtf.getText().split("\\?;");
                    final String track1 = tracks[0].substring(1);
                    final String track2 = tracks[1].substring(0, tracks[1].length() - 1);
                    jl.setText("Processing...");
                    javax.swing.Timer jst = new javax.swing.Timer(1000, new ActionListener() {
                        public void actionPerformed(ActionEvent e)
                        {
                            boolean success = false;
                            try {
                                success = ZonePOSClient.stub.processCreditCard(true, track1, track2, null, null, myOrder.getOrderTotal());
                            } catch (RemoteException ee) {
                                CCFailed();
                                jl.setText("Transaction Failed.");

                                return;
                            }

                            if (!success) {
                                CCFailed();
                                jl.setText("Transaction Denied.");

                                return;
                            }

                            curState = States.PADDLE_NUMBER;
                            UpdateUI();
                        }
                    });
                    jst.setRepeats(false);
                    jst.start();
                }
                super.keyReleased(event);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        jtf.grabFocus();

        topPanel.add(jl, c);
        c.gridy = 1;
        topPanel.add(jtf, c);


    }

    void CCFailed()
    {
        JOptionPane.showMessageDialog(this, "Error", "An error has occured. Try again later..", JOptionPane.ERROR_MESSAGE);
        System.out.println("CC Failed...");
        curState = States.PAYMENT_TYPE;
        UpdateUI();
    }

    void CreateSelectionForm()
    {

        GridBagConstraints c = new GridBagConstraints();
        Font f = new Font(null, -1, 26);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 5;
        JLabel l;
        if (curState.equals(States.PADDLE_NUMBER))
            l = new JLabel("Pick a paddle number:");
        else
            l = new JLabel("Pick a tab number:");

        l.setFont(f);
        topPanel.add(l, c);


        c.gridwidth = 1;

        Tab[] tabs = new Tab[50];

        try {
            tabs = ZonePOSClient.stub.getOpenTabs();
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        int count = 5; // curState.equals(States.PADDLE_NUMBER)?3:5;
        for (int x = 0; x < count; x++) {
            for (int y = 0; y < 10; y++) {
                final int num = y * 5 + x + 1;

                JButton b = new JButton(String.valueOf(num));
                if (curState.equals(States.TAB_SELECT))
                    if (tabs[num - 1].open)
                    {
                     b.setOpaque(true);
                        b.setBackground(new Color(0,200,200));
                        b.setForeground(new Color(200, 0, 0));
                    }
                b.setFont(f);
                c.gridx = x;
                c.gridy = y + 1;

                topPanel.add(b, c);
                if (curState.equals(States.PADDLE_NUMBER)) {
                    b.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event)
                        {
                            myOrder.paddle_id = num - 1;
                            myOrder.id = String.valueOf(num);
                            SubmitOrder();
                        }
                    });
                } else {
                    b.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event)
                        {
                            myOrder.PType = Order.PaymentType.TAB;
                            myOrder.tab_id = num - 1;
                            myOrder.id = "TAB.P#" + String.valueOf(num);
                            curState = States.PADDLE_NUMBER;
                            UpdateUI();
                        }
                    });
                }
            }
        }
        JButton b;

        if (curState.equals(States.PADDLE_NUMBER)) {
            c.gridwidth = 3;
            c.gridx = 1;
            c.gridy = 12;
            b = new JButton("NO PADDLE");
            b.setFont(f);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    myOrder.id = "NO ID";
                    SubmitOrder();
                }
            });
            topPanel.add(b, c);

        }

    }

    void SubmitOrder()
    {
        myOrder.status = Order.Statuses.OPEN;

        try {
            if (myOrder.PType.equals(Order.PaymentType.TAB)) {
                ZonePOSClient.stub.addOrder(myOrder, myOrder.tab_id);

            } else {


                ZonePOSClient.stub.addOrder(myOrder);

            }
        } catch (RemoteException e) {
            // this is bad
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        ZonePOSClient.singleton.UpdateMySelectors();
        ZonePOSClient.temp = new Order();
        ZonePOSClient.singleton.UpdateOrderList();
        dispose();

    }

    void DoNumber(String s, JLabel wha, JLabel wha2)
    {
        tenderedMoney = tenderedMoney + s;
        if (tenderedMoney.equals(""))
            tenderedMoney = "0";

        wha.setText(String.format("%10.2f", Float.valueOf(tenderedMoney) / 100));
        float f = (float) (myOrder.getOrderTotal() - Integer.valueOf(tenderedMoney)) / 100;

        if (f > 0) // they owe money
        {
            wha2.setForeground(new Color(200, 0, 0));
            wha2.setText(String.format("Short: \t%10.2f", f));

        } else {
            wha2.setForeground(new Color(0, 0, 0));

            wha2.setText(String.format("Change:\t%10.2f", f));

        }
    }

    void CreateCashForm()
    {
        GridBagConstraints c = new GridBagConstraints();
        if (tenderedMoney.equals(""))
            tenderedMoney = "0";
        float ef = (float) (myOrder.getOrderTotal() - Integer.valueOf(tenderedMoney)) / 100;

        Font f = new Font(null, -1, 26);
        JLabel mt = new JLabel("Money Tendered:");
        JButton jl00 = new JButton("00");
        JButton jl0 = new JButton("0");
        JButton jl1 = new JButton("1");
        JButton jl2 = new JButton("2");
        JButton jl3 = new JButton("3");
        JButton jl4 = new JButton("4");
        JButton jl5 = new JButton("5");
        JButton jl6 = new JButton("6");
        JButton jl7 = new JButton("7");
        JButton jl8 = new JButton("8");
        JButton jl9 = new JButton("9");
        JButton jlb = new JButton("<-");
        final JLabel jtf = new JLabel("0");


        final JLabel jtc = new JLabel("Change:\t0.00");
        jtc.setForeground(new Color(200, 0, 0));
        jtc.setText(String.format("  Owe: \t%10.2f", ef));
        JButton jls = new JButton("SUBMIT");

        mt.setFont(f);
        jl00.setFont(f);
        jl0.setFont(f);
        jl1.setFont(f);
        jl2.setFont(f);
        jl3.setFont(f);
        jl4.setFont(f);
        jl5.setFont(f);
        jl6.setFont(f);
        jl7.setFont(f);
        jl8.setFont(f);
        jl9.setFont(f);
        jlb.setFont(f);
        jtf.setFont(f);
        jls.setFont(f);
        jtc.setFont(f);

        jtf.setOpaque(true);
        jtc.setOpaque(true);

        jtf.setHorizontalAlignment(JLabel.RIGHT);

        jtf.setBackground(new Color(255, 255, 255));
        jtc.setBackground(new Color(255, 255, 255));

        jl00.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("00", jtf, jtc);
            }
        });
        jl0.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("0", jtf, jtc);
            }
        });
        jl1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("1", jtf, jtc);
            }
        });
        jl2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("2", jtf, jtc);
            }
        });
        jl3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("3", jtf, jtc);
            }
        });
        jl4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("4", jtf, jtc);
            }
        });
        jl5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("5", jtf, jtc);
            }
        });
        jl6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("6", jtf, jtc);
            }
        });
        jl7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("7", jtf, jtc);
            }
        });
        jl8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("8", jtf, jtc);
            }
        });
        jl9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                DoNumber("9", jtf, jtc);
            }
        });
        jlb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                if (tenderedMoney.length() > 1)
                    tenderedMoney = tenderedMoney.substring(0, tenderedMoney.length() - 1);
                else tenderedMoney = "0";
                DoNumber("", jtf, jtc);
            }
        });
        jls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                curState = States.PADDLE_NUMBER;
                UpdateUI();
            }
        });
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.125f;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.gridwidth = 4;
        topPanel.add(mt, c);
        c.gridy = 1;
        c.gridx = 0;
        topPanel.add(jtf, c);
        c.gridwidth = 1;
        c.weightx = .25;
        c.gridy = 2;
        topPanel.add(jl7, c);
        c.gridx = 1;
        topPanel.add(jl8, c);
        c.gridx = 2;
        topPanel.add(jl9, c);
        c.gridy = 3;
        c.gridx = 0;
        topPanel.add(jl4, c);
        c.gridx = 1;
        topPanel.add(jl5, c);
        c.gridx = 2;
        topPanel.add(jl6, c);
        c.gridy = 4;
        c.gridx = 0;
        topPanel.add(jl1, c);
        c.gridx = 1;
        topPanel.add(jl2, c);
        c.gridx = 2;
        topPanel.add(jl3, c);
        c.gridy = 5;
        c.gridx = 0;
        topPanel.add(jl0, c);
        c.gridx = 2;
        topPanel.add(jl00, c);
        c.gridx = 3;
        topPanel.add(jlb, c);
        c.gridy = 6;
        c.weightx = 1;
        c.gridwidth = 4;
        c.gridx = 0;
        topPanel.add(jtc, c);
        c.gridy = 7;
        topPanel.add(jls, c);


    }

    void CreatePaymentForm()
    {
        GridBagConstraints c = new GridBagConstraints();

        JButton jl1 = new JButton("CASH");
        JButton jl2 = new JButton("CREDIT");

        Font f = new Font(null, -1, 26);
        jl1.setFont(f);
        jl2.setFont(f);

        Dimension d = new Dimension(120, 70);
        jl1.setMinimumSize(d);
        jl2.setMinimumSize(d);

        jl1.setPreferredSize(d);
        jl2.setPreferredSize(d);
        jl1.setSize(d);
        jl2.setSize(d);


        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        topPanel.add(jl1, c);
        c.gridy = 1;
        topPanel.add(jl2, c);


        jl1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                myOrder.PType = Order.PaymentType.CASH;
                curState = States.CASH_END;
                UpdateUI();

            }
        });
        jl2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                myOrder.PType = Order.PaymentType.CREDIT;

                curState = States.CREDIT_CARD;
                UpdateUI();

            }
        });


    }

    void CreateDestinationForm()
    {
        GridBagConstraints c = new GridBagConstraints();

        JButton jl1 = new JButton("DELIVER");
        JButton jl2 = new JButton("TAB");
        JButton jl3 = new JButton("PAY");

        Font f = new Font(null, -1, 26);
        jl1.setFont(f);
        jl2.setFont(f);
        jl3.setFont(f);

        Dimension d = new Dimension(120, 70);
        jl1.setMinimumSize(d);
        jl2.setMinimumSize(d);

        jl1.setPreferredSize(d);
        jl2.setPreferredSize(d);
        jl1.setSize(d);
        jl2.setSize(d);
        jl3.setSize(d);
        d = new Dimension(180, 70);

        jl3.setMinimumSize(d);
        jl3.setPreferredSize(d);

        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        topPanel.add(jl1, c);
        c.gridx = 1;
        topPanel.add(jl2, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        topPanel.add(jl3, c);

        jl1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                myOrder.OType = Order.OrderType.DELIVERY;
                curState = States.DELIVERY_INFO;
                UpdateUI();

            }
        });
        jl2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                myOrder.OType = Order.OrderType.TAB;

                curState = States.TAB_SELECT;
                UpdateUI();

            }
        });

        jl3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                myOrder.OType = Order.OrderType.BAR;

                curState = States.PAYMENT_TYPE;
                UpdateUI();

            }
        });

    }
}
