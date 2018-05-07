package battlecamp.Bots.QBot;

import java.util.Objects;

public class StateActionPair {

    private State state;
    private int action;

    public StateActionPair(State state, int action){
        this.state = state;
        this.action = action;
    }

    public State getState() { return state; }

    public void setState(State state) { this.state = state; }

    public int getAction() { return action; }

    public void setAction(int action) { this.action = action; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateActionPair that = (StateActionPair) o;
        return action == that.getAction() &&
                state.equals(that.getState());//Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {

        return Objects.hash(state, action);
    }

    @Override
    public String toString() {
        return "StateActionPair{" +
                "state=" + state +
                ", action=" + action +
                '}';
    }

}
