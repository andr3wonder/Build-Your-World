package byow.Core;

import byow.InputDemo.*;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 32;
    private static final int DEFAULTFONT = 14;
    private int seed;
    private boolean load = true;
    private Avatar avatar;
    private enum State {
        HOME,
        GAME,
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource is = new KeyboardInputSource();
        startGame(is, true);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    // need changing
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        InputSource is = new StringInputDevice(input);
        return startGame(is, false);
    }

    public TETile[][] startGame(InputSource is, Boolean seedFromUser) {
        TERenderer ter = null;
        WorldGenerator world = null;
        boolean colon = false;
        boolean turnLightsOff = false;
        if (seedFromUser) {
            ter = new TERenderer();
            ter.initialize(WIDTH, HEIGHT);
            drawHome();
        }
        State state = State.HOME;
        while (is.possibleNextInput()) {
            char typed = is.getNextKey();
            switch (state) {
                case HOME:
                    if (typed == 'q' || typed == 'Q') {
                        System.exit(0);
                        return null;
                    } else if (typed == 'n' || typed == 'N') {
                        seed = typeSeed(is);
                        Font font = new Font("Monaco", Font.BOLD, DEFAULTFONT);
                        StdDraw.setFont(font);
                        world = new WorldGenerator(seed);
                        avatar = new Avatar(WIDTH, HEIGHT, world.returnWorld(), world.returnRandom(),
                                world.returnWorldStatus());
                        avatar.addAvatarRandom();
                        world.getAvatarInfo(avatar);
                        if (seedFromUser) {
                            while (!StdDraw.hasNextKeyTyped()) {
                                ter.renderFrame(world.returnWorld(), getMouseX(), getMouseY(), currentTime());
//                                drawHUD(getMouseX(), getMouseY(), world.returnWorld(), currentTime());
                            }
                        }
                        state = State.GAME;
                    } else if (typed == 'l' || typed == 'L') {
                        // load saved game
                        if (turnLightsOff != load) {
                            turnLightsOff = load;
                        }
                        world = new WorldGenerator(load);
                        avatar = world.returnAvatar();
                        seed = world.returnSeed();
                        if (seedFromUser) {
                            while (!StdDraw.hasNextKeyTyped()) {
                                ter.renderFrame(world.returnWorld(), getMouseX(), getMouseY(), currentTime());
                            }
                        }
                        state = State.GAME;
                    }
                    break;
                case GAME:
                    if (colon && (typed == 'Q' || typed == 'q')) {
                        world.saveGame();
                        state = State.HOME;
                        load = turnLightsOff;
                        drawHome();
                        Font font = new Font("Monaco", Font.BOLD, DEFAULTFONT);
                        StdDraw.setFont(font);
                    }
                    if (typed == 'Q' || typed == 'q') {
                        state = State.HOME;
                        drawHome();
                        Font font = new Font("Monaco", Font.BOLD, DEFAULTFONT);
                        StdDraw.setFont(font);
                    }
                    if (typed == 'p' || typed == 'P') {
                        turnLightsOff = !turnLightsOff;
                        world = new WorldGenerator(seed, turnLightsOff);

                        // ??? reassign avatar
                        avatar = new Avatar(avatar.getAvatarNewX() + 1, avatar.getAvatarNewY() + 3, world.returnWorld(), world.returnRandom(),
                                world.returnWorldStatus(), world.returnWorld()[avatar.getAvatarNewX()][avatar.getAvatarNewY()]);
                        world.returnWorld()[avatar.getAvatarNewX()][avatar.getAvatarNewY()] = Tileset.AVATAR;

                        world.getAvatarInfo(avatar);

                        world.returnWorld()[avatar.getAvatarNewX()][avatar.getAvatarNewY()] = Tileset.AVATAR;
                    }
                    if (typed == ':') {
                        colon = true;
                    } else if (typed != '\0') {
                        colon = false;
                    }
                    avatar.moveAvatar(typed);
                    if (seedFromUser) {
                        while (!StdDraw.hasNextKeyTyped() && state.equals(State.GAME)) {
                            ter.renderFrame(world.returnWorld(), getMouseX(), getMouseY(), currentTime());
//                            drawHUD(getMouseX(), getMouseY(), world.returnWorld(), currentTime());
                        }
                    }
                    break;
            }
        }

        if (!seedFromUser) {
            return world.returnWorld();
        }
        return null;
    }

    private int getMouseX() {
        int x = (int) StdDraw.mouseX();
        return Math.min(x, WIDTH - 1);
    }

    private int getMouseY() {
        int y = (int) StdDraw.mouseY();
        return Math.min(y, HEIGHT - 3);
    }

    public void drawHUD(int mouseX, int mouseY, TETile[][] world, String time) {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.line(0, HEIGHT - 2, WIDTH, HEIGHT - 2);
        StdDraw.textLeft(1, HEIGHT - 1, world[mouseX][mouseY].description());
        StdDraw.text(WIDTH / 2, HEIGHT - 1, time);
        StdDraw.show();
//        StdDraw.pause(8);
    }

    private String currentTime() {
        Calendar calendar = Calendar.getInstance();
        java.util.Date date = calendar.getTime();
        return new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(date);
    }

    public void drawHome() {
        drawFrameClear("CS61B: THE ANIME", WIDTH / 2, (long) (HEIGHT / 1.3), 30);
        drawFrame("New Game (N)", WIDTH / 2, HEIGHT / 2, 20);
        drawFrame("Load Game (L)", WIDTH / 2, (long) (HEIGHT / 2.25), 20);
        drawFrame("Quit (Q)", WIDTH / 2, (long) (HEIGHT / 2.5), 20);
    }

    public void drawFrame(String text, long x, long y, int fontSize) {
        /* Take the input string text and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, fontSize);
        StdDraw.setFont(font);
        StdDraw.text(x, y, text);
        StdDraw.show();
    }

    public void drawFrameClear(String text, long x, long y, int fontSize) {
        /* Take the input string text and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, fontSize);
        StdDraw.setFont(fontBig);
        StdDraw.text(x, y, text);
        StdDraw.show();
    }

    public int typeSeed(InputSource is) {
        String seed = "";
        drawFrameClear("Please input your seed: ", WIDTH / 2,  HEIGHT / 2, 30);
        while (is.possibleNextInput()) {
            char typed = is.getNextKey();
            if (typed == 's' || typed == 'S') {
                break;
            }
            if (Character.isDigit(typed)) {
                seed += typed;
                drawFrameClear(seed, WIDTH / 2,  HEIGHT / 2, 30);
            }
        }
        int intSeed = 0;
        char[] seedArr = seed.toCharArray();
        for (char num : seedArr) {
            int n = Character.getNumericValue(num);
            intSeed = intSeed * 10 + n;
        }
//        System.out.println(intSeed);
        return intSeed;
    }
}
