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
 * Frame header for ID3v2.2.  Nice and simple...
 */
public class FrameHeader_v2_2
    implements FrameHeader
{
    /** basic header size is always 6 bytes. */
    public static final int FRAME_HEADER_LEN = 6;

    private int size = 0;
    private String id = null;

    /** construct an empty header using a specified frame id. */
    public FrameHeader_v2_2(String id)
        throws IOException
    {
        if (id.length() > 3)
            throw new IOException("Frame id too long: " + id);
        this.id = id;
        size = 0;
    }

    /** construct a header from raw data */
    public FrameHeader_v2_2(byte[] data)
        throws IOException
    {
        super();
        if (data.length != FRAME_HEADER_LEN)
            throw new IOException("Expected " + FRAME_HEADER_LEN + " bytes in frame header.");
        id = new String(data,0,3);
        size = (int)ByteUtils.byte3ToLong(data,3);
    }

    /** {@inheritDoc}  */
    public void setFrameSize(int size) { this.size = size; }

    /** {@inheritDoc}  */
    public int getSize() { return size; }

    /** {@inheritDoc}  */
    public String getId() { return id; }

    /** {@inheritDoc}  */
    public int sizeOf() { return FRAME_HEADER_LEN; }

    /** {@inheritDoc}  */
    public void write(OutputStream out)
        throws IOException
    {
        /** @assume size has been property let in header write. */
        byte[] b_id = id.getBytes();
        out.write(b_id,0,3);
        out.write(ByteUtils.longToByte3(size));
    }

    /** {@inheritDoc}  */
    public boolean compressed() { return false; }

    /** {@inheritDoc}  */
    public long getUncompressedSize()
    {
        throw new RuntimeException("Unsupported operation.");
    }
}
