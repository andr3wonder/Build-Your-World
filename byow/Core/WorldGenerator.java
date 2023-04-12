package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static byow.TileEngine.Tileset.WALL;

public class WorldGenerator {
    // temporary for main test
    private static final int WIDTH = Engine.WIDTH;
    private static final int HEIGHT = Engine.HEIGHT - 2;
    private static final int DEFAULTFONT = 14;
    private Random RANDOM;
    private static final int BOUND = 9;

    private WorldMap wrld = new WorldMap();
    private boolean[][] status = wrld.status();
    private TETile[][] world;
    private LinkedHashMap<Room, Boolean> allRoomPos = new LinkedHashMap<>();
    private Avatar avatar;
    private int seed;
    private boolean[][] lightStatus;

    // construct a new world
    public WorldGenerator(int seed) {
        RANDOM = new Random(seed);
        this.seed = seed;
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.OUT;
            }
        }
        int repeat = RANDOM.nextInt(WIDTH - HEIGHT, WIDTH + HEIGHT);
        for (int i = 0; i < repeat; i++) {
            this.drawRoom();
        }
        this.connectAllRooms();
        this.addWalls();
        this.drawLights(allRoomPos);
    }

    public WorldGenerator(int seed, boolean turnLightsOff) {
        RANDOM = new Random(seed);
        this.seed = seed;
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.OUT;
            }
        }
        int repeat = RANDOM.nextInt(WIDTH - HEIGHT, WIDTH + HEIGHT);
        for (int i = 0; i < repeat; i++) {
            this.drawRoom();
        }
        this.connectAllRooms();
        this.addWalls();

        if (!turnLightsOff) {
            this.drawLights(allRoomPos);
        }
    }

    /**
     * Uses data saved in text file to load previous game
     * @Source https://www.w3schools.com/java/java_files_read.asp
     */
    public WorldGenerator(boolean turnLightsOff) {
        try {
            File savedFile = new File("saved_game.txt");
            Scanner reader = new Scanner(savedFile);
            this.seed = Integer.parseInt(reader.nextLine());
            int avatarNewXPos = Integer.parseInt(reader.nextLine());
            int avatarNewYPos = Integer.parseInt(reader.nextLine());
            reader.close();
            //generate world with seed
            Font font = new Font("Monaco", Font.BOLD, DEFAULTFONT);
            StdDraw.setFont(font);
            WorldGenerator w = new WorldGenerator(this.seed, turnLightsOff);
            // add avatar to saved position
            world = w.returnWorld();
            avatar = new Avatar(avatarNewXPos + 1, avatarNewYPos + 3, w.returnWorld(), w.returnRandom(),
                    w.returnWorldStatus(), world[avatarNewXPos][avatarNewYPos]);
            world[avatarNewXPos][avatarNewYPos] = Tileset.AVATAR;
        } catch (FileNotFoundException e) {
            System.out.println("No saved file found!");
            System.exit(0);
        }
    }


    public TETile[][] returnWorld() {
        return world;
    }
    public LinkedHashMap<Room, Boolean> returnAllRoomPos() {
        return allRoomPos;
    }

    public int returnSeed() {
        return seed;
    }

    public Random returnRandom() {
        return RANDOM;
    }

    public boolean[][] returnWorldStatus() {
        return status;
    }

    public void getAvatarInfo(Avatar avatar) {
        this.avatar = avatar;
    }

    public Avatar returnAvatar() {
        return avatar;
    }

    // nested Position class to save drawn room position
    private class Room {
        private int x;
        private int y;
        private int w;
        private int h;
        private Room(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public int getH() {
            return h;
        }
        public int getW() {
            return w;
        }
    }

    // draw room on screen
    // add roomPos to DS(tbd) when a room is successfully drawn
    public void drawRoom() {
        // randomize room position
        int w = RANDOM.nextInt(3, BOUND);
        int h = RANDOM.nextInt(3, BOUND);
        int posX = RANDOM.nextInt(1, WIDTH - 1);
        int posY = RANDOM.nextInt(1, HEIGHT - 1);
        Room roomPos = new Room(posX, posY, w, h);

        // draw the room if possible
        if (validatePos(w, h, posX, posY) && !isOverlap(w, h, posX, posY)) {
            for (int x = posX; x < posX + w; x++) {
                for (int y = posY; y < posY + h; y++) {
                    world[x][y] = Tileset.FLOOR;
                    status[x][y] = true;
                }
            }
            // add roomPos
            allRoomPos.put(roomPos, false);
            world[posX][posY] = Tileset.LIGHT;
        }
    }

    public void drawLights(LinkedHashMap<Room, Boolean> allRoomPos) {
        for (Room room: allRoomPos.keySet()) {
            drawLight(room);
        }
    }
    public void drawLight(Room room) {
        int posX = room.getX();
        int posY = room.getY();
        int w = room.getW();
        int h = room.getH();

//        int lightXPos = RANDOM.nextInt(posX, posX + w - 1);
//        int lightYPos = RANDOM.nextInt(posY, posY + h - 1);
        int lightXPos = posX;
        int lightYPos = posY;
        world[lightXPos][lightYPos] = Tileset.LIGHT;
//        boolean[] wall = new boolean[4];
        lightStatus = new boolean[WIDTH][HEIGHT];
        ArrayList<TETile> lights = new ArrayList<>(6);
        lights.addAll(Arrays.asList(Tileset.LIGHT, Tileset.LIGHT1, Tileset.LIGHT2, Tileset.LIGHT3, Tileset.LIGHT4, Tileset.LIGHT5));
        for (int i = 1; i < Math.min(6, Math.max(w, h)); i++) {
            int lightXAdd = lightXPos + i;
            int lightYAdd = lightYPos + i;
            int lightXDec = lightXPos - i;
            int lightYDec = lightYPos - i;

            // Andrew's version
            for (int x = lightXDec; x <= lightXAdd; x++) {
                if (lightYAdd < HEIGHT && x < WIDTH && x >= 0 && status[x][lightYAdd]) {
//                    if (world[x][lightYAdd] == WALL) wall[0] = true;
                    if (!isHall(x, lightYAdd) && !lightStatus[x][lightYAdd] && isOwnRoom(x, lightYAdd, posX, posY, w, h)) {
                        world[x][lightYAdd] = lights.get(i);
                        lightStatus[x][lightYAdd] = true;
                    }
                }
                if (lightYDec >= 0 && x < WIDTH && x >= 0 && status[x][lightYDec]) {
//                    if (world[x][lightYDec] == WALL) wall[1] = true;
                    if (!isHall(x, lightYDec) && !lightStatus[x][lightYDec] && isOwnRoom(x, lightYDec, posX, posY, w, h)) {
                        world[x][lightYDec] = lights.get(i);
                        lightStatus[x][lightYDec] = true;
                    }
                }
            }
            for (int y = lightYDec; y <= lightYAdd; y++) {
                if (lightXAdd < WIDTH && y < HEIGHT && y >= 0 && status[lightXAdd][y]) {
//                    if (world[lightXAdd][y] == WALL) wall[2] = true;
                    if (!isHall(lightXAdd, y) && !lightStatus[lightXAdd][y] && isOwnRoom(lightXAdd, y, posX, posY, w, h)) {
                        world[lightXAdd][y] = lights.get(i);
                        lightStatus[lightXAdd][y] = true;
                    }
                }
                if (lightXDec >= 0 && y < HEIGHT && y >= 0 && status[lightXDec][y]) {
//                    if (world[lightXDec][y] == WALL) wall[3] = true;
                    if (!isHall(lightXDec, y) && !lightStatus[lightXDec][y] && isOwnRoom(lightXDec, y, posX, posY, w, h)) {
                        world[lightXDec][y] = lights.get(i);
                        lightStatus[lightXDec][y] = true;
                    }
                }
            }
        }
    }

    // helper function for checking hallways
    private boolean isHall(int x, int y) {
        if (!status[x - 1][y] && !status[x + 1][y] || !status[x][y - 1] && !status[x][y + 1]) {
            return true;
        }
        return false;
    }

    // helper function for checking own room
    private boolean isOwnRoom(int x, int y, int xPos, int yPos, int w, int h) {
        if (x < xPos || x > xPos + w || y < yPos || y > yPos + h) {
            return false;
        }
        return true;
    }

    // check if room can be drawn
    public boolean validatePos(int w, int h, int posX, int posY) {
        if (posX + w < WIDTH && posY + h < HEIGHT) {
            return true;
        }
        return false;
    }

    // check if rooms overlap
    public boolean isOverlap(int w, int h, int posX, int posY) {
        for (int x = posX - 1; x < posX + w + 1; x++) {
            for (int y = posY - 1; y < posY + h + 1; y++) {
                if (status[x][y]) {
                    return true;
                }
            }
        }
        return false;
    }

    // iterate through all existing rooms that aren't connected
    public void connectAllRooms() {
        while (allRoomPos.values().contains(false)) {
            if (!allRoomPos.values().contains(true)) {
                ArrayList<Room> roomsAsArray = new ArrayList<>(allRoomPos.keySet());
                Room randomRoom = roomsAsArray.get(RANDOM.nextInt(roomsAsArray.size()));
                allRoomPos.put(randomRoom, true);
            }
            ArrayList<Room> closestRoute = findClosestRoute();
            if (!closestRoute.isEmpty()) {
                Room closestToRoom = closestRoute.get(0);
                Room closestRoom = closestRoute.get(1);
                allRoomPos.put(closestRoom, true);
                connectRoom(closestToRoom, closestRoom);
            } else {
                break;
            }
        }
    }

    public ArrayList<Room> findClosestRoute() {
        ArrayList result = new ArrayList<>();
        int dist = WIDTH + HEIGHT;
        for (Room room: allRoomPos.keySet()) {
            if (allRoomPos.get(room)) {
                int closestDist = findMyClosestRoute(room);
                Room closestRoom = findMyClosestRoom(room);
                if (closestDist < dist) {
                    dist = closestDist;
                    result.clear();
                    result.add(room);
                    result.add(closestRoom);
                }
            }
        }
        return result;
    }

    public int findMyClosestRoute(Room room) {
        int smallestDist = WIDTH + HEIGHT;
        for (Room other: allRoomPos.keySet()) {
            if (!allRoomPos.get(other)) {
                int roomMidX = room.getX() + room.getW() / 2;
                int roomMidY = room.getY() + room.getH() / 2;
                int otherMidX = other.getX() + other.getW() / 2;
                int otherMidY = other.getY() + other.getH() / 2;
                int currentDist = Math.abs(roomMidX - otherMidX) + Math.abs(roomMidY - otherMidY);
                if (currentDist < smallestDist && currentDist != 0) {
                    smallestDist = currentDist;
                }
            }
        }
        return smallestDist;
    }

    public Room findMyClosestRoom(Room room) {
        Room smallestRoom = null;
        int smallestDist = WIDTH + HEIGHT;
        for (Room other: allRoomPos.keySet()) {
            if (!allRoomPos.get(other)) {
                int currentDist = Math.abs(room.getX() - other.getX()) + Math.abs(room.getY() - other.getY());
                if (currentDist < smallestDist && currentDist != 0) {
                    smallestDist = currentDist;
                    smallestRoom = other;
                }
            }
        }
        return smallestRoom;
    }

    // connect room to the closest room with room and closest room as inputs
    public void connectRoom(Room input, Room closest) {
        // connect horizontally if possible
        for (int k = input.getY(); k < input.getY() + input.getH(); k++) {
            for (int l = closest.getY(); l < closest.getY() + closest.getH(); l++) {
                if (k == l) {
                    connectHorizontal(input, closest, k, input.getX(), closest.getX());
                    return;
                }
            }
        }
        // connect vertically if possible
        for (int i = input.getX(); i < input.getX() + input.getW(); i++) {
            for (int j = closest.getX(); j < closest.getX() + closest.getW(); j++) {
                if (i == j) {
                    connectVertical(input, closest, i, input.getY(), closest.getY());
                    return;
                }
            }
        }
        connectBent(input, closest);
    }

    // connect horizontally on inputX and closestX
    public void connectHorizontal(Room input, Room closest, int inputY, int inputX, int closestX) {
        int start = 0;
        int end = 0;
        if (inputX > closestX) {
            start = closestX + closest.getW();
            end = inputX;
        } else {
            start = inputX + input.getW();
            end = closestX;
        }
        for (int x = start; x < end; x++) {
            world[x][inputY] = Tileset.HALL;
            status[x][inputY] = true;
        }
    }

    // connect vertically on inputY and closestY
    public void connectVertical(Room input, Room closest, int inputX, int inputY, int closestY) {
        int start = 0;
        int end = 0;
        if (inputY > closestY) {
            start = closestY + closest.getH();
            end = inputY;
        } else {
            start = inputY + input.getH();
            end = closestY;
        }
        for (int y = start; y < end; y++) {
            world[inputX][y] = Tileset.HALL;
            status[inputX][y] = true;
        }
    }

    // connect bent A
    public void connectBent(Room input, Room closest) {
        if (input.getX() < closest.getX()) {
            // input room is left of the closest room
            // a
            if (input.getY() < closest.getY()) {
                int startY = input.getY() + RANDOM.nextInt(input.getH());
                int startX = input.getX() + input.getW();
                int endX = closest.getX() + RANDOM.nextInt(closest.getW());
                int endY = closest.getY();
                for (int x = startX; x <= endX; x++) {
                    world[x][startY] = Tileset.HALL;
                    status[x][startY] = true;
                }
                for (int y = startY + 1; y < endY; y++) {
                    world[endX][y] = Tileset.HALL;
                    status[endX][y] = true;
                }
            } else { // b
                int startY = input.getY() + RANDOM.nextInt(input.getH());
                int startX = input.getX() + input.getW();
                int endX = closest.getX() + RANDOM.nextInt(closest.getW());
                int endY = closest.getY() + closest.getH();
                for (int x = startX; x <= endX; x++) {
                    world[x][startY] = Tileset.HALL;
                    status[x][startY] = true;
                }
                for (int y = startY; y >= endY; y--) {
                    world[endX][y] = Tileset.HALL;
                    status[endX][y] = true;
                }
            }
        } else { // input room is right of the closest room
            // c
            if (input.getY() < closest.getY()) {
                int startY = input.getY() + RANDOM.nextInt(input.getH());
                int startX = input.getX();
                int endX = closest.getX() + RANDOM.nextInt(closest.getW());
                int endY = closest.getY();
                for (int x = startX - 1; x > endX; x--) {
                    world[x][startY] = Tileset.HALL;
                    status[x][startY] = true;
                }
                for (int y = startY; y < endY; y++) {
                    world[endX][y] = Tileset.HALL;
                    status[endX][y] = true;
                }
            } else { // d
                int startY = input.getY() + RANDOM.nextInt(input.getH());
                int startX = input.getX();
                int endX = closest.getX() + RANDOM.nextInt(closest.getW());
                int endY = closest.getY() + closest.getH();
                for (int x = startX - 1; x > endX; x--) {
                    world[x][startY] = Tileset.HALL;
                    status[x][startY] = true;
                }
                for (int y = startY; y >= endY; y--) {
                    world[endX][y] = Tileset.HALL;
                    status[endX][y] = true;
                }
            }
        }
    }


    public void addWalls() {
        for (int w = 0; w < status.length; w++) {
            for (int h = 0; h < status[w].length; h++) {
                if (status[w][h]) {
                    drawWalls(w, h);
                }
            }
        }
    }

    public void drawWalls(int w, int h) {
        if (!status[w - 1][h]) {
            world[w - 1][h] = WALL;
        }
        if (!status[w + 1][h]) {
            world[w + 1][h] = WALL;
        }
        if (!status[w][h - 1]) {
            world[w][h - 1] = WALL;
        }
        if (!status[w][h + 1]) {
            world[w][h + 1] = WALL;
        }
        if (!status[w - 1][h + 1]) {
            world[w - 1][h + 1] = WALL;
        }
        if (!status[w - 1][h - 1]) {
            world[w - 1][h - 1] = WALL;
        }
        if (!status[w + 1][h + 1]) {
            world[w + 1][h + 1] = WALL;
        }
        if (!status[w + 1][h - 1]) {
            world[w + 1][h - 1] = WALL;
        }
    }

    /**
     * Saves game to text file at specified location
     * @Source: https://stackoverflow.com/questions/2885173/
     * how-do-i-create-a-file-and-write-to-it-in-java
     */
    public void saveGame() {
        File file = new File("./saved_game.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter("saved_game.txt", StandardCharsets.UTF_8);
            // save seed
            writer.println(seed);
            // save avatar original position
//            writer.println(avatar.getAvatarOriginalXPos());
//            writer.println(avatar.getAvatarOriginalYPos());
            // save avatar new position
            writer.println(avatar.getAvatarNewX());
            writer.println(avatar.getAvatarNewY());
            writer.close();
        } catch (IOException exception) {
            System.out.println(exception);
            System.exit(0);
        }
    }
}
