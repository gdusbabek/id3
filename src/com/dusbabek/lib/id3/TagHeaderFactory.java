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
 * Factory class for creating tag headers.
 */
public class TagHeaderFactory
{
    /**
     * create a tag header of the correct version. The version is specified
     * somewhere in the input stream.
     * @param in InputStream
     * @return TagHeader
     * @throws IOException
     */
    public static TagHeader makeHeader(InputStream in)
        throws IOException
    {
        byte[] b_basicHeader = new byte[BasicTagHeader.BASIC_TAG_HEADER_LEN];
        int read = in.read(b_basicHeader);
        if (read != BasicTagHeader.BASIC_TAG_HEADER_LEN)
            throw new IOException("Expected " + BasicTagHeader.BASIC_TAG_HEADER_LEN + " bytes.");
        if (b_basicHeader[0] != 'I' || b_basicHeader[1] != 'D' || b_basicHeader[2] != '3')
            throw new NotATagException();
        byte version = b_basicHeader[3];
        if (version == 2) // id3v2.2
            return new TagHeader_v2_2(b_basicHeader);
        else if (version == 3)
            return new TagHeader_v2_3(b_basicHeader,in);
        else if (version == 4)
            return new TagHeader_v2_4(b_basicHeader,in);
        else
            throw new IOException("Unexpected version " + version);
    }
}
