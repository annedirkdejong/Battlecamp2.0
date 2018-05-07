package battlecamp.Model;

import battlecamp.Interfaces.Observer;
import battlecamp.Interfaces.Subject;

import java.util.*;
import java.util.stream.Collectors;

public class Board implements Subject{

    private final int rows;
    private final int columns;
    private List<Tile> tiles;

    private List<Observer> observers;
    private Observer tileObserver;

    public Board(int rows, int columns, Observer observer){
        this.rows = rows;
        this.columns = columns;
        this.tileObserver = observer;
        this.tiles = new ArrayList<>();
        generateBoard();
        this.observers = new LinkedList<>();
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void executeMove(Player player, Game.Move move){
        int nextX = player.getX();
        int nextY = player.getY();
        switch (move){
            case NORTH:
                nextY--;
                break;
            case EAST:
                nextX++;
                break;
            case SOUTH:
                nextY++;
                break;
            case WEST:
                nextX--;
                break;
        }
        Tile nextTile = this.getTile(nextX, nextY);
        if(nextTile == null || nextTile.getType() == Tile.Type.ROCK)
            return;
        if(nextTile.getPlayer() != null){
            if(player.getType() == Player.Type.PENGUIN && nextTile.getPlayer().getType() == Player.Type.SEALION)
                player.die();
            else if(player.getType() == Player.Type.SEALION && nextTile.getPlayer().getType() == Player.Type.PENGUIN){
                nextTile.getPlayer().die();
                player.win();
            }
            return;
        }
        if(player.getType() == Player.Type.SEALION && (nextTile.getType() == Tile.Type.WATER || nextTile.getType() == Tile.Type.IGLOO))
            return;
        if(player.getType() == Player.Type.PENGUIN && nextTile.getType() == Tile.Type.IGLOO)
            player.win();
        this.getTile(player.getX(), player.getY()).removePlayer();
        this.getTile(nextX, nextY).setPlayer(player);
    }

    public void updateIce(){
        this.tiles.stream()
                .filter(tile -> tile.getType() == Tile.Type.WATER)
                .skip(new Random().nextInt(this.tiles.size())).findFirst()
                .ifPresent(tile -> tile.setType(Tile.Type.ICE));
    }

    public boolean setPlayer(Player player){
        Tile t = null;
        switch (player.getType()){
            case PENGUIN:
                List<Tile> validPenguinTiles = this.tiles.stream()
                        .filter(tile -> tile.getType() != Tile.Type.ROCK && tile.getPlayer() == null && tile.getType() != Tile.Type.IGLOO && tile.getX() < this.columns / 2) //          <-- Replace magic number
                        .collect(Collectors.toList());
                if(validPenguinTiles.isEmpty())
                    return false;
                t = validPenguinTiles.get(new Random().nextInt(validPenguinTiles.size()));
                break;
            case SEALION:
                List<Tile> validSeaLionTiles = this.tiles.stream()
                        .filter(tile -> tile.getType() == Tile.Type.ICE && tile.getPlayer() == null && tile.getX() == this.columns / 2)
                        .collect(Collectors.toList());
                if(validSeaLionTiles.isEmpty())
                    return false;
                t = validSeaLionTiles.get(new Random().nextInt(validSeaLionTiles.size()));
                break;
        }
        if(t != null){
            this.getTile(t.getX(), t.getY()).setPlayer(player);
            return true;
        }
        return false;
    }

    private void generateBoard(){
        Random rand = new Random();
        int maxIceWidth = 26;               // <-- REPLACE MAGIC NUMBER
        for(int row = 0; row < this.rows; row++){
            for(int col = 0; col < this.columns; col++){
                Tile.Type type = Tile.Type.ICE;
                if (row > (columns / 2 - maxIceWidth / 2) && row < (columns / 2 + maxIceWidth / 2)) {
                    int middle = Math.abs(columns / 2 - row);
                    if (rand.nextInt(maxIceWidth) > middle - 1)
                        type = Tile.Type.ICE;
                }
                if (rand.nextInt(10) > 7)
                    type = Tile.Type.ROCK;
                Tile tile = new Tile(row, col, type);
                tile.register(this.tileObserver);
                this.tiles.add(tile);
            }
        }
        placeHouse();
        fillCaves();
    }

    private void placeHouse(){
        List<Tile> validTiles = this.tiles.stream()
                .filter(tile -> tile.getType() != Tile.Type.ROCK && tile.getX() > this.columns - 5) //          <-- Replace magic number
                .collect(Collectors.toList());
        Tile houseTile = validTiles.get(new Random().nextInt(validTiles.size()));
        houseTile.setType(Tile.Type.IGLOO);

        Tile north = this.getTile(houseTile.getX(), houseTile.getY() + 1);
        Tile south = this.getTile(houseTile.getX(), houseTile.getY() - 1);
        Tile west = this.getTile(houseTile.getX() - 1, houseTile.getY());
        Tile east = this.getTile(houseTile.getX() + 1, houseTile.getY());
        if(north != null) north.setType(Tile.Type.ICE);
        if(south != null) south.setType(Tile.Type.ICE);
        if(west != null) west.setType(Tile.Type.ICE);
        if(east != null) east.setType(Tile.Type.ICE);
    }

    private void fillCaves(){
        // First get the IGLOO tile
        Tile igloo = this.tiles.stream().filter(tile -> tile.getType().equals(Tile.Type.IGLOO)).findFirst().orElse(null);

        // Get all tile connected to the igloo
        List<Tile> visitedTiles = new LinkedList<>();
        List<Tile> newTiles = new LinkedList<>();
        newTiles.add(igloo);
        for(;;){
            visitedTiles.addAll(newTiles);
            int totalTilesFound = visitedTiles.size();
            newTiles.clear();
            for(int i = 0; i < totalTilesFound; i++){
                Tile t = visitedTiles.get(i);
                Tile north = this.getTile(t.getX(), t.getY() + 1);
                Tile south = this.getTile(t.getX(), t.getY() - 1);
                Tile west = this.getTile(t.getX() - 1, t.getY());
                Tile east = this.getTile(t.getX() + 1, t.getY());
                if(north != null && !visitedTiles.contains(north) && !newTiles.contains(north) && (north.getType() == Tile.Type.ICE || north.getType() == Tile.Type.WATER)) newTiles.add(north);
                if(south != null && !visitedTiles.contains(south) && !newTiles.contains(south) && (south.getType() == Tile.Type.ICE || south.getType() == Tile.Type.WATER)) newTiles.add(south);
                if(west != null && !visitedTiles.contains(west) && !newTiles.contains(west) && (west.getType() == Tile.Type.ICE || west.getType() == Tile.Type.WATER)) newTiles.add(west);
                if(east != null && !visitedTiles.contains(east) && !newTiles.contains(east) && (east.getType() == Tile.Type.ICE || east.getType() == Tile.Type.WATER)) newTiles.add(east);
            }
            if(newTiles.isEmpty())
                break;
        }

        // Change all tile that are not connected to ROCK
        this.tiles.stream().filter(tile -> !visitedTiles.contains(tile)).forEach(tile -> tile.setType(Tile.Type.ROCK));
    }

    public Tile getTile(int x, int y){
        if(x >= 0 && x < this.columns && y >= 0 && y < this.rows)
            return this.tiles.get(x * this.columns + y);
        return null;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    @Override
    public void register(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void unregister(Observer o) {
        this.observers.remove(o);
    }

    @Override
    public void notifyObserver() {
        for(Observer observer : this.observers){
            if(observer != null)
                observer.update(this);
        }
    }

    public Board copy(){
        Board b = new Board(this.getRows(), this.getColumns(), null);
        b.setTiles(new ArrayList<>(this.tiles));
        return b;
    }
}
