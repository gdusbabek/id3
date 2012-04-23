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
 * Parses tags.
 */
public class Reader
{
    private static final boolean verbose = System.getProperty("id3.verbose") != null;
    public Reader()
    {
        super();
    }

    /**
     * read a tag from an input stream.
     * @param in InputStream
     * @param len long amount of data supposed to be in the stream.
     * @return Tag
     * @throws IOException
     */
    public Tag read(InputStream in, long len)
        throws IOException
    {
        // we need to be able to reset the stream. so use a buffered stream.
        in = new BufferedInputStream(in);
        in.mark(0);
        /*
        Id3v2 header:

        ID3/file identifier      "ID3"
        ID3 version              $02 00
        ID3 flags                %xx000000
        ID3 size             4 * %0xxxxxxx
      */
        int avail = in.available();
        if (avail < BasicTagHeader.BASIC_TAG_HEADER_LEN)
            throw new IOException("Not enough data to contain a valid header.");
        Tag t = null;
        // try to read an ID3v2.x header. if it's not there, look for a ID3v1.x
        // header. If that isn't present, throw an exception.
        try
        {
            TagHeader tagHeader = TagHeaderFactory.makeHeader(in);
            // a this point, the tag header and any extended header has been
            // read.  The amount needing to be read is the size reported in the
            // header minus any extended header. This value should be
            // accurately reported in the sizeOf() method.
            int dataSize = (int)tagHeader.getDataSize();
            byte[] data = new byte[dataSize];
            int read = in.read(data);
            if (read != dataSize)
                throw new IOException("Expected " + dataSize + " bytes.");
            // unsync if necessary.
            if (tagHeader.usesUnsynchronization())
                data = ByteUtils.unsync(data);
            // generate frames out of data
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            boolean reachedNulls = false;
            Tag_v2 tag = new Tag_v2(tagHeader);
            while (bin.available() > 0 && !reachedNulls)
            {
                try
                {
                    Frame frame = FrameFactory.makeFrame(bin,tagHeader.getVersion());
                    if (frame == null)
                    {
                        if (verbose)
                            System.out.println("reached nulls. About " + bin.available() + " bytes left in tag.");
                        reachedNulls = true;
                    }
                    else
                        tag.putFrame(frame);
                }
                catch (CorruptFrameException ex)
                {
                    // something was "off" in the last frame. try to recover?
                    if(verbose)
                        System.out.println("Corrupt frame; backing out.");
                    reachedNulls = true;
                }
            }
            t = tag;
        }
        // An ID3v2.x header wasn't found.  look for a 1.x header.
        catch (NotATagException ex)
        {
            // look for a 1.x header.
            in.reset();
            in.mark(0);
            if (len < 128)
                throw new NotATagException();
            long skipped = 0;
            while (skipped < len-128)
                skipped += in.skip(len-128-skipped);
            byte[] buf = new byte[128];
            int read = in.read(buf);
            if (read != 128)
                throw new IOException("Expected 128 bytes.");
            Tag tag = new Tag_v1(buf);
            t = tag;
        }
        if (verbose) System.out.println("version: " + t.getVersion());
        return t;
    }

//    private static void scan(Reader r, File f)
//    {
//        if (f.isDirectory())
//        {
//            File[] list = f.listFiles();
//            for (int i = 0; i < list.length; i++)
//                scan(r,list[i]);
//        }
//        else
//        {
//            try
//            {
//                if (verbose) System.out.println(f.getName());
//                FileInputStream in = new FileInputStream(f);
//                Tag t = r.read(in,f.length());
//            }
//            catch (NotATagException ex)
//            {
//                System.out.println("No tag for " + f.getName());
//            }
//            catch (IOException ex)
//            {
//                System.err.print(f.getAbsolutePath() + ",,,,");
//                ex.printStackTrace();
//            }
//        }
//    }
//    public static void main(String arg[])
//    {
//        File dir = new File(arg[0]);
//        Reader reader = new Reader();
//        scan(reader,dir);
//    }
}
