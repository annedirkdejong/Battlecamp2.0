package battlecamp.Interfaces;

import battlecamp.Model.*;

public interface Observer {

    void update(Tile tile);
    void update(Board board);

}
