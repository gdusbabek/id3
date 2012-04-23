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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.*;
import java.util.List;
import java.util.Iterator;

import com.dusbabek.lib.id3.*;

public class TagPanel
    extends JPanel
{
    private Tag model = null;
    private File curFile = null;
    private JFileChooser fc = new JFileChooser();

    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JTextField song = new JTextField();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JTextField artist = new JTextField();
    JLabel jLabel3 = new JLabel();
    JTextField album = new JTextField();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JTextField year = new JTextField();
    JTextField track = new JTextField();
    JLabel jLabel6 = new JLabel();
    JComboBox genre = new JComboBox();
    JLabel jLabel7 = new JLabel();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea comment = new JTextArea();

    public TagPanel()
    {
        super();
        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        DropTarget dt = new DropTarget(this,DnDConstants.ACTION_COPY_OR_MOVE,new Dropper());
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
    }

    private void setModel(Tag tag)
    {
        this.model = tag;
        song.setText(tag.getTitle());
        artist.setText(tag.getArtist());
        album.setText(tag.getAlbum());
        year.setText(tag.getYear());
        track.setText(tag.getTrack());
        genre.setSelectedItem(tag.getGenre());
        comment.setText(tag.getComment());
    }

    public Tag getModel()
    {
        if (model == null)
            return null;
        model.setTitle(song.getText());
        model.setArtist(artist.getText());
        model.setAlbum(album.getText());
        model.setYear(year.getText());
        model.setTitle(song.getText());
        model.setComment(comment.getText());
        model.setTrack(track.getText());
        model.setGenre(genre.getSelectedItem().toString());
        return model;
    }

    public void save()
        throws IOException
    {
        if (curFile == null)
            return;
        fc.setCurrentDirectory(curFile.getParentFile());
        int status = fc.showSaveDialog(this);
        if (status != JFileChooser.APPROVE_OPTION)
            return;
        File saveAsFile = fc.getSelectedFile();
        Mp3File mp3 = new Mp3File(curFile);
        model = getModel();
        mp3.setTag(model);
        mp3.write(saveAsFile);
    }


    public void load(String fileName)
    {
        try
        {
            File f = new File(fileName);
            Reader r = new Reader();
            InputStream in = new FileInputStream(f);
            Tag t = r.read(in,f.length());
            curFile = f;
            setModel(t);
        }
        catch (NotATagException ex)
        {
            JOptionPane.showMessageDialog(this,"File contains no tag.","No tag",JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void jbInit()
        throws Exception
    {
        this.setLayout(gridBagLayout1);
        song.setText("");
        jLabel1.setText("Song");
        jLabel2.setText("Artitst");
        artist.setText("");
        jLabel3.setText("Album");
        album.setText("");
        jLabel4.setText("Year");
        jLabel5.setText("Track");
        year.setText("");
        track.setText("");
        jLabel6.setText("Genre");
        jLabel7.setText("Comment");
        comment.setText("");
        genre.setEditable(true);
        this.add(song, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        jScrollPane1.getViewport().add(comment);
        this.add(artist, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(album, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(genre, new GridBagConstraints(0, 9, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(jScrollPane1, new GridBagConstraints(0, 11, 2, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 50));
        this.add(year, new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(track, new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        this.add(jLabel1, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
                                                 , GridBagConstraints.SOUTHWEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(3, 0, 0, 0), 0, 0));
        this.add(jLabel2, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0
                                                 , GridBagConstraints.SOUTHWEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(3, 0, 0, 0), 0, 0));
        this.add(jLabel3, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0
                                                 , GridBagConstraints.SOUTHWEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(3, 0, 0, 0), 0, 0));
        this.add(jLabel4, new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0
                                                 , GridBagConstraints.SOUTHWEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(3, 0, 0, 0), 0, 0));
        this.add(jLabel5, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0
                                                 , GridBagConstraints.SOUTHWEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(3, 0, 0, 0), 0, 0));
        this.add(jLabel6, new GridBagConstraints(0, 8, 3, 1, 1.0, 0.0
                                                 , GridBagConstraints.SOUTHWEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
        this.add(jLabel7, new GridBagConstraints(0, 10, 2, 1, 1.0, 0.0
                                                 , GridBagConstraints.SOUTHWEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    }

    private class Dropper
        implements DropTargetListener
    {
        public void dragEnter(DropTargetDragEvent dtde)
        {
            if (dtde.getCurrentDataFlavorsAsList().contains(DataFlavor.javaFileListFlavor))
                dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
            else
                dtde.rejectDrag();
        }

        public void dragOver(DropTargetDragEvent dtde)
        {
        }

        public void dropActionChanged(DropTargetDragEvent dtde)
        {
        }

        public void drop(DropTargetDropEvent e)
        {
            Transferable tr = e.getTransferable();
            if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
            {
                try
                {
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    List list = (List)tr.getTransferData(DataFlavor.javaFileListFlavor);
                    e.dropComplete(true);
                    File f = (File)list.iterator().next();
                    load(f.getAbsolutePath());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        public void dragExit(DropTargetEvent dte)
        {
        }
    }
}
