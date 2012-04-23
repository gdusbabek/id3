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

import java.util.Hashtable;
import java.lang.reflect.*;

/**
 * Simplifies dealing with ID3v1.x genres.
 */
public class Genre
{
    // The following genres are defined in ID3v1
    public static final byte Blues = 0;
    public static final byte ClassicRock = 1;
    public static final byte Country = 2;
    public static final byte Dance = 3;
    public static final byte Disco = 4;
    public static final byte Funk = 5;
    public static final byte Grunge = 6;
    public static final byte HipHop = 7;
    public static final byte Jazz = 8;
    public static final byte Metal = 9;
    public static final byte NewAge = 10;
    public static final byte Oldies = 11;
    public static final byte Other = 12;
    public static final byte Pop = 13;
    public static final byte RandB = 14;
    public static final byte Rap = 15;
    public static final byte Reggae = 16;
    public static final byte Rock = 17;
    public static final byte Techno = 18;
    public static final byte Industrial = 19;
    public static final byte Alternative = 20;
    public static final byte Ska = 21;
    public static final byte DeathMetal = 22;
    public static final byte Pranks = 23;
    public static final byte Soundtrack = 24;
    public static final byte EuroTechno = 25;
    public static final byte Ambient = 26;
    public static final byte TripHop = 27;
    public static final byte Vocal = 28;
    public static final byte JazzFunk = 29;
    public static final byte Fusion = 30;
    public static final byte Trance = 31;
    public static final byte Classical = 32;
    public static final byte Instrumental = 33;
    public static final byte Acid = 34;
    public static final byte House = 35;
    public static final byte Game = 36;
    public static final byte SoundClip = 37;
    public static final byte Gospel = 38;
    public static final byte Noise = 39;
    public static final byte AlternRock = 40;
    public static final byte Bass = 41;
    public static final byte Soul = 42;
    public static final byte Punk = 43;
    public static final byte Space = 44;
    public static final byte Meditative = 45;
    public static final byte InstrumentalPop = 46;
    public static final byte InstrumentalRock = 47;
    public static final byte Ethnic = 48;
    public static final byte Gothic = 49;
    public static final byte Darkwave = 50;
    public static final byte TechnoIndustrial = 51;
    public static final byte Electronic = 52;
    public static final byte PopFolk = 53;
    public static final byte Eurodance = 54;
    public static final byte Dream = 55;
    public static final byte SouthernRock = 56;
    public static final byte Comedy = 57;
    public static final byte Cult = 58;
    public static final byte Gangsta = 59;
    public static final byte Top40 = 60;
    public static final byte ChristianRap = 61;
    public static final byte PopFunk = 62;
    public static final byte Jungle = 63;
    public static final byte NativeAmerican = 64;
    public static final byte Cabaret = 65;
    public static final byte NewWave = 66;
    public static final byte Psychadelic = 67;
    public static final byte Rave = 68;
    public static final byte Showtunes = 69;
    public static final byte Trailer = 70;
    public static final byte LoFi = 71;
    public static final byte Tribal = 72;
    public static final byte AcidPunk = 73;
    public static final byte AcidJazz = 74;
    public static final byte Polka = 75;
    public static final byte Retro = 76;
    public static final byte Musical = 77;
    public static final byte RockRoll = 78;
    public static final byte HardRock = 79;

//   The following genres are Winamp extensions
    public static final byte Folk = 80;
    public static final byte FolkRock = 81;
    public static final byte NationalFolk = 82;
    public static final byte Swing = 83;
    public static final byte FastFusion = 84;
    public static final byte Bebob = 85;
    public static final byte Latin = 86;
    public static final byte Revival = 87;
    public static final byte Celtic = 88;
    public static final byte Bluegrass = 89;
    public static final byte Avantgarde = 90;
    public static final byte GothicRock = 91;
    public static final byte ProgressiveRock = 92;
    public static final byte PsychedelicRock = 93;
    public static final byte SymphonicRock = 94;
    public static final byte SlowRock = 95;
    public static final byte BigBand = 96;
    public static final byte Chorus = 97;
    public static final byte EasyListening = 98;
    public static final byte Acoustic = 99;
    public static final byte Humour = 100;
    public static final byte Speech = 101;
    public static final byte Chanson = 102;
    public static final byte Opera = 103;
    public static final byte ChamberMusic = 104;
    public static final byte Sonata = 105;
    public static final byte Symphony = 106;
    public static final byte BootyBass = 107;
    public static final byte Primus = 108;
    public static final byte PornGroove = 109;
    public static final byte Satire = 110;
    public static final byte SlowJam = 111;
    public static final byte Club = 112;
    public static final byte Tango = 113;
    public static final byte Samba = 114;
    public static final byte Folklore = 115;
    public static final byte Ballad = 116;
    public static final byte PowerBallad = 117;
    public static final byte RhythmicSoul = 118;
    public static final byte Freestyle = 119;
    public static final byte Duet = 120;
    public static final byte PunkRock = 121;
    public static final byte DrumSolo = 122;
    public static final byte Acapella = 123;
    public static final byte EuroHouse = 124;
    public static final byte DanceHall = 125;

    public static final byte Indie = (byte)0x83; // 131

    private static String[] GENRE_STRINGS = null;
    private static Hashtable stoi = new Hashtable();
    private static Hashtable itos = new Hashtable();

    // fill up the hashes. Use reflection.
    static
    {
        // I can get away with this because this number follows the actual
        // values.
        GENRE_STRINGS = new String[126];
        int gsPos = 0;

        // for each of the byte fields in this class, create a corresponding
        // entry in the GENRE_STRINGS array.  Also add corresponding values
        // to the hashes to enable easy lookups later on.
        Field[] fields = Genre.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            if (fields[i].getType().equals(byte.class))
            {
                try
                {
                    String name = fields[i].getName();
                    GENRE_STRINGS[gsPos] = name;
                    int b_value = 0x000000ff & (int)fields[i].getByte(Genre.class);
                    Integer value = new Integer(b_value);
                    stoi.put(name,value);
                    itos.put(value,name);
                }
                catch (IllegalAccessException ex)
                {
                    ex.printStackTrace();
                }
                catch (IllegalArgumentException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * get the string (name) of a genre from its byte value.
     * @param b byte
     * @return String
     * @throws IllegalArgumentException
     */
    public static String getString(byte b)
      throws IllegalArgumentException
    {
        Integer value = new Integer(0x000000ff & (int)b);
        if (value == null)
            throw new IllegalArgumentException("Genre " + b + " does not exist.");
        return (String)itos.get(value);
    }

    /**
     * get the byte value of a genre based on its name.
     * @param name String
     * @return byte
     * @throws IllegalArgumentException
     */
    public static byte getByte(String name)
      throws IllegalArgumentException
    {
        Integer value = (Integer)stoi.get(name);
        if (value == null)
            throw new IllegalArgumentException("Genre " + name + " does not exist.");
        return value.byteValue();
    }
}
