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
 * Interface that all tags must implement. Note that at this level things
 * are frame agnostic.
 **/
public interface Tag
{
    /** ID3v1.0 */
    public static final String V1_0 = "1.0";

    /** ID3v1.1 */
    public static final String V1_1 = "1.1";

    /** ID3v2.2 */
    public static final String V2_2 = "2.2";

    /** ID3v2.3 */
    public static final String V2_3 = "2.3";

    /** ID3v2.4 */
    public static final String V2_4 = "2.4";

    /** @return ablum name */
    public String getAlbum();

    /** @return artist name */
    public String getArtist();

    /** @return comment */
    public String getComment();

    /** @return genre */
    public String getGenre();

    /** @return title (song name) */
    public String getTitle();

    /** @return track number */
    public String getTrack();

    /** @return year */
    public String getYear();

    /** set album name */
    public void setAlbum(String s);

    /** set artist name */
    public void setArtist(String s);

    /** set comment */
    public void setComment(String s);

    /** set genre */
    public void setGenre(String s);

    /** set title (song name) */
    public void setTitle(String s);

    /** set track */
    public void setTrack(String s);

    /** set year */
    public void setYear(String s);

    public void write(OutputStream out)
      throws IOException;

    /** @return the stringified version: "1.1", "2.3", etc. */
    public String getVersion();

}
