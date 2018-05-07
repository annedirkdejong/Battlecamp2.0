package battlecamp;

import battlecamp.Bots.QBot.QBot;
import battlecamp.Bots.SimpelHunter.SimpelHunter;
import battlecamp.Model.*;
import battlecamp.View.*;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private MainWindow viewer;

    private List<Game> games;
    private List<Player> players;

    public Controller(){
        this.games = new ArrayList<>();
        this.players = new ArrayList<>();
        this.viewer = new MainWindow();

        // -- Add players here --
        this.players.add(new QBot(Player.Type.PENGUIN, "myQBot"));
        this.players.add(new SimpelHunter(Player.Type.SEALION, "SeaLion"));

    }

    public void start(){
        if(!this.players.isEmpty() && games.stream().noneMatch(game -> game.getStatus() == Game.Status.RUNNING)){
            while (true) {
                Board board = new Board(Settings.BOARD_WIDTH, Settings.BOARD_HEIGHT, this.viewer);
                board.register(this.viewer);
                games.add(new Game(
                        this.games.size(),
                        board,
                        this.players)
                );

            }
        }
    }
}
