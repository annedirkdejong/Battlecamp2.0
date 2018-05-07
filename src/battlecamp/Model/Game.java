package battlecamp.Model;

import battlecamp.Bots.QBot.State;
import battlecamp.Settings;

import java.util.List;

public class Game {

    public enum Status {INITIALIZING, TRAINING, RUNNING, STOPPED}
    public enum Move {NORTH, EAST, SOUTH, WEST}

    private int id;
    private Board board;
    private List<Player> players;

    public static Status status;
    private int playersTurn;

    public Game(int id, Board board, List<Player> players){
        this.status = Status.INITIALIZING;
        this.id = id;
        this.board = board;
        this.players = players;
        this.playersTurn = 0;
        this.board.notifyObserver();
        System.out.println("Board created... Placing players");
        if(addPlayers()) {
            System.out.println("Starting game");
            start();
        }
    }

    public void start(){

        long start = System.currentTimeMillis();
        //train();
        this.status = Status.RUNNING;
        System.out.println("Training of " + Settings.TRAINING_EPOCHS + " epochs took " + (System.currentTimeMillis() - start) + "ms");

        play();

    }

    private void train(){
        int episode = 0;
        this.status = Status.TRAINING;
        int moves = 0;
        while(this.status == Status.TRAINING){
            System.out.println("Training episode " + episode);
            Player currentPlayer = this.players.get(this.playersTurn);
            Move move = currentPlayer.doTrainingMove();
            this.board.executeMove(currentPlayer, move);

            if(gameFinished(currentPlayer)) {
                if(episode == Settings.TRAINING_EPOCHS)
                    this.status = Status.RUNNING;
                for(Player p : this.players)
                    p.reset();
                addPlayers();
                episode++;
                moves = 0;
            }
            moves++;
            nextTurn();
        }
    }

    private void play(){
        while(this.status == Status.RUNNING){
            Player currentPlayer = this.players.get(this.playersTurn);
            Move move = currentPlayer.doMove();
            this.board.executeMove(currentPlayer, move);

            if(gameFinished(currentPlayer)) {
                this.status = Status.STOPPED;
                for(Player p : this.players)
                    p.reset();
            }

            nextTurn();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean gameFinished(Player player){
        if(player.hasWon()) {
            return true;
        } else if(player.hasDied()){
            return true;
        }
        return false;
    }

    private boolean addPlayers(){
        for(Player player : this.players) {
            if (!this.board.setPlayer(player)) {
                System.out.println("Error: Could not find a suitable spot for player " + player);
                return false;
            }
            player.joingGame(this);
        }
        return true;
    }

    private void nextTurn(){
        this.playersTurn = this.playersTurn < this.players.size() - 1 ? this.playersTurn + 1 : 0;
        if(Settings.MELT_ICE)
            this.board.updateIce();
    }

    public Status getStatus() {
        return status;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public State getState(){
        return new State(this.players);
    }

    public Board getBoard() {
        return board;
    }
}
