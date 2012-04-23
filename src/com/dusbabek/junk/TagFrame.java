/*
(c) Copyright 2004, 2005 Gary Dusbabek gdusbabek@gmail.com

ALL RIGHTS RESERVED.

By using this software, you acknowlege and agree that:

1. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
FITNESS FOR A PARTICULAR PURPOSE.

2. This product may be freely copied and distributed in source or binary form
given that the license (this file) and any copyright declarations remain in
tact.

The End
*/

package com.dusbabek.junk;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class TagFrame
    extends JFrame
{
    private ActionListener buttonListener = null;
    private JFileChooser fc = new JFileChooser();

    public TagFrame()
        throws HeadlessException
    {
        super();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        buttonListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                if (ae.getSource() == saveButton)
                    doSave();
                else if (ae.getSource() == openButton)
                    doOpen();
            }
        };
        saveButton.addActionListener(buttonListener);
        openButton.addActionListener(buttonListener);
    }

    private void doOpen()
    {
        int status = fc.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            jPanel1.load(file.getAbsolutePath());
        }
    }

    private void doSave()
    {
        try
        {
            jPanel1.save();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,makeHtml(ex.getMessage(),"There was a problem saving."),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String makeHtml(String header, String body)
    {
        String s = "<html><body><h3>" + header + "</h3>" + body + "</body></html>";
        return s;
    }

    public static void main(String args[])
    {
        TagFrame tf = new TagFrame();
        tf.setSize(640,480);
        tf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tf.setVisible(true);
    }

    private void jbInit()
        throws Exception
    {
        this.getContentPane().setLayout(gridBagLayout1);
        saveButton.setText("Save");
        openButton.setText("Open");
        this.getContentPane().add(jPanel1,
                                  new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
            , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(openButton,
                                  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(saveButton,
                                  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
    }

    GridBagLayout gridBagLayout1 = new GridBagLayout();
    TagPanel jPanel1 = new TagPanel();
    JButton saveButton = new JButton();
    JButton openButton = new JButton();
}
