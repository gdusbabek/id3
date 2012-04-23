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
 * Tags are composed of individual frames. A frame encapsulates a basic piece
 * of information.
 */
public class Frame
{
    private static final boolean verbose = System.getProperty("id3.verbose") != null;
    private FrameHeader header = null;
    private byte[] data = null;

    /**
     * Creates a frame from a header and raw frame data.
     * @param header FrameHeader
     * @param data byte[]
     * @throws IOException
     */
    public Frame(FrameHeader header, byte[] data)
        throws IOException
    {
        this.header = header;
        this.data = data;
    }

    /** @return the frame header */
    public FrameHeader getHeader() { return header; }

    /** @return the raw frame data */
    public byte[] getData() { return data; }

    /** write the frame (all of it) to a stream. */
    public void write(OutputStream out)
        throws IOException
    {
        /** @todo error checking: data.length isn't too big to represents in
         * the bits allowed by a particular version from FrameHeader. */

        byte[] dataToWrite = getData();
        if (getHeader().compressed())
            dataToWrite = ByteUtils.zip(dataToWrite);
        getHeader().setFrameSize(dataToWrite.length);
        getHeader().write(out);
        out.write(dataToWrite);
        if (verbose)
            System.out.println("write frame " + getHeader().getId() + "," + dataToWrite.length + "," + getHeader().compressed());
    }
}
