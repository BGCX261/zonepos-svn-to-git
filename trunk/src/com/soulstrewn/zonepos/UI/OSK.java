package com.soulstrewn.zonepos.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * User: jeff
 * Date: Mar 4, 2009
 * Time: 1:42:43 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class OSK {
    private JButton qButton;
    private JPanel panel1;
    private JButton wButton;
    private JButton eButton;
    private JButton rButton;
    private JButton tButton;
    private JButton yButton;
    private JButton uButton;
    private JButton iButton;
    private JButton oButton;
    private JButton pButton;
    private JButton aButton;
    private JButton sButton;
    private JButton dButton;
    private JButton fButton;
    private JButton gButton;
    private JButton hButton;
    private JButton jButton;
    private JButton kButton;
    private JButton lButton;
    private JButton zButton;
    private JButton xButton;
    private JButton cButton;
    private JButton vButton;
    private JButton bButton;
    private JButton nButton;
    private JButton mButton;
    private JButton button1;
    private JButton button2;
    private JButton a2Button;
    private JButton a1Button;
    private JButton a3Button;
    private JButton a4Button;
    private JButton a5Button;
    private JButton a6Button;
    private JButton a7Button;
    private JButton a8Button;
    private JButton a9Button;
    private JButton a0Button;
    private JButton button3;
    private JTextField textField1;
    private JButton okayButton;
    private JFrame frame;
    private static Robot robot;

    public OSK()
    {
        aButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'A');
            }
        });
        bButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'B');
            }
        });
        cButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'C');
            }
        });
        dButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'D');
            }
        });
        eButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'E');
            }
        });
        fButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'F');
            }
        });
        gButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'G');
            }
        });
        hButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'H');
            }
        });
        iButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'I');
            }
        });
        jButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'J');
            }
        });
        kButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'K');
            }
        });
        lButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'L');
            }
        });
        mButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'M');
            }
        });
        nButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'N');
            }
        });
        oButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'O');
            }
        });
        pButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'P');
            }
        });
        qButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'Q');
            }
        });
        rButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'R');
            }
        });
        sButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'S');
            }
        });
        tButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'T');
            }
        });
        uButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'U');
            }
        });
        vButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'V');
            }
        });
        wButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'W');
            }
        });
        xButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'X');
            }
        });
        yButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'Y');
            }
        });
        zButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + 'Z');
            }
        });
        a0Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '0');
            }
        });
        a1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '1');
            }
        });
        a2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '2');
            }
        });
        a3Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '3');
            }
        });
        a4Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '4');
            }
        });
        a5Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '5');
            }
        });
        a6Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '6');
            }
        });
        a7Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '7');
            }
        });
        a8Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '8');
            }
        });
        a9Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '9');
            }
        });
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                if (textField1.getText().length() > 0)
                    textField1.setText(textField1.getText().substring(0, textField1.getText().length() - 1));
            }
        });
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + ' ');
            }
        });
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                textField1.setText(textField1.getText() + '.');
            }
        });
        okayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {

                // return or something
                textField1.setText("");
            }
        });

        frame = new JFrame("com.soulstrewn.zonepos.UI.OSK");
        try {
            robot = new Robot();
        } catch (AWTException e) {
        }
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setFocusableWindowState(false);
        frame.setFocusable(false);
        frame.enableInputMethods(false);
        frame.setAlwaysOnTop(true);
        frame.pack();


        panel1.setFocusable(false);
        panel1.enableInputMethods(false);


        frame.setVisible(true);
        aButton.setFocusable(false);


    }

    public static void main(String[] args)
    {
        new OSK();

    }
}
