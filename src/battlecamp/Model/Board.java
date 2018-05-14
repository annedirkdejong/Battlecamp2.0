package battlecamp.Model;

import battlecamp.Interfaces.Tile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Board {

    private final int rows;
    private final int columns;

    private List<Tile> tiles;

    public Board(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        this.tiles = new ArrayList<>();
        generateBoard();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    private void generateBoard(){
        Random rand = new Random();
        int maxIceWidth = 26;               // <-- REPLACE MAGIC NUMBER
        for(int row = 0; row < this.rows; row++){
            for(int col = 0; col < this.columns; col++){
                if (rand.nextInt(10) > 7)
                    this.tiles.add(new RockTile(row, col));
                else if(row > (columns / 2 - maxIceWidth / 2) && row < (columns / 2 + maxIceWidth / 2) && rand.nextInt(maxIceWidth) > (Math.abs(columns / 2 - row)) - 1)
                    this.tiles.add(new IceTile(row, col)); // <-- This should be WaterTile
                else
                    this.tiles.add(new IceTile(row, col));
            }
        }
        placeHouse();
        fillCaves();
    }

    private void placeHouse(){
        List<Tile> validTiles = this.tiles.stream()
                .filter(tile -> tile.getClass() != RockTile.class && tile.getColumn() > this.columns - 5) //          <-- Replace magic number
                .collect(Collectors.toList());
        Tile houseTile = validTiles.get(new Random().nextInt(validTiles.size()));
        this.tiles.set(this.tiles.indexOf(houseTile), new IglooTile(houseTile.getRow(), houseTile.getColumn()));

        Tile north = this.getTile(houseTile.getRow(), houseTile.getColumn() + 1);
        Tile south = this.getTile(houseTile.getRow(), houseTile.getColumn() - 1);
        Tile west = this.getTile(houseTile.getRow() - 1, houseTile.getColumn());
        Tile east = this.getTile(houseTile.getRow() + 1, houseTile.getColumn());
        if(north != null) this.tiles.set(this.tiles.indexOf(north), new IceTile(north.getRow(), north.getColumn()));
        if(south != null) this.tiles.set(this.tiles.indexOf(south), new IceTile(south.getRow(), south.getColumn()));
        if(west != null) this.tiles.set(this.tiles.indexOf(west), new IceTile(west.getRow(), west.getColumn()));
        if(east != null) this.tiles.set(this.tiles.indexOf(east), new IceTile(east.getRow(), east.getColumn()));
    }

    private void fillCaves(){
        // First get the IGLOO tile
        Tile igloo = this.tiles.stream().filter(tile -> tile.getClass().equals(IglooTile.class)).findFirst().orElse(null);

        // Get all tile connected to the igloo
        List<Tile> visitedTiles = new LinkedList<>();
        List<Tile> newTiles = new LinkedList<>();
        newTiles.add(igloo);
        for(;;){
            visitedTiles.addAll(newTiles);
            if(newTiles.isEmpty()) break;
            int totalTilesFound = visitedTiles.size();
            newTiles.clear();
            for(int i = 0; i < totalTilesFound; i++){
                Tile t = visitedTiles.get(i);
                Tile north = this.getTile(t.getRow() + 1, t.getColumn());
                Tile south = this.getTile(t.getRow() - 1, t.getColumn());
                Tile west = this.getTile(t.getRow(), t.getColumn() - 1);
                Tile east = this.getTile(t.getRow(), t.getColumn() + 1);
                if(north != null && !visitedTiles.contains(north) && !newTiles.contains(north) && (north.getClass().equals(IceTile.class) || north.getClass().equals(WaterTile.class))) newTiles.add(north);
                if(south != null && !visitedTiles.contains(south) && !newTiles.contains(south) && (south.getClass().equals(IceTile.class) || south.getClass().equals(WaterTile.class))) newTiles.add(south);
                if(west != null && !visitedTiles.contains(west) && !newTiles.contains(west) && (west.getClass().equals(IceTile.class) || west.getClass().equals(WaterTile.class))) newTiles.add(west);
                if(east != null && !visitedTiles.contains(east) && !newTiles.contains(east) && (east.getClass().equals(IceTile.class) || east.getClass().equals(WaterTile.class))) newTiles.add(east);
            }
        }
        // Change all tile that are not connected to ROCK
        this.tiles.stream().filter(tile -> !visitedTiles.contains(tile)).forEach(tile -> this.tiles.set(this.tiles.indexOf(tile), new RockTile(tile.getRow(), tile.getColumn())));
    }

    public List<Tile> getValidSpawningTiles(Player player){
        return this.tiles
                .stream()
                .filter(tile ->
                        ((player.getType().equals(Player.PlayerType.PENGUIN) && tile.getColumn() < columns / 2) ||
                                (player.getType().equals(Player.PlayerType.SEA_LION) && tile.getColumn() > columns / 2)) &&
                                tile.isValidSpawn(player))
                .collect(Collectors.toList());
    }

    public Tile getTile(int row, int column) throws IndexOutOfBoundsException{
        if(row >= 0 && row < this.rows && column >= 0 && column < this.columns)
            return this.tiles.get(row * this.columns + column);
        return null;
    }

}
