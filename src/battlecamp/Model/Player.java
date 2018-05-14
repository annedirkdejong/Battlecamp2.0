package battlecamp.Model;

import battlecamp.Interfaces.Bot;
import battlecamp.Interfaces.PlayerObserver;
import battlecamp.Interfaces.PlayerSubject;

import javax.swing.*;
import java.util.List;

public class Player implements PlayerSubject {

    public enum PlayerType {PENGUIN, SEA_LION}
    public enum PlayerStatus {ALIVE, DEAD, WON}

    private final PlayerType type;
    private PlayerStatus status;
    private final String name;
    private int row;
    private int column;

    private final Bot controller;
    private PlayerObserver observer;

    public Player(PlayerType type, Bot controller, String name) {
        this.type = type;
        this.status = PlayerStatus.ALIVE;
        this.controller = controller;
        this.name = name;
    }

    public void reset(){
        this.status = PlayerStatus.ALIVE;
    }

    public void win(){
        this.status = PlayerStatus.WON;
        this.controller.win();
    }

    public void die(){
        this.status = PlayerStatus.DEAD;
        this.controller.die();
    }

    public void move(int row, int column){
        if(Game.status != null && (Game.status.equals(Game.GameStatus.RUNNING)))
            this.notifyObserver(this.row, this.column, row, column);
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public PlayerStatus getStatus(){
        return this.status;
    }

    public PlayerType getType() {
        return type;
    }

    public String getName(){
        return this.name;
    }

    public Game.Move makeMove(Board board, List<Player> players){
        return this.controller.makeMove(this, board, players);
    }

    public ImageIcon getImageInstance(int width, int height){
        return this.controller.getImageInstance(width, height);
    }

    @Override
    public void register(PlayerObserver o) {
        this.observer = o;
    }

    @Override
    public void unregister(PlayerObserver o) {
        this.observer = null;
    }

    @Override
    public void notifyObserver(int oldRow, int oldColumn, int newRow, int newColumn) {
        this.observer.update(this, oldRow, oldColumn, newRow, newColumn);
    }

    @Override
    public String toString() {
        return "Player{" +
                "type=" + type +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}
