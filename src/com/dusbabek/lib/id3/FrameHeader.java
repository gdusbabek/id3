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

import java.io.OutputStream;
import java.io.IOException;

/**
 * Interface for all frame headers.
 */
public interface FrameHeader
{
    /** @return the frame id */
    public String getId();

    /** @return the size of frame data */
    public int getSize();

    /** set the frame data size */
    public void setFrameSize(int size);

    /** @return the size of the frame header (includes extended headers) */
    public int sizeOf();

    /** write header to a stream */
    public void write(OutputStream out) throws IOException;

    /** @return true if frame is compressed */
    public boolean compressed();

    /** @return the uncompressed size of the frame data */
    public long getUncompressedSize();
}
