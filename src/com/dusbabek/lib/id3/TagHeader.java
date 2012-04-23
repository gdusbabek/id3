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
 * Interface implemented by all tag headers.
 */
public interface TagHeader
{
    /** @return tag flags */
    public byte getFlags();

    /** @return size of tag */
    public long getTagSize();

    /** @return id3 version */
    public byte getVersion();

    /** @return size of header */
    public int sizeOf();

    /** @return size of all frames (no headers included) */
    public long getDataSize();

    /** set data size */
    public void setDataSize(long l);

    /** write header to stream */
    public void write(OutputStream out) throws IOException;

    /** @return true if unsynchronization is used. */
    public boolean usesUnsynchronization();
}
