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

package com.dusbabek.lib.id3;

import java.util.Hashtable;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * Represents a ID3v2.x tag. I'm not going to bother with bits and bytes here,
 * but here is the rough format.
 * <header>
 * <optional extended header>
 * <frame set>
 *     <frame>
 *     ...
 *     <frame>
 * </frameset>
 * <optional padding>
 *
 * You should get the idea if you've made it this far.
 */
public class Tag_v2
    implements Tag
{
    private TagHeader header = null;
    private Hashtable frames = new Hashtable();

    /** creates a blank tag. Useful for writing to files with no current tag */
    public Tag_v2()
        throws IOException
    {
        super();
        this.header = new TagHeader_v2_3();
    }

    /**
     * creates a stub of a tag (header but no frames).
     * @param header TagHeader
     */
    public Tag_v2(TagHeader header)
    {
        super();
        this.header = header;
    }

    /**
     * add or replace a frame.
     * @param fr Frame
     * @todo option to add or replace to handle multiple occurances of a frame.
     */
    public void putFrame(Frame fr)
    {
        frames.remove(fr.getHeader().getId());
        frames.put(fr.getHeader().getId(),fr);
    }

    /** @return frame as specified by id. */
    private Frame getFrame(String id)
    {
        return (Frame)frames.get(id);
    }

    /** {@inheritDoc}  */
    public String getAlbum()
    {
        TextFrame tf = (TextFrame)getFrame(FrameType.getId(FrameType.ALBUM,header.getVersion()));
        return tf == null ? "" : tf.getValue();
    }

    /** {@inheritDoc}  */
    public String getArtist()
    {
        TextFrame tf = (TextFrame)getFrame(FrameType.getId(FrameType.ARTIST,header.getVersion()));
        return tf == null ? "" : tf.getValue();
    }

    /** {@inheritDoc}  */
    public String getComment()
    {
        CommentFrame cf = (CommentFrame)getFrame(FrameType.getId(FrameType.COMMENT,header.getVersion()));
        return cf == null ? "" : cf.getComment();
    }

    /** {@inheritDoc}  */
    public String getGenre()
    {
        TextFrame tf = (TextFrame)getFrame(FrameType.getId(FrameType.GENRE,header.getVersion()));
        return tf == null ? "" : tf.getValue();
    }

    /** {@inheritDoc}  */
    public String getTitle()
    {
        TextFrame tf = (TextFrame)getFrame(FrameType.getId(FrameType.SONGNAME,header.getVersion()));
        return tf == null ? "" : tf.getValue();
    }

    /** {@inheritDoc}  */
    public String getTrack()
    {
        TextFrame tf = (TextFrame)getFrame(FrameType.getId(FrameType.TRACK,header.getVersion()));
        return tf == null ? "" : tf.getValue();
    }

    /** {@inheritDoc}  */
    public String getYear()
    {
        TextFrame tf = (TextFrame)getFrame(FrameType.getId(FrameType.YEAR,header.getVersion()));
        return tf == null ? "" : tf.getValue();
    }

    /** {@inheritDoc}  */
    public void setAlbum(String s)
    {
        try
        {
            putFrame(new TextFrame(FrameType.getId(FrameType.ALBUM,header.getVersion()),s, header.getVersion()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /** {@inheritDoc}  */
    public void setArtist(String s)
    {
        try
        {
            putFrame(new TextFrame(FrameType.getId(FrameType.ARTIST,header.getVersion()),s, header.getVersion()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /** {@inheritDoc}  */
    public void setComment(String s)
    {
        try
        {
            putFrame(new CommentFrame(s,header.getVersion()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /** {@inheritDoc}  */
    public void setGenre(String s)
    {
        try
        {
            putFrame(new TextFrame(FrameType.getId(FrameType.GENRE,header.getVersion()),s, header.getVersion()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /** {@inheritDoc}  */
    public void setTitle(String s)
    {
        try
        {
            putFrame(new TextFrame(FrameType.getId(FrameType.SONGNAME,header.getVersion()),s, header.getVersion()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /** {@inheritDoc}  */
    public void setTrack(String s)
    {
        try
        {
            putFrame(new TextFrame(FrameType.getId(FrameType.TRACK,header.getVersion()),s, header.getVersion()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /** {@inheritDoc}  */
    public void setYear(String s)
    {
        try
        {
            putFrame(new TextFrame(FrameType.getId(FrameType.YEAR,header.getVersion()),s, header.getVersion()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /** {@inheritDoc}  */
    public void write(OutputStream out)
        throws IOException
    {
        // convert all frames to a byte array.
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (Iterator i = frames.values().iterator(); i.hasNext();)
        {
            Frame fr = (Frame)i.next();
            fr.write(bout);
        }
        bout.flush();
        byte[] frameBytes = bout.toByteArray();
        bout.close();
        if (header.usesUnsynchronization())
            frameBytes = ByteUtils.unsync(frameBytes);
        header.setDataSize(frameBytes.length);
        header.write(out);
        out.write(frameBytes);
    }

    /** {@inheritDoc}  */
    public String getVersion()
    {
        switch (header.getVersion())
        {
            case 2: return Tag.V2_2;
            case 3:  return Tag.V2_3;
            case 4: return Tag.V2_4;
            default: return "Unkown/Invalid";
        }
    }
}

