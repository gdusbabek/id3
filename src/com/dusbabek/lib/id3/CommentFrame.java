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
 * Specialized frame used to store comments.
 * @todo do not ignore text encoding and language values.
 * I don't understand what makes a comment so special.  Couldn't a text frame
 * have been used? Notice that my implementation more or less ignores the
 * description field.
 */
public class CommentFrame
    extends Frame
{
    private String description = "";
    private String comment = "";

    /**
     * create a comment frame.
     * @param comment String the comment.
     * @param version int tag version.
     * @throws IOException
     */
    public CommentFrame(String comment, int version)
        throws IOException
    {
        this(makeHeader(comment,version),makeData(comment,version));
    }

    /**
     * create a comment frame from a header and raw data.
     * @param header FrameHeader
     * @param data byte[]
     * @throws IOException
     */
    public CommentFrame(FrameHeader header, byte[] data)
        throws IOException
    {
        super(header, data);
        /*
        2.2
         Comment                   "COM"
         Frame size                $xx xx xx

         Text encoding             $xx
         Language                  $xx xx xx
         Short content description <textstring> $00 (00)
         The actual text           <textstring>

         2.3
         <Header for 'Comment', ID: "COMM">
         Text encoding	$xx
         Language 	$xx xx xx
         Short content descrip.	<text string according to encoding> $00 (00)
         The actual text 	<full text string according to encoding>
         */
        // the point is that they are the same for 2.x tags.

        // separate the descriptio and text.
        // find the null that demarks desc and text.
        boolean found = false;
        for (int i = 4; i < data.length && !found; i++)
        {
            if (data[i] == 0)
            {
                found = true;
                description = new String(data,4,i-4);
                comment = new String(data,i+1,data.length-(i+1));
            }
        }
    }

    /** @return the comment description */
    public String getDescription() { return description; }

    /** @return the comment. */
    public String getComment() { return comment; }

    // static method to generate a comment frame header.
    private static FrameHeader makeHeader(String comment, int version)
        throws IOException
    {
        byte[] b_comment = comment.getBytes();
        // generate header.
        int len = 5 + b_comment.length;
        FrameHeader fh = null;
        if (version == 2)
            fh = new FrameHeader_v2_2(FrameType.getId(FrameType.COMMENT,version));
        else if (version == 3)
            fh = new FrameHeader_v2_3(FrameType.getId(FrameType.COMMENT,version));
        else if (version == 4)
            fh = new FrameHeader_v2_4(FrameType.getId(FrameType.COMMENT,version));
        else
            throw new IOException("Unsupported version: " + version);
        fh.setFrameSize(len);
        return fh;
    }

    // static method to generate comment data. note that the description is
    // empty.
    private static byte[] makeData(String comment, int version)
        throws IOException
    {
        // generate data
        byte[] b_comment = comment.getBytes();
        int len = 5 + b_comment.length;
        byte[] data = new byte[len];
        data[0] = 0;
        data[1] = data[2] = data[3] = 0;
        data[4] = 0;
        System.arraycopy(b_comment,0,data,5,b_comment.length);
        return data;
    }
}
