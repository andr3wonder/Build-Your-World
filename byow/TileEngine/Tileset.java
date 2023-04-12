package byow.TileEngine;

import java.awt.Color;
import java.util.Arrays;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you", "./byow/images/you2.png");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", "./byow/images/Sprites_01.png");
    public static final TETile FLOOR = new TETile(' ', new Color(136, 118, 118), new Color(0, 0, 0),
            "floor");
    public static final TETile HALL = new TETile(' ', new Color(136, 118, 118), new Color(0, 0, 0),
            "hallway");
    public static final TETile LIGHT = new TETile('◉', Color.white, new Color(0, 73, 140), "light");
    public static final TETile LIGHT1 = new TETile(' ', Color.black, new Color(0, 60, 115), "floor");
    public static final TETile LIGHT2 = new TETile(' ', Color.black, new Color(0, 47, 90), "floor");
    public static final TETile LIGHT3 = new TETile(' ', Color.black, new Color(2, 34, 75), "floor");
    public static final TETile LIGHT4 = new TETile(' ', Color.black, new Color(0, 21, 50), "floor");
    public static final TETile LIGHT5 = new TETile(' ', Color.black, new Color(0, 8, 25), "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile OUT = new TETile('≈', Color.blue, Color.black, "outdoors", "./byow/images/darkrock.png");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", "./byow/images/tree.png");
}


