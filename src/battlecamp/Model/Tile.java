package battlecamp.Model;

import battlecamp.Interfaces.Observer;
import battlecamp.Interfaces.Subject;

import java.util.LinkedList;
import java.util.List;



public class Tile implements Subject{

    private List<Observer> observers;

    public enum Type {WATER, ICE, ROCK, IGLOO}

    private final int X;
    private final int Y;
    private Type type;
    private Player player;

    public Tile(int x, int y, Type type){
        this.X = x;
        this.Y = y;
        this.type = type;
        this.observers = new LinkedList<>();
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.player.move(this.X, this.Y);
        this.player.setTile(this);
        this.notifyObserver();
    }

    public void removePlayer(){
        this.player.setTile(null);
        this.player = null;
        this.notifyObserver();
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
        for(Observer o : this.observers)
            o.update(this);
    }
}
