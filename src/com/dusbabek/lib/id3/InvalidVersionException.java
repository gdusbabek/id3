/*

(c) Copyright 2004 Gary Dusbabek gdusbabek@gmail.com

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
 * Thrown when an invalid Id3 version is detected in an id3 tag header.
 */
public class InvalidVersionException
    extends IOException
{
    private byte version;

    /**
     * constructor.
     * @param version byte invalid version.
     */
    public InvalidVersionException(byte version)
    {
        super("Invalid version: " + version);
        this.version = version;
    }

    /** @return invalid version */
    public byte getVersion() { return version; }
}
