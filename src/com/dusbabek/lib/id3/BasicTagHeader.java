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

/** @todo unsynchronization needs to be performed depending on version! */
/**
 * This class represents the 10 bytes that are the basic header on any ID3v2
 * tag.  For the uninitiated, that format is
 * 'I' 'D' '3'  0x00-0x02
 * version      0x03
 * revision     0x04
 * flags        0x05
 * tag size     0x06-0x09
 *
 * the tag size can be calculated by ignorning the first bit of each bit (hi)
 * and converting the 28 bits that are left over into an integer.
 */
public abstract class BasicTagHeader
    implements TagHeader
{
    private static final boolean verbose = System.getProperty("id3.verbose") != null;

    /** size of a basic tag header. */
    public static final int BASIC_TAG_HEADER_LEN = 10;

    private byte version = 0;
    private byte revision = 0;
    private byte flags = 0;
    private long tagSize = 0;

    /**
     * Constructor. Fills value based on byte array passed in.
     * @param tenBytes byte[]
     * @throws IOException
     */
    public BasicTagHeader(byte[] tenBytes)
        throws IOException
    {
        /*
        ID3/file identifier      "ID3"
        ID3 version              $02 00
        ID3 flags                %xx000000
        ID3 size             4 * %0xxxxxxx
        */
        if (tenBytes.length != BASIC_TAG_HEADER_LEN)
            throw new IOException("Expected " + BASIC_TAG_HEADER_LEN + " bytes.");
        if (tenBytes[0] != 'I' || tenBytes[1] != 'D' || tenBytes[2] != '3')
            throw new NotATagException();
        version = tenBytes[3];
        revision = tenBytes[4]; // not used much.
        flags = tenBytes[5];
        tagSize = ByteUtils.removeZeroBits(ByteUtils.byte4ToLong(tenBytes,6));
        if (verbose)
        {
            System.out.println("version: " + version);
            System.out.println("revistion: " + revision);
            System.out.println("flags: " + ByteUtils.byteToString(flags));
            System.out.println("tag size: " + tagSize);
        }
    }

    /** @return the version field */
    public byte getVersion() { return version; }

    /** @return the flag field */
    public byte getFlags() { return flags; }

    /**
     * returned as long to avoid sign extension problems in java. A java long
     * is unsigned, but think of this value as a 28 bit unsigned integer.
     * @return the size of the entire tag minus the 10 byte header.
     */
    public long getTagSize() { return tagSize; }

    /**
     * kind of moot. returns the actual size of the header, which will include
     * an extended header in some versions.
     * @return int
     */
    public int sizeOf() { return BASIC_TAG_HEADER_LEN; }

    /**
     * returns the size of the data (not including ANY headers).
     * @return long
     */
    public long getDataSize() { return getTagSize(); }

    /**
     * set the size of the data.
     * @param l long
     */
    public void setDataSize(long l) { tagSize = l; }

    /**
     * set the size of the tag.
     * @param l long
     */
    protected void setTagSize(long l) { tagSize = l; }

    /**
     * write this header to a stream. Note that the tag size needs to be
     * correct BEFORE the write.  All Tag implementations need to be aware of
     * this.
     * @param out OutputStream
     * @throws IOException
     */
    public void write(OutputStream out) throws IOException
    {
        // 6 bytes.
        out.write(new byte[]{'I','D','3',version,revision,flags });
        /** @assume tag size has been properly set */
        out.write(ByteUtils.longToByte4(ByteUtils.addZeroBits(tagSize)));
    }

    public abstract boolean usesUnsynchronization();
}
