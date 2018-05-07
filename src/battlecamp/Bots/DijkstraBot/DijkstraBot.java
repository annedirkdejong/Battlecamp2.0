package battlecamp.Bots.DijkstraBot;

import battlecamp.Model.Game;
import battlecamp.Model.Player;
import battlecamp.Model.Tile;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DijkstraBot extends Player {

    private Map<Tile, Tile> tiles = new HashMap<>();
    private List<Game.Move> directions = null;

    public DijkstraBot(Player.Type type, String name){
        super(type, name);
    }

    @Override
    public void joingGame(Game game){
        this.currentGame = game;
        SimpleGraph<Tile, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        game.getBoard().getTiles().forEach(t -> {
            tiles.put(t, t);
            graph.addVertex(t);
        });
        Tile home = null;
        Player player = game.getPlayers().stream().filter(p -> p.getName().equals(this.getName())).findFirst().get();
        for(int x = 0; x < game.getBoard().getColumns(); x++) {
            for(int y = 0; y < game.getBoard().getRows(); y++) {
                Tile currentTile = tiles.get(tileEntity(x, y));
                if (currentTile.getType().equals(Tile.Type.IGLOO)) {
                    home = currentTile;
                }
                if (x < game.getBoard().getColumns() - 1 || y < game.getBoard().getRows() - 1) {
                    if (x > 0) {
                        Tile leftNeighbor = tiles.get(tileEntity(x - 1, y));
                        addEdge(graph, currentTile, leftNeighbor);
                    }
                    if (y > 0) {
                        Tile topNeighbor = tiles.get(tileEntity(x, y - 1));
                        addEdge(graph, currentTile, topNeighbor);
                    }
                    if (x < game.getBoard().getColumns() - 1) {
                        Tile rightNeighbor = tiles.get(tileEntity(x + 1, y));
                        addEdge(graph, currentTile, rightNeighbor);
                    }
                    if (y < game.getBoard().getRows() - 1) {
                        Tile bottomNeighbor = tiles.get(tileEntity(x, y + 1));
                        addEdge(graph, currentTile, bottomNeighbor);
                    }
                }
            }
        }
        Tile currentPosition = tiles.get(tileEntity(player.getX(), player.getY()));
        DijkstraShortestPath<Tile, DefaultEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Tile, DefaultEdge> path = dijkstraShortestPath.getPath(currentPosition, home);
        directions = new LinkedList<>();
        for (DefaultEdge edge : path.getEdgeList()) {
            Tile source = graph.getEdgeSource(edge);
            Tile target = graph.getEdgeTarget(edge);
            if (!currentPosition.equals(source)) {
                Tile tmp = source;
                source = target;
                target = tmp;
            }
            if (target.getX() < source.getX()) {
                directions.add(Game.Move.WEST);
            } else if (target.getX() > source.getX()) {
                directions.add(Game.Move.EAST);
            } else if (target.getY() < source.getY()) {
                directions.add(Game.Move.NORTH);
            } else {
                directions.add(Game.Move.SOUTH);
            }
            currentPosition = target;
        }
    }

    private void addEdge(SimpleGraph<Tile, DefaultEdge>  graph, Tile source, Tile target) {
        if (!source.getType().equals(Tile.Type.ROCK) && !target.getType().equals(Tile.Type.ROCK)) {
            DefaultEdge edge = graph.addEdge(source, target);
        }
    }

    private Tile tileEntity(int x, int y) {
        Tile tile = new Tile(x, y, Tile.Type.ICE);
        return tile;
    }

    @Override
    public Game.Move doMove() {
        return null;
    }

    @Override
    public Game.Move doTrainingMove() {
        return doMove();
    }
}
