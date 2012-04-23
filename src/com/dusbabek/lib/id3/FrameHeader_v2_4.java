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
 * Frame header for ID3v2.4 tag.
 */
public class FrameHeader_v2_4
    extends FrameHeader_v2_3
{
    private byte encryptionMethod = 0;
    private long dataLengthIndicator = 0;

    /** construct an empty header based on a frame id. */
    public FrameHeader_v2_4(String id)
        throws IOException
    {
        super(id);
    }

    /** construct a header from raw data */
    public FrameHeader_v2_4(byte[] data)
        throws IOException
    {
        super(data);
    }

    /** {@inheritDoc}  */
    public boolean tagAlterPreservation() { return (getFlags()[0] & 0x40) > 0; }

    /** {@inheritDoc}  */
    public boolean fileAlterPreservation() { return (getFlags()[0] & 0x20) > 0; }

    /** {@inheritDoc}  */
    public boolean readOnly() { return (getFlags()[0] & 0x10) > 0; }

    /** {@inheritDoc}  */
    public boolean compressed() { return (getFlags()[1] & 0x08) > 0; }

    /** {@inheritDoc}  */
    public boolean encrypted() { return (getFlags()[1] & 0x04) > 0; }

    /** {@inheritDoc}  */
    public boolean grouping() { return (getFlags()[1] & 0x40) > 0; }

    /** @return true if unsynchronization is used. */
    public boolean unsynchronization() { return (getFlags()[1] & 0x02) > 0; }

    /** @return true if there is a datalength idicator.  It is REQUIRED in some instances. */
    public boolean dataLengthIndicator() { return (getFlags()[1] & 0x01) > 0; }

    /** set the data length indicator */
    public void setDataLengthIndicator(long l) { dataLengthIndicator = l; }

    /** @return the data length indicator. */
    public long getDataLengthIndicator() { return dataLengthIndicator; }

    /** set the encryption method. */
    public void setEncryptionMethod(byte b) { encryptionMethod = b; }

    /** {@inheritDoc}  */
    public void write(OutputStream out)
        throws IOException
    {
        byte[] b_id = getId().getBytes();
        out.write(b_id,0,4);
        /** @assume size has been properly set in Frame.write(). */
        out.write(ByteUtils.longToByte4(getSize()));
        out.write(getFlags());
        if (grouping())
            out.write(getGrouping());
        if (compressed())
            out.write(ByteUtils.longToByte4(getUncompressedSize())); // won't work in v4.
        if (encrypted())
            out.write(encryptionMethod);
        if (dataLengthIndicator())
            out.write(ByteUtils.longToByte4(ByteUtils.addZeroBits(dataLengthIndicator)));
    }


}
