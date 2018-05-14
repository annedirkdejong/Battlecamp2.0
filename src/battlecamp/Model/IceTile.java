package battlecamp.Model;

import battlecamp.Interfaces.Tile;
import battlecamp.Settings;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class IceTile implements Tile {

    private static ImageIcon imgIcon;

    private final int row;
    private final int column;

    public IceTile(int row, int col) {
        this.row = row;
        this.column = col;
    }

    @Override
    public ImageIcon getImageInstance(int width, int height) {
        if(imgIcon == null || imgIcon.getIconWidth() != width || imgIcon.getIconHeight() != height)
            try {
                imgIcon = new ImageIcon(ImageIO.read(new File(Settings.resourcePath + "ice.png")).getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } catch (IOException e) { e.printStackTrace(); }
        return imgIcon;
    }

    @Override
    public boolean movePlayer(Player player) {
        player.move(row, column);
        return true;
    }

    @Override
    public boolean isValidSpawn(Player player) {
        return true;
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
        return "IceTile{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
