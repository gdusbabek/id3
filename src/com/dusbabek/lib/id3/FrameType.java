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

/**
 * Constants for dealing with all kinds of frames. Also provides some static
 * methods for getting the textual frame ID of a given frame (they differ
 * across spec versions).
 */
public class FrameType
{
    private static final boolean verbose = System.getProperty("id3.verbose") != null;
    private static int inc = 0;

    public static final int COMMENT = inc++;
    public static final int ALBUM = inc++;
    public static final int BEATS_PER_MINUTE = inc++;
    public static final int COMPOSER = inc++;
    public static final int GENRE = inc++;

    public static final int COPYRIGHT_MESSAGE = inc++;
    public static final int DATE = inc++;
    public static final int PLAYLIST_DELAY = inc++;
    public static final int ENCODED_BY = inc++;
    public static final int LYRICIST = inc++;

    public static final int FILE_TYPE = inc++;
    public static final int TIME = inc++;
    public static final int CONTENT_GROUP = inc++;
    public static final int SONGNAME = inc++;
    public static final int SUBTITLE = inc++;

    public static final int INITIAL_KEY = inc++;
    public static final int LANGUAGE = inc++;
    public static final int LENGTH = inc++;
    public static final int MEDIA_TYPE = inc++;
    public static final int ORIGINAL_TITLE = inc++;

    public static final int ORIGINAL_FILE_NAME = inc++;
    public static final int ORIGINAL_LYRICIST = inc++;
    public static final int ORIGINAL_ARTIST = inc++;
    public static final int ORIGINAL_RELEASE_YEAR = inc++;
    public static final int FILE_OWNER = inc++;

    public static final int ARTIST = inc++;
    public static final int ORCHESTRA = inc++;
    public static final int CONDUCTOR = inc++;
    public static final int INTERPRETED = inc++;
    public static final int PART_OF_SET = inc++;

    public static final int PUBLISHER = inc++;
    public static final int TRACK = inc++;
    public static final int RECORDING_DATES = inc++;
    public static final int STATION_NAME = inc++;
    public static final int STATION_OWNER = inc++; //

    public static final int SIZE = inc++;
    public static final int ISRC = inc++;
    public static final int ENCODING_SETTING = inc++;
    public static final int YEAR = inc++;
    public static final int USER_DEFINED_TEXT = inc++;

    public static final int ENCODING_TIME = inc++;
    public static final int RECORDING_TIME = inc++;
    public static final int RELEASE_TIME = inc++;
    public static final int TAGGING_TIME = inc++;
    public static final int INVOLVED_PEOPLE_LIST = inc++;

    public static final int MUSICIAN_CREDITS_LIST = inc++;
    public static final int MOOD = inc++;
    public static final int PRODUCED_NOTICE = inc++;
    public static final int ALBUM_SORT_ORDER = inc++;
    public static final int PERFORMER_SORT_ORDER = inc++;

    public static final int TITLE_SORT_ORDER = inc++;
    public static final int SET_SUB_TITLE = inc++;
    public static final int ORIGINAL_RELEASE_TIME = inc++;


    private static final String[] V2_TAGS = {
      "COM","TAL","TBP","TCM","TCO",
      "TCR","TDA","TDY","TEN","TXT",
      "TFT","TIM","TT1","TT2","TT3",
      "TKE","TLA","TLE","TMT","TOT",
      "TOF","TOL","TOA","TOR","   ", /* TOWN */
      "TP1","TP2","TP3","TP4","TPA",
      "TPB","TRK","TRD","   ","   ", /** TRSN, TRSO */
      "TSI","TRC","TSS","TYE","TXX"
    };
    private static final String[] V3_TAGS = {
      "COMM","TALB","TBPM","TCOM","TCON",
      "TCOP","TDAT","TDLY","TENC","TEXT",
      "TFLT","TIME","TIT1","TIT2","TIT3",
      "TKEY","TLAN","TLEN","TMED","TOAL",
      "TOFN","TOLY","TOPE","TORY","TOWN",
      "TPE1","TEP2","TPE3","TPE4","TPOS",
      "TPUB","TRCK","TRDA","TRSN","TRSO",
      "TSIZ","TSRC","TSSE","TYER","TXXX"
    };
    private static final String[] V4_TAGS = {
      "COMM","TALB","TBPM","TCOM","TCON",
      "TCOP","TDAT","TDLY","TENC","TEXT",
      "TFLT","TIME","TIT1","TIT2","TIT3",
      "TKEY","TLAN","TLEN","TMED","TOAL",
      "TOFN","TOLY","TOPE","TORY","TOWN",
      "TPE1","TEP2","TPE3","TPE4","TPOS",
      "TPUB","TRCK","TRDA","TRSN","TRSO",
      "TSIZ","TSRC","TSSE","TYER","TXXX",

      "TDEN","TDRC","TDRL","TDTG","TIPL",
      "TMCL","TMOO","TPRO","TSOA","TSOP",
      "TSOT","TSST","TDOR"
    };


    /**
     * return the textual frame id (3 or 4 characters) as specified by type and
     * version.
     * @param type int should be a constant as declared in this class.
     * @param version int 2, 3 or 4.
     * @return String
     */
    public static String getId(int type, int version)
    {
        if (version == 2)
            return V2_TAGS[type];
        else if (version == 3)
            return V3_TAGS[type];
        else if (version == 4)
            return V4_TAGS[type];
        else
            throw new RuntimeException("Unexpected ID3 version.");
    }
}
