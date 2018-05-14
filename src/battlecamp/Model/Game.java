package battlecamp.Model;

import battlecamp.Interfaces.Tile;
import battlecamp.Settings;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game{

    public enum GameStatus {INITIALIZING, TRAINING, RUNNING, STOPPED}
    public enum Move {UP, DOWN, LEFT, RIGHT}

    public static GameStatus status;
    private final Board board;
    private final List<Player> players;

    private final int MAX_TRAINING_EPOCHS;
    private final boolean ALLOW_TRAINING;

    public Game(Board board, List<Player> players, boolean allowTraining, int maxEpochs) {

        this.status = GameStatus.INITIALIZING;
        this.board = board;
        this.players = players;
        this.ALLOW_TRAINING = allowTraining;
        this.MAX_TRAINING_EPOCHS = maxEpochs;
        addPlayers();

    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void start(){
        if(this.ALLOW_TRAINING)
            train();
        else
            status = GameStatus.RUNNING;
        play();
        timeout(500);

    }

    private void train(){
        status = GameStatus.TRAINING;
        int episode = 0;
        while(status == GameStatus.TRAINING){
            for(Player currentPlayer : this.players) {
                Move move = currentPlayer.makeMove(this.board, this.players);
                executeMove(currentPlayer, move);
                if(gameEnded()){
                    if(episode == MAX_TRAINING_EPOCHS)
                        status = GameStatus.RUNNING;
                    for(Player player : this.players) {
                        player.reset();
                    }
                    addPlayers();
                    episode++;
                    break;
                }

            }
        }
    }

    private void play(){
        int moves = 0;
        while(!status.equals(GameStatus.STOPPED)){
            for(Player currentPlayer : this.players){
                if(moves++ == Settings.MAX_MOVES)
                    status = GameStatus.STOPPED;
                timeout(200 / this.players.size());
                Move move = currentPlayer.makeMove(this.board, this.players);
                executeMove(currentPlayer, move);
                if(gameEnded()){
                    status = GameStatus.STOPPED;
                    break;
                }
            }
        }
    }

    private void timeout(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean gameEnded(){
        if(this.players.stream().anyMatch(player -> player.getStatus().equals(Player.PlayerStatus.WON)))
            return true;
        return this.players.stream().noneMatch(player -> player.getStatus().equals(Player.PlayerStatus.ALIVE));
    }

    private void executeMove(Player player, Move move){
        int newRow = player.getRow();
        int newCol = player.getColumn();
        switch (move){
            case UP:
                newRow--;
                break;
            case DOWN:
                newRow++;
                break;
            case LEFT:
                newCol--;
                break;
            case RIGHT:
                newCol++;
                break;
        }
        Tile tileToMoveTo = this.board.getTile(newRow, newCol);
        // Try to move the player to the new tile
        if(tileToMoveTo == null)
            return;
        // Check if a player has won or died
        Player otherPlayer = checkForPlayer(tileToMoveTo);
        if(tileToMoveTo.movePlayer(player) && otherPlayer != null){
            if(player.getType().equals(Player.PlayerType.PENGUIN) && otherPlayer.getType().equals(Player.PlayerType.SEA_LION)){
                player.die();
                otherPlayer.win();
            } else if(player.getType().equals(Player.PlayerType.SEA_LION) && otherPlayer.getType().equals(Player.PlayerType.PENGUIN)){
                player.win();
                otherPlayer.die();
            }
        }

    }

    private void addPlayers(){
        for(Player player : this.players){
            List<Tile> validTiles = this.board.getValidSpawningTiles(player);
            validTiles = validTiles.stream().filter(tile -> checkForPlayer(tile) == null).collect(Collectors.toList());
            Tile tile = validTiles.get(new Random().nextInt(validTiles.size()));
            player.move(tile.getRow(), tile.getColumn());
        }
    }

    private Player checkForPlayer(Tile tile){
        return this.players
                .stream()
                .filter(player ->
                        player.getRow() == tile.getRow() && player.getColumn() == tile.getColumn())
                .findFirst()
                .orElse(null);
    }

}
