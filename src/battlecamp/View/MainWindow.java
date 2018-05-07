package battlecamp.View;

import battlecamp.Interfaces.Observer;
import battlecamp.Model.Board;
import battlecamp.Model.Game;
import battlecamp.Model.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame implements Observer {

    private List<JButton> buttons;
    private JPanel mainPanel;

    public MainWindow(){
        this.buttons = new ArrayList<>();
        this.setSize(1000,1000);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        int xPos = (dim.width / 2) - (this.getWidth() / 2);
        int yPos = (dim.height / 2) - (this.getHeight() / 2);
        this.setLocation(xPos, yPos);

        this.mainPanel = new JPanel();
        this.add(this.mainPanel);

        this.setTitle("Battlecamp 2.0");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void update(Tile tile) {
        if(Game.status == Game.Status.RUNNING) {
            JButton b = this.buttons.get(tile.getY() * (int) Math.sqrt((double) this.buttons.size()) + tile.getX());
            if (tile.getPlayer() != null) {
                switch (tile.getPlayer().getType()) {
                    case PENGUIN:
                        b.setBackground(Color.GREEN);
                        break;
                    case SEALION:
                        b.setBackground(Color.RED);
                        break;
                }
            } else if (tile.getType() == Tile.Type.IGLOO) {
                b.setBackground(Color.CYAN);
            } else {
                b.setBackground(Color.WHITE);
            }
            validate();
        }
    }

    @Override
    public void update(Board board) {
        this.setVisible(true);
        this.buttons = new ArrayList<>();
        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new GridLayout(board.getRows(), board.getColumns()));
        List<Tile> tiles = board.getTiles();
        for(int row = 0; row < board.getRows(); row++){
            for(int col = 0; col < board.getColumns(); col++) {
                JButton btn = new JButton();
                btn.setEnabled(false);
                if (tiles.get(col * board.getColumns() + row).getType() == Tile.Type.ROCK) {
                    btn.setBackground(Color.gray);
                } else if (tiles.get(col * board.getColumns() + row).getType() == Tile.Type.IGLOO) {
                    btn.setBackground(Color.CYAN);
                } else {
                    btn.setBackground(Color.WHITE);
                }
                this.buttons.add(btn);
                this.mainPanel.add(btn);
            }
        }
        this.add(mainPanel);
        validate();
    }
}

