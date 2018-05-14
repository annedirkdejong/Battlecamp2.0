package battlecamp.Bots.DijkstraBot;

import battlecamp.Interfaces.Bot;
import battlecamp.Interfaces.Tile;
import battlecamp.Model.*;
import battlecamp.Settings;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DijkstraBot implements Bot {

    private static ImageIcon imgIcon;

    private SimpleGraph<Tile, DefaultEdge> graph;

    @Override
    public Game.Move makeMove(Player myPlayer, Board board, List<Player> players) {
        if(this.graph == null)
            initDijkstra(board);
        Player enemy = locateClosestEnemy(myPlayer, players);
        Tile myTile = board.getTile(myPlayer.getRow(), myPlayer.getColumn());
        Tile targetTile = board.getTile(enemy.getRow(), enemy.getColumn());

        DijkstraShortestPath<Tile, DefaultEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Tile, DefaultEdge> path = dijkstraShortestPath.getPath(myTile, targetTile);
        if(path == null)
            System.out.println("MyTile: " + myTile + ", Target: " + targetTile);
        for (DefaultEdge edge : path.getEdgeList()) {
            Tile source = graph.getEdgeSource(edge);
            Tile target = graph.getEdgeTarget(edge);
            if (!myTile.equals(source)) {
                Tile tmp = source;
                source = target;
                target = tmp;
            }
            if (target.getColumn() < source.getColumn()) {
                return Game.Move.LEFT;
            } else if (target.getColumn() > source.getColumn()) {
                return Game.Move.RIGHT;
            } else if (target.getRow() < source.getRow()) {
                return Game.Move.UP;
            } else {
                return Game.Move.DOWN;
            }
        }
        return Game.Move.UP;
    }

    private Player locateClosestEnemy(Player myPlayer, List<Player> players){
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

    private void initDijkstra(Board board){
        this.graph = new SimpleGraph<>(DefaultEdge.class);
        for(Tile tile : board.getTiles())
            this.graph.addVertex(tile);
        for(int row = 0; row < board.getRows(); row++){
            for(int col = 0; col < board.getColumns(); col++){
                if (col < board.getColumns() - 1 || row < board.getRows() - 1) {
                    Tile currentTile = board.getTile(row, col);
                    if (col > 0) {
                        Tile leftNeighbor = board.getTile(row, col - 1);
                        addEdge(graph, currentTile, leftNeighbor);
                    }
                    if (row > 0) {
                        Tile topNeighbor = board.getTile(row - 1, col);
                        addEdge(graph, currentTile, topNeighbor);
                    }
                    if (col < board.getColumns() - 1) {
                        Tile rightNeighbor = board.getTile(row, col + 1);
                        addEdge(graph, currentTile, rightNeighbor);
                    }
                    if (row < board.getRows() - 1) {
                        Tile bottomNeighbor = board.getTile(row + 1, col);
                        addEdge(graph, currentTile, bottomNeighbor);
                    }
                }
            }
        }
    }

    private void addEdge(SimpleGraph<Tile, DefaultEdge>  graph, Tile source, Tile target) {
        if (!source.getClass().equals(RockTile.class) && !target.getClass().equals(RockTile.class) &&
                !source.getClass().equals(WaterTile.class) && !target.getClass().equals(WaterTile.class)) {
            DefaultEdge edge = graph.addEdge(source, target);
        }
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
}
