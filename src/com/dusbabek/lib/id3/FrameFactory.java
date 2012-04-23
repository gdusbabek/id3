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

/**
 * Factory class that creates all frames.  Give it an input stream and a version
 * and it takes care of the rest.  Text and comment frames are automatically
 * specialized.
 */
public class FrameFactory
{
    private static final boolean verbose = System.getProperty("id3.verbose") != null;

    // read a ID3v2.2 frame header.
    private static FrameHeader_v2_2 readFrameHeader_2_2(InputStream in)
        throws IOException
    {
        byte[] b = new byte[FrameHeader_v2_2.FRAME_HEADER_LEN];
        int read = in.read(b);
        if (read != b.length)
            throw new IOException("Expected " + b.length + " bytes.");
        if (b[0] == 0 && b[1] == 0 && b[2] == 0)
            return null; // probably reached padding.
        return new FrameHeader_v2_2(b);
    }

    // read a ID3v2.3 frame header
    private static FrameHeader_v2_3 readFrameHeader_2_3(InputStream in)
        throws IOException
    {
        byte[] b = new byte[FrameHeader_v2_3.FRAME_HEADER_LEN];
        int read = in.read(b);
        if (read != b.length)
            throw new IOException("Expected " + b.length + " bytes.");
        if (b[0] == 0 && b[1] == 0 && b[2] == 0)
        {
            while (in.read() == 0);
            return null; // probably reached padding.
        }
        FrameHeader_v2_3 my_fh = new FrameHeader_v2_3(b);
        if (my_fh.compressed())
        {
            if (verbose) System.out.println("compressed frame");
            // read 4 byes and set as uncompressed size
            byte[] uncsz = new byte[4];
            read = in.read(uncsz);
            if (read != uncsz.length)
                throw new IOException("Expected " + uncsz.length + " bytes.");
            my_fh.setUncompressedSize(ByteUtils.byte4ToLong(uncsz, 0));
        }
        if (my_fh.grouping())
        {
            if (verbose) System.out.println("grouped frame");
            // read 1 byte and set as grouping.
            byte bb = (byte) in.read();
            my_fh.setGrouping(bb);
        }
        return my_fh;
    }

    // read an evermore complicated ID3v2.4 frame header.
    private static FrameHeader_v2_4 readFrameHeader_2_4(InputStream in)
        throws IOException
    {
        byte[] b = new byte[FrameHeader_v2_3.FRAME_HEADER_LEN];
        int read = in.read(b);
        if (read != b.length)
            throw new IOException("Expected " + b.length + " bytes.");
        if (b[0] == 0 && b[1] == 0 && b[2] == 0)
            return null; // probably reached padding.
        FrameHeader_v2_4 my_fh = new FrameHeader_v2_4(b);
        if (my_fh.compressed())
        {
            if (verbose) System.out.println("compressed frame");
            // the datalength indicator MUST be present.
            if (!my_fh.dataLengthIndicator())
                throw new CorruptFrameException(
                    "Compressed frames must include a data length indicator");
        }
        if (my_fh.grouping())
        {
            if (verbose) System.out.println("grouped frame");
            // read 1 byte and set as grouping.
            byte bb = (byte) in.read();
            my_fh.setGrouping(bb);
        }
        if (my_fh.encrypted())
        {
            // read 1 byte and set as grouping.
            byte bb = (byte) in.read();
            my_fh.setEncryptionMethod(bb);
            if (verbose) System.out.println("encrypted by: " + bb + " " +
                                            my_fh.getId());
        }
        if (my_fh.dataLengthIndicator())
        {
            if (verbose) System.out.println("data length indicator");
            // read 4 byes and set as uncompressed size
            byte[] uncsz = new byte[4];
            read = in.read(uncsz);
            if (read != uncsz.length)
                throw new IOException("Expected " + uncsz.length + " bytes.");
            my_fh.setUncompressedSize(ByteUtils.byte4ToLong(uncsz, 0));
        }
        return my_fh;
    }

    /**
     * Create a frame.
     * @param in InputStream
     * @param version int
     * @return Frame, TextFrame or CommentFrame
     * @throws IOException
     */
    public static Frame makeFrame(InputStream in, int version)
        throws IOException
    {
        // construct header.
        FrameHeader fh = null;
        if (version == 2)
            fh = readFrameHeader_2_2(in);
        else if (version == 3)
            fh = readFrameHeader_2_3(in);
        else if (version == 4)
            fh = readFrameHeader_2_4(in);
        else
            throw new IOException("Unexpected version " + version);
        if (fh == null) // reached padding
            return null;
        // sanity check length reported in frame header.
        if (fh.getSize() > 0x00100000 || fh.getSize() < 0)
            throw new CorruptFrameException("Invalid frame header size: " + fh.getId() + ", " + fh.getSize());
        else if (verbose)
            System.out.println(fh.getId() + "," + fh.getSize());
        // read data
        byte[] data = new byte[fh.getSize()];
        int read = in.read(data);
        if (read != data.length)
        {
            // if it is all nulls, then we have reched padding that, for
            // whatever reason had enough of a frame header to count for
            // something;  Chances are we have a corrupt frame, but press on and
            // see if a recovery is possible.
            boolean throwEx = false;
            for (int i = 0; i < data.length; i++)
            {
                if (data[i] != 0)
                {
                    throwEx = true;
                    break;
                }
            }
            if (throwEx)
                throw new CorruptFrameException("Expected " + data.length + " bytes.");
            else
                return null;
        }
        /** @todo handle unsynchronization */

        if (fh.compressed())
            data = ByteUtils.zip_expand(data,fh.getUncompressedSize());
        // generate right frame.
        if (fh.getId().startsWith("T"))
            return new TextFrame(fh,data);
        else if (fh.getId().startsWith("COM"))
            return new CommentFrame(fh,data);
        else
            return new Frame(fh,data);
    }
}
