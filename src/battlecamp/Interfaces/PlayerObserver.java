package battlecamp.Interfaces;

import battlecamp.Model.*;

public interface PlayerObserver {

    void update(Player player, int oldRow, int oldColumn, int newRow, int newColumn);

}
