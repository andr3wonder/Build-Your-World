package byow.Core;

public class WorldMap {
    private boolean[][] worldStatus;
    public WorldMap() {
        worldStatus = new boolean[Engine.WIDTH][Engine.HEIGHT];
    }

    public boolean[][] status() {
        return worldStatus;
    }
}
