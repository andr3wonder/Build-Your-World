package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.InputDemo.*;

import java.util.Random;

public class Avatar {
    private int x;
    private int y;
    private int w;
    private int h;
    private boolean[][] status;
    private TETile[][] world;
    private TETile[][] worldReplica;
    private Random rand;
    private Position avatarOriginalPos;
    private TETile tile;

    private class Position {
        private int x;
        private int y;
        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getXPos() {
            return x;
        }
        public int getYPos() {
            return y;
        }
    }

    public Avatar(int w, int h, TETile[][] world, Random rand, boolean[][] status) {
        this.x = w - 1;
        this.y = h - 3;
        this.w = w;
        this.h = h - 2;
        this.world = world;
        this.worldReplica = world;
        this.rand = rand;
        this.status = status;
    }

    public Avatar(int w, int h, TETile[][] world, Random rand, boolean[][] status, TETile oldTile) {
        this.x = w - 1;
        this.y = h - 3;
        this.w = w;
        this.h = h - 2;
        this.world = world;
        this.worldReplica = world;
        this.rand = rand;
        this.status = status;
        this.tile = oldTile;
    }

    public int getAvatarOriginalXPos() {
        return avatarOriginalPos.getXPos();
    }

    public int getAvatarOriginalYPos() {
        return avatarOriginalPos.getYPos();
    }

    public int getAvatarNewX() {
        return x;
    }

    public int getAvatarNewY() {
        return y;
    }

    public void addAvatarRandom() {
        while (true) {
            if (validateAvatarPos(this.x, this.y)) {
                this.tile = worldReplica[this.x][this.y];
                world[this.x][this.y] = Tileset.AVATAR;
                avatarOriginalPos = new Position(this.x, this.y);
                break;
            } else {
                this.x = rand.nextInt(w);
                this.y = rand.nextInt(h);
            }
        }
    }

    private boolean validateAvatarPos(int x, int y) {
        return status[x][y];
    }

    public void moveAvatar(char key) {
        char t = key;
        if (t == 'w' || t == 'W') {
            if (validateAvatarPos(this.x, this.y + 1)) {
                world[this.x][this.y] = tile;
                tile = world[this.x][this.y + 1];
                world[this.x][this.y + 1] = Tileset.AVATAR;
                this.y = this.y + 1;
            }
        } else if (t == 'a' || t == 'A') {
            if (validateAvatarPos(this.x - 1, this.y)) {
                world[this.x][this.y] = tile;
                tile = world[this.x - 1][this.y];
                world[this.x - 1][this.y] = Tileset.AVATAR;
                this.x = this.x - 1;
            }
        } else if (t == 's' || t == 'S') {
            if (validateAvatarPos(this.x, this.y - 1)) {
                world[this.x][this.y] = tile;
                tile = world[this.x][this.y - 1];
                world[this.x][this.y - 1] = Tileset.AVATAR;
                this.y = this.y - 1;
            }
        } else if (t == 'd' || t == 'D') {
            if (validateAvatarPos(this.x + 1, this.y)) {
                world[this.x][this.y] = tile;
                tile = world[this.x + 1][this.y];
                world[this.x + 1][this.y] = Tileset.AVATAR;
                this.x = this.x + 1;
            }
        }
    }
}
