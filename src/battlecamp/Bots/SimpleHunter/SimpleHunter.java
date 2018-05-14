package battlecamp.Bots.SimpleHunter;

import battlecamp.Interfaces.Bot;
import battlecamp.Interfaces.Tile;
import battlecamp.Model.Board;
import battlecamp.Model.Game;
import battlecamp.Model.Player;
import battlecamp.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleHunter implements Bot {

    private static ImageIcon imgIcon;

    @Override
    public Game.Move makeMove(Player myPlayer, Board board, List<Player> players) {
        List<Game.Move> possibleMoves = getPossibleMoves(myPlayer, board);
        Player pray = findClosestTarget(myPlayer, players);
        if(possibleMoves.isEmpty()) {
            return Game.Move.UP;
        }

        Game.Move move = possibleMoves.get(0);
        if(pray.getRow() > myPlayer.getRow())
            move = Game.Move.DOWN;
        if(!possibleMoves.contains(move)){
            if(pray.getColumn() > myPlayer.getColumn() && possibleMoves.contains(Game.Move.RIGHT))
                move = Game.Move.RIGHT;
            else
                move = Game.Move.LEFT;
        }
        return move;
    }

    @Override
    public ImageIcon getImageInstance(int width, int height) {
        if(imgIcon == null || imgIcon.getIconWidth() != width || imgIcon.getIconHeight() != height)
            try {
                imgIcon = new ImageIcon(ImageIO.read(new File(Settings.resourcePath + "sealion.png")).getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } catch (IOException e) { e.printStackTrace(); }
        return imgIcon;
    }

    @Override
    public void win() {

    }

    @Override
    public void die() {

    }

    private Player findClosestTarget(Player myPlayer, List<Player> players){
        Player closest = null;
        float closedRange = Float.MAX_VALUE;
        List<Player> targets = players.stream().filter(player -> !player.equals(myPlayer) && player.getStatus().equals(Player.PlayerStatus.ALIVE)).collect(Collectors.toList());
        if(targets.isEmpty())
            return null;
        for(Player target : targets){
            float diffRow = myPlayer.getRow() - target.getRow();
            float diffCol = myPlayer.getColumn() - target.getColumn();
            float range = (float) Math.sqrt(diffRow*diffRow + diffCol*diffCol);
            if(range < closedRange){
                closest = target;
                closedRange = range;
            }
        }
        return closest;
    }

    private List<Game.Move> getPossibleMoves(Player myPlayer, Board board){
        List<Game.Move> moves = new LinkedList<>();
        Tile north = board.getTile(myPlayer.getRow() - 1, myPlayer.getColumn());
        Tile south = board.getTile(myPlayer.getRow() + 1, myPlayer.getColumn());
        Tile west = board.getTile(myPlayer.getRow(), myPlayer.getColumn() - 1);
        Tile east = board.getTile(myPlayer.getRow(), myPlayer.getColumn() + 1);
        if(north != null && north.isValidSpawn(myPlayer)) moves.add(Game.Move.UP);
        if(south != null && south.isValidSpawn(myPlayer)) moves.add(Game.Move.DOWN);
        if(west != null && west.isValidSpawn(myPlayer)) moves.add(Game.Move.LEFT);
        if(east != null && east.isValidSpawn(myPlayer)) moves.add(Game.Move.RIGHT);
        return moves;
    }
}
