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

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

/**
 * The highest level of dealing with id3s. At this level, you don't have to
 * know about ID3 versions, frames or anything like that. You just open up
 * a file, request its tag, make changes and write to a different file.
 */
public class Mp3File
{
    private Tag tag = null;
    private File file = null;
    private Reader reader = null;

    /**
     * constructor. attempts to read a tag. If none is present, a default
     * ID3v2.3 tag is created with no frames.
     * @param file File
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Mp3File(File file)
        throws FileNotFoundException, IOException
    {
        this.file = file;
        reader = new Reader();
        try
        {
            FileInputStream fin = new FileInputStream(file);
            tag = reader.read(fin, file.length());
        }
        catch (NotATagException ex)
        {
            System.err.println(ex.getMessage());
            tag = new Tag_v2();
        }
        catch (FileNotFoundException ex)
        {
            throw ex;
        }
        catch (IOException ex)
        {
            throw ex;
        }
    }

    /**
     * set the tag for this file.
     * @param tag Tag
     */
    public void setTag(Tag tag) { this.tag = tag; }

    /**
     * get the tag for this file.  Rather than create a bunch of wrapper methods
     * for the set/get methods in Tag, I provoide this one.  It makes things
     * cleaner.  Thus, if you REALLY wanto to deal with frames, you feasibly
     * could.
     * @return Tag
     */
    public Tag getTag() { return tag; }

    /**
     * rewrite to a different file.
     * @param f File
     * @throws IOException
     */
    public void write(File f)
        throws IOException
    {
        if (f.equals(file))
            throw new IOException("Cannot rewrite source file.");
        if (tag.getVersion().startsWith("1."))
            write1x(f);
        else
            write2x(f);
    }

    // support for writing ID3v1.x tags.
    private void write1x(File f)
        throws IOException
    {
        long skip = file.length() - 128;
        FileOutputStream out = new FileOutputStream(f,false);
        FileInputStream in = new FileInputStream(file);
        long written = 0;
        while (written < skip)
        {
            byte[] buf = new byte[(int)(skip-written)];
            int read = in.read(buf);
            written += read;
            out.write(buf,0,read);
        }
        tag.write(out);
        out.flush();
        out.close();
        in.close();
    }

    // support for writing ID3v2.x tags.
    private void write2x(File f)
        throws IOException
    {
        long skip = skipToSync(file);
        FileOutputStream out = new FileOutputStream(f,false);
        tag.write(out);
        FileInputStream in = new FileInputStream(file);
        long skipped = 0;
        while (skipped < skip)
            skipped += in.skip(skip-skipped);
        long written = 0;
        while (written < file.length() - skip)
        {
            byte[] buf = new byte[in.available()];
            written += in.read(buf);
            out.write(buf);
        }
        out.flush();
        out.close();
        in.close();
    }

    // searches for the sync signal in an mp3. It tries to be smart by first
    // looking for an ID3 tag so it can get a rough estimate of how much to
    // skip without checking bytes.  If a sync isn't found at the end of the
    // tag, it will continue reading until a sync is found or the end of the
    // file is reached.
    private static long skipToSync(File f)
        throws IOException
    {
        long id3Len = 0;
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(f);
            TagHeader th = TagHeaderFactory.makeHeader(in);
            id3Len = th.getTagSize() + 10;
        }
        catch (NotATagException ex)
        {
            // that's fine. lets just look for a sync signal then.
        }
        finally
        {
            in.close();
        }
        in = new FileInputStream(f);
        long skipped = 0;
        while (skipped < id3Len)
            skipped += in.skip(id3Len-skipped);
        // at this point we should have a valid sync signal.  But lets read
        // until we have found it fore sure.
        byte[] buf = new byte[256]; // should be close eh?
        boolean found = false;
        boolean waiting = false;
        while (skipped < f.length() && !found)
        {
            int read = in.read(buf);
            if (read < 0)
                throw new IOException("Expected some data.");
            for (int i = 0; !found && i < read; i++)
            {
                if (buf[i] == -1) // 0xff
                {
                    if (waiting)
                        found = true;
                    else
                        waiting = true;
                }
                else if (waiting && (buf[i] & 0xe0) == 0xe0)
                    found = true;
                if (found)
                    id3Len = skipped + i - 1;
            }
            skipped += read;
        }
        in.close();
        if (!found)
            throw new IOException("Could not find sync signal. " + skipped);
        return id3Len;
    }
}
