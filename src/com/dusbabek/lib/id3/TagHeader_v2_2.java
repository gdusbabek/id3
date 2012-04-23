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
 * ID3v2.2 tag header. interprets flags correctly.
 */
public class TagHeader_v2_2
        extends BasicTagHeader
{
    public TagHeader_v2_2(byte[] data)
        throws IOException
    {
        super(data);
    }

    /** @return true if this tag uses unsynchronization */
    public boolean usesUnsynchronization()
    {
        return (getFlags() & 0x80) > 0;
    }

    /** @return true if this tag uses compression */
    public boolean usesCompression()
    {
        return (getFlags() & 0x40) > 0;
    }
}
