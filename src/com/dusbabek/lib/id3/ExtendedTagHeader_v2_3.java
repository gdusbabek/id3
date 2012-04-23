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
import java.io.InputStream;
import java.io.OutputStream;

/** @todo ensure that extHeaderSize is maintained according to existence of crc. */

/**
 * Extended header for a ID3v2.3 tag. Here is the way it usually goes down:
 * ext header size         0x00-0x03
 * ext flags               0x04-0x05
 * padding size            0x06-0x09
 * optional crc            0x0a-0x0d
 *
 * the crc will only be present if specified in the flags.
 * the ext header size will either be 6 or 10 depending on whether or not a
 * crc is specified.
 */
public class ExtendedTagHeader_v2_3
  implements ExtendedTagHeader
{
    long extHeaderSize = 0;
    byte[] flags = null;
    long sizeOfPadding = 0;
    byte[] crc = null;

    /**
     * Construct an extended header from a stream of bytes.
     * @param in InputStream
     * @throws IOException
     */
    public ExtendedTagHeader_v2_3(InputStream in)
        throws IOException
    {
        super();
        // Extended header size	 	$xx xx xx xx
        byte[] b = new byte[4];
        int read = in.read(b);
        if (read != b.length)
            throw new IOException("Expected " + b.length + " bytes.");
        extHeaderSize = ByteUtils.byte4ToLong(b,0);
        // Extended Flags	$xx xx
        flags = new byte[2];
        read = in.read(flags);
        if (read != flags.length)
            throw new IOException("Expected " + flags.length + " bytes.");
        // Size of padding	$xx xx xx xx
        read = in.read(b);
        if (read != b.length)
            throw new IOException("Expected " + b.length + " bytes.");
        sizeOfPadding = ByteUtils.byte4ToLong(b,0);
        // Optional crc $xx xx xx xx
        if (crcDataPresent())
        {
            crc = new byte[4];
            read = in.read(crc);
            if (read != crc.length)
                throw new IOException("Expected " + crc.length + " bytes.");
            extHeaderSize = 10; // duh.
        }
    }

    /**
     * write extended header.
     * @param out OutputStream
     * @throws IOException
     */
    public void write(OutputStream out)
        throws IOException
    {
        out.write(ByteUtils.longToByte4(extHeaderSize));
        out.write(flags);
        out.write(ByteUtils.longToByte4(sizeOfPadding));
        if (crc != null)
            out.write(crc);
    }

    /** @return physical size of extended header */
    public int sizeOf()
    {
        // 10 or 14.
        if (crc == null)
            return 10;
        else
            return 14;
    }

    /** @return true of a CRC is specified. */
    public boolean crcDataPresent()
    {
        return (flags[0] & 0x80) > 0;
    }
}
