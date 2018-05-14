package battlecamp.Bots.QBot;


import battlecamp.Interfaces.Bot;
import battlecamp.Model.Board;
import battlecamp.Model.Game;
import battlecamp.Model.Player;
import battlecamp.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class QBot  implements Bot {

    private static ImageIcon imgIcon;

    private final float Alpha = 0.25f;
    private final float Gamma = 0.9f;
    private final float Epsilon = 0.1f;

    private Map<StateActionPair, Float> QFunction;
    private StateActionPair previousStateActionPair;

    public QBot(){
        this.QFunction = new HashMap<>();
        this.previousStateActionPair = null;
    }

    @Override
    public Game.Move makeMove(Player myPlayer, Board board, List<Player> players) {
        if(Game.status.equals(Game.GameStatus.RUNNING)) {
            int action = getBestAction(new State(players));
            switch (action) {
                case 0:
                    return Game.Move.UP;
                case 1:
                    return Game.Move.RIGHT;
                case 2:
                    return Game.Move.DOWN;
                case 3:
                    return Game.Move.LEFT;
            }
            System.out.println("INVALID MOVE");
            return Game.Move.UP;
        }
        else if(Game.status.equals(Game.GameStatus.TRAINING)){
            int action = getBestAction(new State(players));
            if (this.previousStateActionPair != null) {
                float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
                float maxQ = this.QFunction.getOrDefault(new StateActionPair(new State(players), action), 0.0f);
                updateQ(q, maxQ, 0);
            }
            action = determineAction(action);
            // Save state and action
            this.previousStateActionPair = new StateActionPair(new State(players), action);
            // Take action e-greedy
            //action = determineAction(action);
            switch (action) {
                case 0:
                    return Game.Move.UP;
                case 1:
                    return Game.Move.RIGHT;
                case 2:
                    return Game.Move.DOWN;
                case 3:
                    return Game.Move.LEFT;
            }
            System.out.println("INVALID MOVE WHILE TRAINING");
            return Game.Move.UP;
        }
        return Game.Move.UP;
    }

    @Override
    public ImageIcon getImageInstance(int width, int height) {
        if(imgIcon == null || imgIcon.getIconWidth() != width || imgIcon.getIconHeight() != height)
            try {
                imgIcon = new ImageIcon(ImageIO.read(new File(Settings.resourcePath + "penguin.png")).getScaledInstance(width, height, Image.SCALE_SMOOTH));
            } catch (IOException e) { e.printStackTrace(); }
        return imgIcon;
    }

    @Override
    public void win(){
        float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
        this.updateQ(q, 0, 1);
    }

    @Override
    public void die(){
        float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
        this.updateQ(q, 0, -1);
    }

    private void updateQ(float newQ, float maxQ, float reward){
        this.QFunction.put(
                this.previousStateActionPair, newQ + this.Alpha * (reward + this.Gamma * maxQ - newQ)
        );
    }


    private int getBestAction(State s){
        List<Integer> actions = new LinkedList<>(Arrays.asList(0,1,2,3));
        Collections.shuffle(actions);

        float highest = -Float.MAX_VALUE;
        int bestAction = -1;
        for(int i = 0; i < 4; i++){
            float value = this.QFunction.getOrDefault(new StateActionPair(s,actions.get(i)),0.0f);
            if(value > highest){
                highest = value;
                bestAction = i;
            }
        }
        return  actions.get(bestAction);
    }

    private int determineAction(int bestAction){
        List<Integer> actions = new LinkedList<>(Arrays.asList(0,1,2,3));
        actions.remove(bestAction);

        // e-greedy action
        Random r = new Random();
        float e = r.nextInt(1000) / 1000.0f;
        if(e > this.Epsilon)
            return bestAction;
        if(e < this.Epsilon / 3)
            return actions.get(0);
        if(e < (this.Epsilon / 3) * 2)
            return actions.get(1);
        return actions.get(2);
    }
}
