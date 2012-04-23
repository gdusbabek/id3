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
 * Frame header for ID3v2.3
 */
public class FrameHeader_v2_3
    implements FrameHeader
{
    /** basic header is always 10 bytes. */
    public static final int FRAME_HEADER_LEN = 10;

    private String id = null;
    private int size = 0;
    private byte[] flags = new byte[2];

    private long uncompressedSize = 0;
    private byte grouping = 0;

    /** construct an empty header using a specific frame id. */
    public FrameHeader_v2_3(String id)
        throws IOException
    {
        if (id.length() > 4)
            throw new IOException("Header id too long: " + id);
        this.id = id;
        size = 0;
    }

    /** construct a header from raw data. */
    public FrameHeader_v2_3(byte[] data)
        throws IOException
    {
        super();
        if (data.length != FRAME_HEADER_LEN)
            throw new IOException("Expected " + FRAME_HEADER_LEN + " bytes in frame header.");
        id = new String(data,0,4);
        size = (int)ByteUtils.byte4ToLong(data,4);
        flags[0] = data[8];
        flags[1] = data[9];
    }

    /** @return flags */
    protected byte[] getFlags() { return flags; }

    /** {@inheritDoc}  */
    public String getId() { return id; }

    /** {@inheritDoc}  */
    public int getSize() { return size; }

    /** {@inheritDoc}  */
    public int sizeOf() { return FRAME_HEADER_LEN; }

    /** {@inheritDoc}  */
    public void setFrameSize(int size) { this.size = size; }

    /** {@inheritDoc}  */
    public void write(OutputStream out)
        throws IOException
    {
        byte[] b_id = id.getBytes();
        out.write(b_id,0,4);
        /** @assume size has been properly set in Frame.write(). */
        out.write(ByteUtils.longToByte4(size));
        out.write(flags);
        if (compressed())
            out.write(ByteUtils.longToByte4(uncompressedSize)); // won't work in v4.
        if (grouping())
            out.write(grouping);
    }

    /** @return tag alter preservation */
    public boolean tagAlterPreservation() { return (getFlags()[0] & 0x80) > 0; }

    /** @return file alter preservation */
    public boolean fileAlterPreservation() { return (getFlags()[0] & 0x40) > 0; }

    /** @return true if this frame is meant to be read only. */
    public boolean readOnly() { return (getFlags()[0] & 0x20) > 0; }

    /** {@inheritDoc}  */
    public boolean compressed() { return (getFlags()[1] & 0x80) > 0; }

    /**
     * I must point out that the spec is only half-baked in this regard.  THE
     * BEST I can do is ignore encrypted frames.
     * @return true if this frame is encrypted.
     */
    public boolean encrypted() { return (getFlags()[1] & 0x40) > 0; }

    /** @return true if this frame uses grouping. */
    public boolean grouping() { return (getFlags()[1] & 0x20) > 0; }

    /** set the uncompressed size of this frame. */
    public void setUncompressedSize(long l) { uncompressedSize = l; };

    /** {@inheritDoc}  */
    public long getUncompressedSize() { return uncompressedSize; }

    /** set the grouping type */
    public void setGrouping(byte b) { grouping = b; }

    /** @return the grouping type. */
    public byte getGrouping() { return grouping; }
}
