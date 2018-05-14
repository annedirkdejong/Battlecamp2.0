package battlecamp.View;

import battlecamp.Interfaces.PlayerObserver;
import battlecamp.Interfaces.SettingsObserver;
import battlecamp.Interfaces.SettingsSubject;
import battlecamp.Interfaces.Tile;
import battlecamp.Model.Board;
import battlecamp.Model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BattlecampViewer extends JFrame implements PlayerObserver, SettingsSubject {
    private SettingsObserver observer;
    private List<JLabel> tiles;
    private Board board;

    private int boardWidth;
    private int boardHeight;
    private int iconWidth;
    private int iconHeight;

    private JPanel mainPanel;
    private JPanel boardPanel;
    private JPanel settingsPanel;
    private JPanel gameSettingsPanel;
    private JPanel dimensionsPanel;
    private JCheckBox loopCheckBox;
    private JCheckBox meltIceCheckBox;
    private JButton startButton;
    private JButton stopButton;
    private JComboBox widthComboBox;
    private JComboBox heightComboBox;
    private JCheckBox allowTrainingCheckBox;
    private JTextField epochsTextField;
    private JTabbedPane tabbedPane1;
    private JPanel botPanel;
    private JPanel benchmarkPanel;
    private JTabbedPane tabbedPane2;
    private JLabel avgTrainingLabel;
    private JCheckBox qBotCheckBox;
    private JCheckBox dijkstraBotCheckBox;
    private JTextField qBotAlpha;
    private JTextField qBotGamma;
    private JTextField qBotEpsilon;

    public BattlecampViewer(){
        setUI();
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        int xPos = (dim.width / 2) - (this.getWidth() / 2);
        int yPos = (dim.height / 2) - (this.getHeight() / 2);
        this.setLocation(xPos, yPos);
        this.setTitle("Battlecamp 2.0");
        this.widthComboBox.setSelectedIndex(1);
        this.heightComboBox.setSelectedIndex(1);
        this.setVisible(true);

        updateBoardSize(10, 10);
        initBoard();
        setListeners();
    }

    @Override
    public void update(Player player, int oldRow, int oldColumn, int newRow, int newColumn) {
        getTile(oldRow, oldColumn).setIcon(this.board.getTile(oldRow, oldColumn).getImageInstance(this.iconWidth, this.iconHeight));
        getTile(newRow, newColumn).setIcon(
                new ImageIcon(
                        combineImages(
                                this.board.getTile(newRow, newColumn).getImageInstance(this.iconWidth, this.iconHeight),
                                player.getImageInstance(this.iconWidth, this.iconHeight)
                        )
                )
        );
    }

    private void updateBoardSize(int width, int height){
        this.boardWidth = width;
        this.boardHeight = height;
        this.iconWidth = this.boardPanel.getWidth() / width;
        this.iconHeight = this.boardPanel.getHeight() / height;
    }

    public void displayBoard(Board board) {
        this.board = board;
        for(int row = 0; row < this.boardHeight; row++) {
            for (int col = 0; col < this.boardWidth; col++) {
                Tile tile = board.getTile(row, col);
                getTile(row, col).setIcon(tile.getImageInstance(this.iconWidth, this.iconHeight));
            }
        }
        this.boardPanel.repaint();
    }

    private void initBoard(){
        this.tiles = new ArrayList<>();
        this.boardPanel.removeAll();
        this.boardPanel.setLayout(new GridLayout(this.boardHeight, this.boardWidth));
        for(int row = 0; row < this.boardHeight; row++) {
            for (int col = 0; col < this.boardWidth; col++) {
                JLabel newTile = new JLabel();
                newTile.setHorizontalAlignment(SwingConstants.CENTER);
                newTile.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                this.tiles.add(newTile);
                this.boardPanel.add(newTile);
            }
        }
        this.boardPanel.validate();
    }

    private JLabel getTile(int row, int column){
        if(row >= 0 && row < this.boardHeight && column >= 0 && column < this.boardWidth)
            return this.tiles.get(row * this.boardWidth + column);
        return null;
    }

    private BufferedImage combineImages(ImageIcon img1, ImageIcon img2){
        BufferedImage combi = new BufferedImage(img1.getIconWidth(), img1.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combi.createGraphics();
        g.drawImage(ImageIcon_To_BufferedImage(img1), 0, 0, null);
        g.drawImage(ImageIcon_To_BufferedImage(img2), 0, 0, null);
        g.dispose();
        return combi;
    }

    private BufferedImage ImageIcon_To_BufferedImage(ImageIcon img){
        BufferedImage BI = new BufferedImage(
                img.getIconWidth(),
                img.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics g = BI.createGraphics();
        img.paintIcon(null, g, 0, 0);
        g.dispose();
        return BI;
    }

    @Override
    public void register(SettingsObserver o) {
        this.observer = o;
    }

    @Override
    public void unregister(SettingsObserver o) {
        if(this.observer.equals(o))
            this.observer = null;
    }

    private void setUI(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void setListeners(){
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                observer.stopGame();
                try {
                    int newWidth = Integer.parseInt(Objects.requireNonNull(widthComboBox.getSelectedItem()).toString());
                    int newHeight = Integer.parseInt(Objects.requireNonNull(heightComboBox.getSelectedItem()).toString());
                    int trainingEpochs = Integer.parseInt(Objects.requireNonNull(epochsTextField.getText()));
                    if(newWidth != boardWidth || newHeight != boardHeight) {
                        updateBoardSize(newWidth, newHeight);
                        initBoard();
                    }
                    observer.startGame(
                            boardWidth,
                            boardHeight,
                            loopCheckBox.isSelected(),
                            meltIceCheckBox.isSelected(),
                            allowTrainingCheckBox.isSelected(),
                            trainingEpochs,
                            qBotCheckBox.isSelected(),
                            dijkstraBotCheckBox.isSelected()
                    );

                }
                catch (NumberFormatException nfe){
                    System.out.println("Error: Failed to parse board dimensions'n " + nfe.getStackTrace());
                }
            }
        });
        loopCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                observer.setLooping(loopCheckBox.isSelected());
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                observer.stopGame();
            }
        });
        allowTrainingCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                epochsTextField.setEnabled(allowTrainingCheckBox.isSelected());
            }
        });
        qBotCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                qBotAlpha.setEnabled(qBotCheckBox.isSelected());
                qBotGamma.setEnabled(qBotCheckBox.isSelected());
                qBotEpsilon.setEnabled(qBotCheckBox.isSelected());
            }
        });
    }
}
