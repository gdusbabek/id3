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
 * Represents a ID3v2.4 extended tag header, which differs SIGNIFICANTLY from
 * the ID3V2.3 extended tag header. Here is how it works:
 * size of header         0x00-0x03
 * number of flag bytes   0x04
 * flags                  0x05-0x05+<number of flag bytes>
 * optional parts specified in flags. each optional part consists of a
 * single byte header indicationg how many bytes to read for the rest of the
 * part.
 */
public class ExtendedTagHeader_v2_4
    implements ExtendedTagHeader
{
    long extHeaderSize = 0;
    byte numFlagBytes = 0;
    byte[] flags = null;
    byte[] update = null;
    byte[] crc = null;
    byte[] tagRestrictions = null;
    private int size = 0;

    /**
     * Construct an extended header from a stream of bytes.
     * @param in InputStream
     * @throws IOException
     */
    public ExtendedTagHeader_v2_4(InputStream in)
        throws IOException
    {
        super();
        byte[] b = new byte[4];
        int read = in.read(b);
        if (read != b.length)
            throw new IOException("Expected " + b.length + " bytes.");
        size += b.length;
        extHeaderSize = ByteUtils.removeZeroBits(ByteUtils.byte4ToLong(b,0));
        numFlagBytes = (byte)in.read();
        size++;
        flags = new byte[numFlagBytes];
        read = in.read(flags);
        size += read;
        if (read != flags.length)
            throw new IOException("Expected " + flags.length + " bytes.");
        if (isUpdate())
            update = readBlock(in);
        if (usesCrc())
            crc = readBlock(in); // 5 bytes.
        if (usesTagRestrictions())
            tagRestrictions = readBlock(in); // 1 byte
    }

    /**
     * write extended header to a stream.
     * @param out OutputStream
     * @throws IOException
     */
    public void write(OutputStream out) throws IOException
    {
        /** @assume that the private size variable has been maitained. */
        extHeaderSize = size;
        out.write(ByteUtils.longToByte4(ByteUtils.addZeroBits(extHeaderSize)));
        out.write(numFlagBytes);
        out.write(flags);
        if (isUpdate())
        {
            out.write(update.length);
            out.write(update);
        }
        if (usesCrc())
        {
            out.write(crc.length);
            out.write(crc);
        }
        if (usesTagRestrictions())
        {
            out.write(tagRestrictions.length);
            out.write(tagRestrictions);
        }
    }

    // read in a block (term I invented.  a block consists of a one-byte size
    // header, followed by the amount of bytes specified in the header.
    private byte[] readBlock(InputStream in)
        throws IOException
    {
        int blkSz = in.read();
        size++;
        if (blkSz > 128)
            throw new IOException("Extended header block exceeded 128 bytes.");
        byte[] buf = new byte[blkSz];
        int read = in.read(buf);
        if (read != blkSz)
            throw new IOException("Expected " + blkSz + " bytes.");
        size += blkSz;
        return buf;
    }

    /** @return size of extended header. */
    public int sizeOf() { return size; }

    /** @return true if this tag updates another. */
    public boolean isUpdate() { return (flags[0] & 0x40) > 0; }

    /** @return true if this tag specifies a CRC */
    public boolean usesCrc() { return (flags[0] & 0x20) > 0; }

    /** @return true if this tag calls for tag restrictions. */
    public boolean usesTagRestrictions() { return (flags[0] & 0x10) > 0; }
}
