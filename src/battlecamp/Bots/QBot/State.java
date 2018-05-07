package battlecamp.Bots.QBot;

import battlecamp.Model.Player;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.Objects;

public class State {

    private List<Player.Type> types;
    private List<Point> coords;


    public State(List<Player> players) {

        this.types = new ArrayList<>();
        this.coords = new ArrayList<>();
        players.forEach(player -> {
            this.types.add(player.getType());
            this.coords.add(new Point(player.getX(), player.getY()));
        });

    }

    public State(){}

    public State copy(){
        State s = new State();
        s.setCoords(new ArrayList<>(this.getCoords()));
        s.setTypes(new ArrayList<>(this.getTypes()));
        return s;
    }

    public List<Player.Type> getTypes() {
        return types;
    }

    public void setTypes(List<Player.Type> types) {
        this.types = types;
    }

    public List<Point> getCoords() {
        return coords;
    }

    public void setCoords(List<Point> coords) {
        this.coords = coords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return types.equals(state.getTypes()) &&
                coords.equals(state.getCoords());
    }

    @Override
    public int hashCode() {

        return Objects.hash(types, coords);
    }

    @Override
    public String toString() {
        return "State{" +
                "types=" + types +
                ", coords=" + coords +
                '}';
    }

}
