package battlecamp.Interfaces;

import battlecamp.Model.Board;
import battlecamp.Model.Game;
import battlecamp.Model.Player;

import javax.swing.*;
import java.util.List;

public interface Bot {

    Game.Move makeMove(Player myPlayer, Board board, List<Player> players);
    ImageIcon getImageInstance(int width, int height);
    void win();
    void die();

}
