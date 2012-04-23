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

import java.io.*;

/**
 * Specialized frame.
 * <normal frame header>
 * <null byte>
 * <text data.
 */
public class TextFrame
    extends Frame
{
    private String value = null;

    /**
     * construct a text frame from values
     * @param id String
     * @param value String
     * @param version int
     * @throws IOException
     */
    public TextFrame(String id, String value, int version)
        throws IOException
    {
        this(makeHeader(id,value,version),pad(value.getBytes()));
    }

    /**
     * construct a text frame from raw data
     * @param header FrameHeader
     * @param data byte[]
     * @throws IOException
     */
    public TextFrame(FrameHeader header, byte[] data)
        throws IOException
    {
        super(header, data);
        value = new String(getData(),1,getData().length-1);
    }

    /** @return text value (minus null padding) */
    public String getValue() { return value; }

    // factory method for making text frame headers.
    private static FrameHeader makeHeader(String id, String value, int version)
        throws IOException
    {
        FrameHeader header = null;
        if (version == 2)
            header = new FrameHeader_v2_2(id);
        else if (version == 3)
            header = new FrameHeader_v2_3(id);
        else if (version == 4)
            header = new FrameHeader_v2_4(id);
        header.setFrameSize(value.length() + 1);
        return header;
    }

    // factory method for formatting text frame data.
    private static byte[] pad(byte[] b)
    {
        byte[] buf = new byte[b.length + 1];
        buf[0] = 0;
        System.arraycopy(b,0,buf,1,b.length);
        return buf;
    }
}
