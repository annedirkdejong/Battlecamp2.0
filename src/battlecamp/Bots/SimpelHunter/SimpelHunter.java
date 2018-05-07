package battlecamp.Bots.SimpelHunter;

import battlecamp.Model.Game;
import battlecamp.Model.Player;
import battlecamp.Model.Tile;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SimpelHunter extends Player {

    public SimpelHunter(Type type, String name) {
        super(type, name);
    }

    @Override
    public Game.Move doMove() {
        List<Game.Move> possibleMoves = getPossibleMoves();
        Player pray = findClosestTarget();
        if(possibleMoves.isEmpty()) {
            return Game.Move.NORTH;
        }

        Game.Move move = possibleMoves.get(0);
        if(pray.getY() > this.getY())
            move = Game.Move.SOUTH;
        if(!possibleMoves.contains(move)){
            if(pray.getX() > this.getX() && possibleMoves.contains(Game.Move.EAST))
                move = Game.Move.EAST;
            else
                move = Game.Move.WEST;
        }
        return move;
    }

    @Override
    public Game.Move doTrainingMove() {
        return doMove();
    }

    private Player findClosestTarget(){
        Player closest = null;
        float closedRange = Float.MAX_VALUE;
        List<Player> targets = this.currentGame.getPlayers().stream().filter(player -> !player.getName().equals(this.getName()) && !player.hasDied()).collect(Collectors.toList());
        if(targets.isEmpty())
            return null;
        for(Player target : targets){
            float diffX = this.getX() - target.getX();
            float diffY = this.getY() - target.getY();
            float range = (float) Math.sqrt(diffX*diffX + diffY*diffY);
            if(range < closedRange){
                closest = target;
                closedRange = range;
            }
        }
        return closest;
    }

    private List<Game.Move> getPossibleMoves(){
        List<Game.Move> moves = new LinkedList<>();
        Tile north = this.currentGame.getBoard().getTile(this.getX(), this.getY() + 1);
        Tile south = this.currentGame.getBoard().getTile(this.getX(), this.getY() - 1);
        Tile west = this.currentGame.getBoard().getTile(this.getX() - 1, this.getY());
        Tile east = this.currentGame.getBoard().getTile(this.getX() + 1, this.getY());
        if(north != null && north.getType().equals(Tile.Type.ICE)) moves.add(Game.Move.NORTH);
        if(south != null && south.getType().equals(Tile.Type.ICE)) moves.add(Game.Move.SOUTH);
        if(west != null && west.getType().equals(Tile.Type.ICE)) moves.add(Game.Move.WEST);
        if(east != null && east.getType().equals(Tile.Type.ICE)) moves.add(Game.Move.EAST);
        return moves;
    }

}
