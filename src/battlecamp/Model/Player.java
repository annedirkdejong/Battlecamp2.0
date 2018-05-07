package battlecamp.Model;

public abstract class Player {

    public enum Type {PENGUIN, SEALION}

    private final Type type;
    private final String name;

    private int x;
    private int y;
    protected boolean won;
    protected boolean died;
    private Tile tile;

    protected Game currentGame;

    public Player(Type type, String name){
        this.type = type;
        this.name = name;
        this.won = false;
        this.died = false;
    }

    public abstract Game.Move doMove();

    public abstract Game.Move doTrainingMove();

    public void win(){
        this.won = true;
    }

    public void die(){
        this.died = true;
    }

    public boolean hasWon(){
        return this.won;
    }

    public boolean hasDied(){
        return this.died;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Type getType() {
        return type;
    }

    public void joingGame(Game game){
        this.currentGame = game;
    }

    public Game getCurrentGame(){
        return this.currentGame;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void reset(){
        this.tile.removePlayer();
        this.won = false;
        this.died = false;
    }

    public String getName() {
        return name;
    }
}
