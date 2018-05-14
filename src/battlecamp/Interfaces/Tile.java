package battlecamp.Interfaces;

import battlecamp.Model.Player;
import battlecamp.Settings;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public interface Tile {

    ImageIcon getImageInstance(int width, int height);
    boolean movePlayer(Player player);
    boolean isValidSpawn(Player player);
    int getRow();
    int getColumn();

}
