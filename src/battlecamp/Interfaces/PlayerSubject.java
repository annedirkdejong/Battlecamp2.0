package battlecamp.Interfaces;

public interface PlayerSubject {

    void register(PlayerObserver o);
    void unregister(PlayerObserver o);
    void notifyObserver(int oldRow, int oldColumn, int newRow, int newColumn);

}
