package battlecamp;

import battlecamp.Bots.DijkstraBot.DijkstraBot;
import battlecamp.Bots.QBot.QBot;
import battlecamp.Interfaces.SettingsObserver;
import battlecamp.Model.Board;
import battlecamp.Model.Game;
import battlecamp.Model.Player;
import battlecamp.View.BattlecampViewer;

import java.util.ArrayList;
import java.util.List;

public class Controller implements SettingsObserver {

    private final BattlecampViewer viewer;

    private int boardWidth;
    private int boardHeight;
    private boolean looping;
    private boolean meltIce;
    private Game currentGame;

    public Controller(){
        this.viewer = new BattlecampViewer();
        this.viewer.register(this);
    }

    @Override
    public void startGame(int width, int height, boolean loop, boolean melt, boolean allowTraining, int maxTrainingEpochs, boolean qBot, boolean dijkstraBot) {
        Thread gameThread = new Thread(){
            @Override
            public void run(){
                looping = loop;
                meltIce = melt;
                if(currentGame != null)
                    stopGame();

                do {
                    // Todo: Retrieve players from Viewer
                    List<Player> players = new ArrayList<>();

                    if(qBot) {
                        Player qBot = new Player(Player.PlayerType.PENGUIN, new QBot(), "QBot");
                        qBot.register(viewer);
                        players.add(qBot);
                    }

                    if(dijkstraBot) {
                        Player simpleHunter = new Player(Player.PlayerType.SEA_LION, new DijkstraBot(), "DijkstraBot");
                        simpleHunter.register(viewer);
                        players.add(simpleHunter);
                    }

                    // Create board
                    Board board = new Board(height, width);
                    viewer.displayBoard(board);
                    // Initialize & start the game
                    currentGame = new Game(board, players, allowTraining, maxTrainingEpochs);
                    currentGame.start();
                }while (looping);
                stopGame();
            }
        };
        gameThread.start();
    }

    @Override
    public void stopGame() {
        if(this.currentGame != null && !this.currentGame.getStatus().equals(Game.GameStatus.STOPPED)) {
            this.currentGame.setStatus(Game.GameStatus.STOPPED);
        }
        this.currentGame = null;
    }

    @Override
    public void setLooping(boolean value) {
        this.looping = value;
    }
}
