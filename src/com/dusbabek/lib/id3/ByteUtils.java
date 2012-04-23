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
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

/**
 * Utilities for converting long to bytes and vice versa.  Please note that the
 * longs I deal with are only 4 bytes in size (not 8).  I could have gotten
 * away with using ints, but java has a nasty habit of using sign extension
 * which would have foiled me for large UNSIGNED values.
 */
public class ByteUtils
{
    /**
     * convert a 3 byte array to an 8 byte long. only last 3 bytes matter though.
     * @param b byte[]
     * @param offset int
     * @return long
     */
    public static long byte3ToLong(byte[] b, int offset)
    {
        long l = 0;
        for (int i = 0; i < 3; i++)
        {
            l <<= 8;
            l |= (0x00000000000000ffL & b[offset+i]);
        }
        return l;
    }

    /**
     * convert a 4 byte array to an 8 byte long. only last 4 bytes matter though.
     * @param b byte[]
     * @param offset int
     * @return long
     */
    public static long byte4ToLong(byte[] b, int offset)
    {
        long l = 0;
        for (int i = 0; i < 4; i++)
        {
            l <<= 8;
            l |= (0x00000000000000ffL & b[offset+i]);
        }
        return l;
    }

    /**
     * convert long to 3 byte array. Only the last 3 bytes of l are significant.
     * @param l long
     * @return byte[]
     */
    public static byte[] longToByte3(long l)
    {
        byte[] b = new byte[3];
        for (int i = 0; i < b.length; i++)
            b[i] = (byte)(0x000000000000ffL & (l >> ((2-i)*8)));
        return b;
    }

    /**
     * convert long to 4 byte array. Only the last 4 bytes of l are significant.
     * @param l long
     * @return byte[]
     */
    public static byte[] longToByte4(long l)
    {
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++)
            b[i] = (byte)(0x000000000000ffL & (l >> ((3-i)*8)));
        return b;
    }

    /**
     * This is unique to Id3s. It doesn't really belong here. Tag headers are
     * encoded in 4 bytes, but only the 7 least bits are significant. So take
     * the long; convert it to 4 bytes; then throw away the first bit of each
     * byte. It is IMPORTANT to get this method right because it is used to
     * report the tag size, which is crucial.
     * @param l long
     * @return long
     */
    public static long removeZeroBits(long l)
    {
        long c = 0;
        for (int i = 0; i < 4; i++)
            c |= ((0x0000000000007fL << (8 * i)) & l) >> i ;
        return c;

    }

    /**
     * Unique to Id3s. Used in putting the tag size field (4 bytes) into spec
     * format. The size field is encoded in 28 bits spread over 4 bytes (32 bits).
     * So for each 7 bits, add another at the front.  The spec claims to explain
     * whether or not that bit should be 1 or 0, but for the life of me, I couldn't
     * figure it out.  Maybe I need to read it again.  For now, until I get my
     * head on straight, I am just padding with 0.
     * @param l long
     * @return long
     */
    public static long addZeroBits(long l)
    {
        long d = 0;
        for (int i = 0; i < 4; i++)
            d |= ((0x000000000000007fL << (7 * i)) & l) << i;
        return d;

    }

    /**
     * decompress data using ZLIB.
     * @param b byte[] data to decompress.
     * @param size long expected uncompressed size.
     * @return byte[] uncompressed data.
     * @throws IOException
     */
    public static byte[] zip_expand(byte[] b, long size)
        throws IOException
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(b);
        ZipInputStream in = new ZipInputStream(bin);
        byte[] buf = new byte[(int)size];
        int read = in.read(buf);
        if (read != size)
            throw new IOException("Expected " + size + " bytes, got " + read + ".");
        in.close();
        return buf;
    }

    /**
     * compress data using ZLIB
     * @param b byte[] data to compress
     * @return byte[] compressed data.
     * @throws IOException
     */
    public static byte[] zip(byte[] b)
        throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bout);
        out.write(b);
        out.flush();
        out.finish();
        byte[] d = bout.toByteArray();
        out.close();
        return d;
    }

//    private static void examine(byte[] b)
//    {
//        for (int i = 0; i < b.length; i++)
//            System.out.print(byteToString(b[i]) + " ");
//        System.out.println("");
//    }

//    private static String nibbleToString(int lo)
//    {
//        if (lo < 10)
//            return ""+(lo);
//        else
//        {
//            switch (lo)
//            {
//                case 10: return "a";
//                case 11: return "b";
//                case 12: return "c";
//                case 13: return "d";
//                case 14: return "e";
//                case 15: return "f";
//                default: throw new RuntimeException("Unexpected " + lo);
//            }
//        }
//    }

//    private static String byteToString(int b)
//    {
//        b &= 0x00000000ff;
//        int hi = (0x000000ff & (b >> 4));
//        int lo = (0x000000ff & (b & 0x0f));
//        String s = "0x"
//            + nibbleToString(hi)
//            + nibbleToString(lo);
//        return s;
//    }

    public static String byteToString(byte b)
    {
        String s = "0x";
        for (int i = 0; i < 8; i++)
        {
            if ((b & (1 << 7-i)) > 0)
                s += "1";
            else
                s += "0";
        }
        return s;
    }


    // remove false synchronizations.
    public static byte[] unsync(byte[] data)
    {
        // ff 00 --> ff 00 00
        // ff ex --> ff 00 ex
        int finalSize = 0;
        for (int i = 0; i < data.length; i++)
        {
            if (i + 1 < data.length)
            {
                if (data[i] == 0xff && data[i + 1] == 0x00)
                    finalSize += 2;
                else if (data[i] == 0xff && (data[i + 1] & 0xe0) > 0)
                    finalSize += 2;
                else
                    finalSize++;
            }
            else
                finalSize++;
        }
        byte[] nd = new byte[finalSize];
        int pos = 0;

        for (int i = 0; i < data.length; i++)
        {
            if (i + 1 < data.length)
            {
                if ( (data[i] == 0xff && data[i + 1] == 0x00)
                    || (data[i] == 0xff && (data[i + 1] & 0xe0) > 0))
                {
                    nd[pos++] = data[i];
                    nd[pos++] = 0x00;
                }
            }
            else
                nd[pos++] = data[i];
        }

        return nd;

    }

    // find false sync replacements
    public static byte[] sync(byte[] data)
    {
        // ff 00 ex --> ff ex
        byte[] nd = new byte[data.length];
        int pos = 0;
        for (int i = 0; i < data.length; i++)
        {
            if (i + 2 < data.length && data[i] == 0xff && data[i + 1] == 0x00 && (data[i + 2] & 0xe0) > 0)
            {
                nd[pos++] = data[i];
                i += 2;
                nd[pos++] = data[i];
            }
            else
                nd[pos++] = data[i];
        }

        // ff 00 00 --> ff 00
        byte[] nd2 = new byte[data.length];
        int max = pos;
        pos = 0;
        for (int i = 0; i < max; i++)
        {
            if (i + 2 < max && nd[i] == 0xff && nd[i + 1] == 0x00 && nd[i + 2] == 00)
            {
                nd2[pos++] = nd[i++];
                nd2[pos++] = nd[i];
            }
            else
                nd2[pos++] = nd[i];
        }
        byte[] adjusted = new byte[pos];
        System.arraycopy(nd2, 0, adjusted, 0, pos);
        return adjusted;
    }

}
