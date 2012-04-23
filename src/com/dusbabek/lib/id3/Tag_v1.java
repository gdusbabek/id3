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

import java.io.IOException;
import java.io.OutputStream;

/**
 * ID3v1.x tag.
 *
 * 'T' 'A' 'G'          3 bytes
 * song name            30 bytes
 * artist               30 bytes
 * album                30 bytes
 * year                 4 bytes
 * comment              30 bytes
 * genre                1 byte
 *
 * in v1.1, the genre is 28 bytes. byte 29 is null and byte 30 indicates
 * track (8 bit unsigned int).
 */
public class Tag_v1
    implements Tag
{
    /** length of a 1.x tag is always 128 bytes. */
    public static final int TAG_LENGTH = 128;

    /** ID3v1.0 */
    public static final int VERSION_1_0 = 1;
    /** ID3v1.1 */
    public static final int VERSION_1_1 = 2;

    private int version;
    private String title = null;
    private String artist = null;
    private String album = null;
    private String year = null;
    private String comment = null;
    private byte track;
    private byte genre;

    /**
     * construct a tag from data parts.
     * @param data byte[]
     * @throws IOException
     */
    public Tag_v1(byte[] data)
        throws IOException
    {
        super();
        version = VERSION_1_0;
        if (data[125] == 0 && data[126] != 0)
            version = VERSION_1_1;
        if (data.length != TAG_LENGTH)
            throw new IOException("Expected 128 byte 1.x header.");
        if (data[0] != 'T' || data[1] != 'A' || data[2] != 'G')
            throw new NotATagException();
        title = new String(data,3,30);
        artist = new String(data,33,30);
        album = new String(data,63,30);
        year = new String(data,93,4);
        if (version == VERSION_1_0)
            comment = new String(data,97,30);
        else
        {
            comment = new String(data,97,28);
            track = data[126];
        }
        genre = data[127];
    }

    /** @return stringified verion. */
    public String getVersion()
    {
        if (version == VERSION_1_0)
            return Tag.V1_0;
        else if (version == VERSION_1_1)
            return Tag.V1_1;
        else
            throw new RuntimeException("Invalid version: " + version);
    }

    /** {@inheritDoc}  */
    public String getTitle() { return title; }

    /** {@inheritDoc}  */
    public String getArtist() { return artist; }

    /** {@inheritDoc}  */
    public String getAlbum() { return album; }

    /** {@inheritDoc}  */
    public String getYear() { return year; }

    /** {@inheritDoc}  */
    public String getComment() { return comment; }

    /** {@inheritDoc}  */
    public String getTrack()
    {
        return new Byte(track).toString();
    }

    /** {@inheritDoc}  */
    public String getGenre()
    {
        try
        {
            return Genre.getString(genre);
        }
        catch (IllegalArgumentException ex)
        {
            System.err.println(ex);
            return null;
        }
    }

    /** {@inheritDoc}  */
    public void setTitle(String s)
    {
        s = s.substring(0,Math.min(30,s.length()));
        title = s;
    }

    /** {@inheritDoc}  */
    public void setArtist(String s)
    {
        s = s.substring(0,Math.min(30,s.length()));
        artist = s;
    }

    /** {@inheritDoc}  */
    public void setAlbum(String s)
    {
        s = s.substring(0,Math.min(30,s.length()));
        album = s;
    }

    /** {@inheritDoc}  */
    public void setYear(String s)
    {
        s = s.substring(0,Math.min(4,s.length()));
        year = s;
    }

    /** {@inheritDoc}  */
    public void setComment(String s)
    {
        s = s.substring(0,Math.min(30,s.length()));
        comment = s;
    }

    /** {@inheritDoc}  */
    public void setTrack(String s)
    {
        s = s.substring(0,Math.max(4,s.length()));
        try
        {
            track = Byte.parseByte(s);
            version = VERSION_1_1;
        }
        catch (NumberFormatException ex)
        {
            System.err.println(ex);
        }
    }

    /**
     * sets the genre according to the 1.x byte spec.
     * @param b byte
     */
    public void setGenre(byte b)
    {
        genre = b;
    }

    /** {@inheritDoc}  */
    public void setGenre(String s)
    {
        try
        {
            genre = Genre.getByte(s);
        }
        catch (IllegalArgumentException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    /** {@inheritDoc}  */
    public void write(OutputStream out)
      throws IOException
    {
        out.write(new byte[]{'T','A','G'});
        write(out,getTitle().getBytes(),30);
        write(out,getArtist().getBytes(),30);
        write(out,getAlbum().getBytes(),30);
        write(out,getYear().getBytes(),4);
        if (version == VERSION_1_0)
            write(out,getComment().getBytes(),30);
        else
        {
            write(out,getComment().getBytes(),28);
            out.write(0);
            out.write(new byte[]{track});
        }
        out.write(new byte[]{genre});
    }

    // helper for ensuring that the data written fits exactly into the space
    // alotted. it will truncate long data and pad short data.
    private void write(OutputStream out, byte[] data, int size)
      throws IOException
    {
        System.out.println(new String(data));
        if (data.length >= size)
            out.write(data,0,size);
        else
        {
            out.write(data);
            for (int i = 0; i < size - data.length; i++)
                out.write(0);
        }
    }
}
