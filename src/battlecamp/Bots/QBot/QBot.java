package battlecamp.Bots.QBot;

import battlecamp.Model.Board;
import battlecamp.Model.Game;
import battlecamp.Model.Player;
import battlecamp.Model.Tile;

import java.util.*;

public class QBot extends Player {

    private final int trainingEpochs = 100;

    private final float Alpha = 0.5f;
    private final float Gamma = 0.9f;
    private final float Epsilon = 0.1f;

    private Map<StateActionPair, Float> QFunction;
    private StateActionPair previousStateActionPair;



    public QBot(Player.Type type, String name){
        super(type, name);
        this.QFunction = new HashMap<>();
        this.previousStateActionPair = null;
    }

    @Override
    public Game.Move doTrainingMove(){
        int action = getBestAction(this.getCurrentGame().getState());
        if (this.previousStateActionPair != null) {
            float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
            float maxQ = this.QFunction.getOrDefault(new StateActionPair(this.getCurrentGame().getState(), action), 0.0f);
            updateQ(q, maxQ, 0);
        }
        action = determineAction(action);
        // Save state and action
        this.previousStateActionPair = new StateActionPair(this.getCurrentGame().getState(), action);
        // Take action e-greedy
        //action = determineAction(action);
        switch (action) {
            case 0:
                return Game.Move.NORTH;
            case 1:
                return Game.Move.EAST;
            case 2:
                return Game.Move.SOUTH;
            case 3:
                return Game.Move.WEST;
        }
        System.out.println("INVALID MOVE WHILE TRAINING");
        return Game.Move.NORTH;
    }

    @Override
    public void win(){
        this.won = true;
        float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
        this.updateQ(q, 0, 1);
    }

    @Override
    public void die(){
        this.died = false;
        float q = this.QFunction.getOrDefault(this.previousStateActionPair, 0.0f);
        this.updateQ(q, 0, -1);
    }

    @Override
    public Game.Move doMove() {
        int action = getBestAction(this.getCurrentGame().getState());
        switch (action){
            case 0:
                return Game.Move.NORTH;
            case 1:
                return Game.Move.EAST;
            case 2:
                return Game.Move.SOUTH;
            case 3:
                return Game.Move.WEST;
        }
        System.out.println("INVALID MOVE");
        return Game.Move.NORTH;
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
