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
 * Tag header for ID3v2.4. Doesn't add much to v2.3
 * @todo support footers in the reader.
 */
public class TagHeader_v2_4
    extends TagHeader_v2_3
{
    private ExtendedTagHeader_v2_4 extHeader = null;

    /**
     * constructs a header from 10 bytes and input stream.
     * @param data byte[]
     * @param in InputStream
     * @throws IOException
     */
    public TagHeader_v2_4(byte[] data, InputStream in)
        throws IOException
    {
        super(data,in);
    }

    /** {@inheritDoc} */
    protected ExtendedTagHeader makeExtendedHeader(InputStream in)
        throws IOException
    {
        return new ExtendedTagHeader_v2_4(in);
    }

    /** @return true if a footer is specified. */
    public boolean usesFooter()
    {
        return (getFlags() & 0x10) > 0;
    }
}
