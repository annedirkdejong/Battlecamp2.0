package battlecamp.Interfaces;

public interface SettingsObserver {

    void startGame(int width, int height, boolean loop, boolean meltIce, boolean allowTraining, int maxTrainingEpochs, boolean qBot, boolean dijkstraBot);
    void stopGame();
    void setLooping(boolean value);


}
