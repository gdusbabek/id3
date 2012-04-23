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
 * Debugging class used to scan a directory and evaulate id3 tags.
 */
public class FsScanner
{
    private static File DEST_DIR = System.getProperty("dest") == null ? null : new File(System.getProperty("dest"));
    private static long dumpCount = 0;
    private static long dumpTime = 0;

    private File f;
    private Reader r = new Reader();

    private int noTag = 0;
    private int badVersion = 0;
    private int otherErr = 0;
    private int ok = 0;

    public FsScanner(File f)
    {
        this.f = f;
    }

    public void scan()
    {
        try
        {
            scan(f);
            if (DEST_DIR != null)
            {
                System.out.println("dumped " + dumpCount + " in " + dumpTime);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        System.out.println("ok="+ok+" noTag="+noTag+" badVersion="+badVersion+" other="+otherErr);
    }

    private void scan(File f)
        throws IOException
    {
        if (f.isDirectory())
        {
            File[] list = f.listFiles();
            for (int i = 0; i < list.length; i++)
            {
                if (list[i].isDirectory())
                    scan(list[i]);
                else if (list[i].getName().toLowerCase().endsWith(".mp3") && !list[i].getName().startsWith("."))
                    scan(list[i]);
            }
        }
        else
        {
            FileInputStream in = null;
            try
            {
                in = new FileInputStream(f);
                Tag id3 = null;
                try
                {
                    r.read(in);
                    System.out.println("OK : " + f.getAbsolutePath());
                }
                catch (CorruptTagException ex)
                {
                    System.err.println("CORRUPT: " + f.getAbsolutePath());
                    System.err.println("         " + ex.getMessage());
                    return;
                }
                catch (InvalidVersionException ex)
                {
                    System.err.println("BAD_VERSION: " + f.getAbsolutePath());
                    return;
                }
                catch (NotATagException ex)
                {
                    System.err.println("NO_TAG: " + f.getAbsolutePath());
                    return;
                }
                if (DEST_DIR != null)
                {
                    long start = System.currentTimeMillis();
                    File of = new File(DEST_DIR,"test-" + f.getName());
                    FileOutputStream out = new FileOutputStream(of,false);
                    id3.write(out);
                    in.close();
                    in = new FileInputStream(f);
                    long size = f.length();
                    long passed = id3.originalTagLength();
                    in.skip(passed);
                    while (passed < size)
                    {
                        byte[] buf = new byte[in.available()];
                        int read = in.read(buf);
                        out.write(buf);
                        passed += read;
                    }
                    out.flush();
                    out.close();
                    long end = System.currentTimeMillis();
                    dumpCount++;
                    dumpTime += (end-start);
                }
                ok++;
            }
            catch (NotATagException ex)
            {
                System.err.println("no tag " + f.getName());
                noTag++;
            }
            catch (InvalidVersionException ex)
            {
                badVersion++;
            }
            catch (IOException ex)
            {
                System.err.print(f.getName() + " " );
                ex.printStackTrace();
                otherErr++;
            }
            finally
            {
                try { in.close(); } catch (Exception ex) { }
            }
        }
    }

    public static void main(String args[])
    {
        String source = System.getProperty("source");
        String dest = System.getProperty("dest");

        File f = new File(source);
        FsScanner fs = new FsScanner(f);
        fs.scan();
    }
}
