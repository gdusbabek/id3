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
 * ID3v2.3 header. They only get more complicated.
 */
public class TagHeader_v2_3
    extends TagHeader_v2_2
{
    private ExtendedTagHeader extHeader = null;

    /** constructs an empty header */
    public TagHeader_v2_3()
        throws IOException
    {
        super(makeGenericHeader());
    }

    /**
     * construct a header from 10 bytes and an input stream.
     * @param data byte[] a basic header.
     * @param in InputStream
     * @throws IOException
     */
    public TagHeader_v2_3(byte[] data, InputStream in)
        throws IOException
    {
        super(data);
        if (usesExtendedHeader())
            extHeader = makeExtendedHeader(in);
    }

    /**
     * create an extended header. Extended header formats differ across versions
     * so the implementation of creating them needs to be specific to the
     * header implementations.
     * @param in InputStream
     * @return ExtendedTagHeader
     * @throws IOException
     */
    protected ExtendedTagHeader makeExtendedHeader(InputStream in)
        throws IOException
    {
        return new ExtendedTagHeader_v2_3(in);
    }

    /** @return true if an extended header is present */
    public boolean usesExtendedHeader()
    {
        return (getFlags() & 0x40) > 0;
    }

    /** @return true if this tag is experimental. */
    public boolean usesExperimental()
    {
        return (getFlags() & 0x20) > 0;
    }

    /** {@inheritDoc} */
    public int sizeOf()
    {
        int sz = super.sizeOf();
        if (extHeader != null)
            sz += extHeader.sizeOf();
        return sz;
    }

    /** {@inheritDoc} */
    public long getDataSize()
    {
        return getTagSize() - (extHeader == null ? 0 : extHeader.sizeOf());
    }

    /** {@inheritDoc} */
    public void setDataSize(long l)
    {
        // tagSize = l - extHeader.len
        this.setTagSize(l - (extHeader == null ? 0 : extHeader.sizeOf()));
    }

    /** @todo what about compression? */

    /** {@inheritDoc} */
    public void write(OutputStream out)
        throws IOException
    {
        /** @assume that size is set to extHeader len + data len. */
        super.write(out);
        if (extHeader != null)
            extHeader.write(out);
    }

    // make the guts of an id3v2.3 header.
    private static byte[] makeGenericHeader()
    {
        return new byte[]{
            'I','D','3', // tag identifier.
            3, // indicats ID3v2.3
            0, // no revision
            0, // no flags.
            0,0,0,0}; // no size.
    }

}
