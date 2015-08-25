package com.soulstrewn.zonepos.UI;

import javax.swing.*;

/**
 * User: jeff
 * Date: Mar 2, 2009
 * Time: 5:25:57 PM
 * No warranties, expressed or implied, including the warranty that it'll actually work, etc...
 */
public class XMLEditorForm {
    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField3;
    private JTextField textField2;
    private JTextField textField4;
    private JTextField textField5;
    private JRadioButton manyRadioButton;
    private JRadioButton oneRadioButton;
    private JTextField textField6;
    private JTextField textField7;
    private JList list1;
    private JButton button1;
    private JButton button2;
    private JList list2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JTree tree1;
    private JButton button7;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("com.soulstrewn.zonepos.UI.XMLEditorForm");
        frame.setContentPane(new XMLEditorForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
