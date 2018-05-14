package battlecamp.Model;

import battlecamp.Interfaces.Tile;
import battlecamp.Settings;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class WaterTile implements Tile {

    private static ImageIcon imgIcon;

    private final int row;
    private final int column;

    public WaterTile(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public ImageIcon getImageInstance(int width, int height) {
        if(imgIcon == null || imgIcon.getIconWidth() != width || imgIcon.getIconHeight() != height)
            try {
                imgIcon = new ImageIcon(ImageIO.read(new File(Settings.resourcePath + "rock.png")).getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } catch (IOException e) { e.printStackTrace(); }
        return imgIcon;
    }

    @Override
    public boolean movePlayer(Player player) {
        switch (player.getType()){
            case PENGUIN:
                player.move(this.row, this.column);
                return true;
            case SEA_LION:
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean isValidSpawn(Player player) {
        switch (player.getType()){
            case PENGUIN:
                return true;
            case SEA_LION:
                return false;
            default:
                return false;
        }
    }

    @Override
    public int getRow() {
        return this.row;
    }

    @Override
    public int getColumn() {
        return this.column;
    }

    @Override
    public String toString() {
        return "WaterTile{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
