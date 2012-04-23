package com.dusbabek.lib.id3;

import java.io.*;

/**
 * One-stop shopping for editing a tag in a file.  Simple values only.
 */
public class Id3
{
    public static final int NO_STATE = 0;
    public static final int MISSING = 1;
    public static final int CORRUPT = 2;
    public static final int INVALID_VERSION = 3;
    public static final int OTHER_ERROR = 4;
    public static final int OK = 99;

    private File file = null;
    private Tag tag = null;
    private int state = NO_STATE;

    public Id3(File file)
    {
        this.file = file;
    }

    public void setTitle(String title)
    {
        ensureTag();
        tag.putFrame(FrameType.SongName,title);
    }

    public void setArtist(String artist)
    {
        ensureTag();
        tag.putFrame(FrameType.Artist,artist);
    }

    public void setAlbum(String album)
    {
        ensureTag();
        tag.putFrame(FrameType.Album,album);
    }

    public void setYear(String year)
    {
        ensureTag();
        tag.putFrame(FrameType.Year,year);
    }

    public void setGenre(String genre)
    {
        ensureTag();
        tag.putFrame(FrameType.Genre,genre);
    }

    public void setTrack(String track)
    {
        ensureTag();
        tag.putFrame(FrameType.Track,track);
    }

    public void rewrite()
        throws IOException
    {
        InputStream in = new FileInputStream(file);
        File outFile = new File(file.getParentFile(),file.getName() + ".tmp");
        OutputStream out = new FileOutputStream(outFile,false);
        long bytesToRead = 0;
        // we need to read past the old tag.
        switch (state)
        {
            case NO_STATE: throw new IOException("NO STATE");
            case INVALID_VERSION:
            case MISSING: // write in front
                bytesToRead = file.length();
                break;
            case CORRUPT: // rip out everything before the first sync signal.
                in.close();
                bytesToRead = file.length() - skipToSync(file);
                in = new FileInputStream(file);
                in.skip(file.length() - bytesToRead);
                break;
            case OK:
                // proceed normally.
                in.skip(tag.originalTagLength());
                bytesToRead = file.length() - tag.originalTagLength();
                break;
            default: throw new IOException("Invalid state " + state);
        }

        long bytesRead = 0;
        while (bytesRead < bytesToRead)
        {
            int avail = in.available();
            if (avail < 0)
                throw new IOException("expected some bytes.");
            else if (avail == 0)
                continue;
            byte[] buf = new byte[avail];
            int read = in.read(buf);
            if (read == 0)
                continue;
            if (read < 0)
                throw new IOException("expected some bytes.");
            bytesRead += read;
            out.write(buf);
        }
        out.flush();
        in.close();
        out.close();
        if (!file.delete())
            throw new IOException("Could not delete original file.");
        if (!outFile.renameTo(file))
            throw new IOException("Could not rename temp file.");
    }



    private long skipToSync(File f)
        throws IOException
    {
        FileInputStream in = new FileInputStream(f);
        byte[] b = new byte[1];
        long size = f.length();
        int pos = 0;
        boolean found = false;
        while (pos < size && !found)
        {
            pos += in.read(b);
            if (b[0] == 0xff)
            {
                pos +=in.read(b);
                if ((b[0] & 0xe0) > 0)
                {
                    found = true;
                    break;
                }
            }
        }
        in.close();
        if (!found)
            throw new IOException("sync signal not found.");
        return pos-2;
    }

    // precondition: state != NO_STATE and read() has been called.
    private void ensureTag()
    {
        if (tag == null)
        {
            try
            {
                tag = new Tag();
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    public void read()
        throws IOException
    {
        if (file == null)
            throw new IOException("file not set.");
        Reader reader = new Reader();
        InputStream in = new FileInputStream(file);
        state = OTHER_ERROR; // catch all in case generic IOEx is thrown.
        try
        {
            tag = reader.read(in);
            state = OK;
        }
        catch (NotATagException ex)
        {
            state = MISSING;
        }
        catch (InvalidVersionException ex)
        {
            state = INVALID_VERSION;
        }
        catch (CorruptTagException ex)
        {
            state = CORRUPT;
        }
    }
}
