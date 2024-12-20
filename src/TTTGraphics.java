import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TTTGraphics extends JFrame {
    private static final long serialVersionUID = 1L;

    // Define constants
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final int CELL_SIZE = 120;
    public static final int BOARD_WIDTH = CELL_SIZE * COLS;
    public static final int BOARD_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 10;
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final int CELL_PADDING = CELL_SIZE / 5;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
    public static final int SYMBOL_STROKE_WIDTH = 8;

    // Colors and fonts
    public static final Color COLOR_BG = Color.BLACK;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_GRID = Color.WHITE;
    public static final Color COLOR_CROSS = new Color(138, 43, 226);  // Purple
    public static final Color COLOR_NOUGHT = new Color(255, 0, 0);  // Red

    public static final Font FONT_STATUS = new Font("Arial Black", Font.ITALIC, 18);

    // Game state
    private GameState currentState;
    private Seed currentPlayer;
    private Cell[][] cells;
    private boolean isAIMode;
    private AIPlayer aiPlayer;
    private Board gameBoard;

    // UI Components
    private GamePanel gamePanel;
    private JLabel statusBar;
    private JButton newGameButton;
    private JButton switchModeButton;
    private JButton resetScoreButton;
    private JPanel scorePanel;
    private JLabel crossScoreLabel;
    private JLabel noughtScoreLabel;
    private int crossScore = 0;
    private int noughtScore = 0;
    private SoundEffect soundEffect;  // Add sound effect object

    public TTTGraphics() {
        // Initialize sound effects first
        soundEffect = new SoundEffect();

        // Initialize the game
        gameBoard = new Board();
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }

        aiPlayer = new AIPlayerMinimax(gameBoard);
        aiPlayer.setSeed(Seed.NOUGHT);  // AI plays O
        isAIMode = true;  // Start with AI mode by default

        // Set up GUI components
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // Add mouse listener for game moves
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == GameState.PLAYING) {
                    int row = e.getY() / CELL_SIZE;
                    int col = e.getX() / CELL_SIZE;

                    if (row >= 0 && row < ROWS && col >= 0 && col < COLS &&
                            cells[row][col].content == Seed.EMPTY) {
                        // Human move
                        cells[row][col].content = currentPlayer;
                        gameBoard.cells[row][col].content = currentPlayer;
                        updateGameState(currentPlayer, row, col);

                        if (currentState == GameState.PLAYING && isAIMode &&
                                currentPlayer == Seed.CROSS) {  // After human plays X
                            // Switch to AI's turn
                            currentPlayer = Seed.NOUGHT;
                            gamePanel.repaint();

                            // Delay AI move slightly for better UX
                            Timer timer = new Timer(500, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    // AI move
                                    makeAIMove();
                                }
                            });
                            timer.setRepeats(false);
                            timer.start();
                        } else if (!isAIMode && currentState == GameState.PLAYING) {
                            // PVP mode - switch players only if game is still going
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        }
                        gamePanel.repaint();
                    }
                } else if (currentState != GameState.PLAYING) {
                    // Game is over, clicking anywhere starts a new game
                    newGame();
                }
            }
        });

        // Status bar
        statusBar = new JLabel("       ");
        statusBar.setFont(FONT_STATUS);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(COLOR_BG_STATUS);

        // Buttons
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> newGame());

        switchModeButton = new JButton("Switch to PVP");
        switchModeButton.addActionListener(e -> {
            isAIMode = !isAIMode;
            switchModeButton.setText(isAIMode ? "Switch to PVP" : "Switch to AI");
            newGame();
        });

        resetScoreButton = new JButton("Reset Scores");
        resetScoreButton.addActionListener(e -> resetScores());

        // Create score panel
        scorePanel = new JPanel();
        scorePanel.setBackground(COLOR_BG_STATUS);
        scorePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));

        crossScoreLabel = new JLabel("X: 0");
        crossScoreLabel.setFont(FONT_STATUS);
        crossScoreLabel.setForeground(COLOR_CROSS);

        noughtScoreLabel = new JLabel("O: 0");
        noughtScoreLabel.setFont(FONT_STATUS);
        noughtScoreLabel.setForeground(COLOR_NOUGHT);

        JLabel vsLabel = new JLabel("vs");
        vsLabel.setFont(FONT_STATUS);
        scorePanel.add(crossScoreLabel);
        scorePanel.add(vsLabel);
        scorePanel.add(noughtScoreLabel);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(newGameButton);
        buttonPanel.add(switchModeButton);
        buttonPanel.add(resetScoreButton);

        // Create a top panel to hold both score and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(scorePanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Layout
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(topPanel, BorderLayout.NORTH);
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setTitle("Tic Tac Toe with AI");
        setVisible(true);

        newGame();
    }

    private void makeAIMove() {
        if (currentState == GameState.PLAYING && currentPlayer == Seed.NOUGHT) {
            int[] move = aiPlayer.move();
            if (move != null) {
                cells[move[0]][move[1]].content = Seed.NOUGHT;
                gameBoard.cells[move[0]][move[1]].content = Seed.NOUGHT;
                updateGameState(Seed.NOUGHT, move[0], move[1]);

                if (currentState == GameState.PLAYING) {
                    currentPlayer = Seed.CROSS;  // Back to human
                }
                gamePanel.repaint();
            }
        }
    }

    private void updateGameState(Seed theSeed, int row, int col) {
        if (hasWon(theSeed, row, col)) {
            currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
            if (currentState == GameState.CROSS_WON) {
                crossScore++;
                crossScoreLabel.setText("X: " + crossScore);
                if (isAIMode) {
                    soundEffect.playPlayerWinMusic();  // Player wins against AI
                } else {
                    soundEffect.playVictoryMusic();    // PVP victory
                }
            } else {
                noughtScore++;
                noughtScoreLabel.setText("O: " + noughtScore);
                if (isAIMode) {
                    soundEffect.playAIWinMusic();      // AI wins
                } else {
                    soundEffect.playVictoryMusic();    // PVP victory
                }
            }
        } else if (isDraw()) {
            currentState = GameState.DRAW;
            soundEffect.stopBackgroundMusic();  // Stop background music on draw
        }
        // else no change to current state (still GameState.PLAYING)
    }

    private boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.EMPTY) {
                    return false;  // An empty cell found, not a draw
                }
            }
        }
        return true;  // No empty cell, it's a draw
    }

    private boolean hasWon(Seed theSeed, int currentRow, int currentCol) {
        return (cells[currentRow][0].content == theSeed  // 3-in-the-row
                && cells[currentRow][1].content == theSeed
                && cells[currentRow][2].content == theSeed
                || cells[0][currentCol].content == theSeed  // 3-in-the-column
                && cells[1][currentCol].content == theSeed
                && cells[2][currentCol].content == theSeed
                || currentRow == currentCol  // 3-in-the-diagonal
                && cells[0][0].content == theSeed
                && cells[1][1].content == theSeed
                && cells[2][2].content == theSeed
                || currentRow + currentCol == 2  // 3-in-the-opposite-diagonal
                && cells[0][2].content == theSeed
                && cells[1][1].content == theSeed
                && cells[2][0].content == theSeed);
    }

    private void newGame() {
        // Stop any playing sounds and start background music
        if (soundEffect != null) {
            soundEffect.stopAllSounds();
            soundEffect.playBackgroundMusic();
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col].content = Seed.EMPTY;
                gameBoard.cells[row][col].content = Seed.EMPTY;
            }
        }
        currentState = GameState.PLAYING;
        currentPlayer = Seed.CROSS;  // cross plays first
        statusBar.setText("X's Turn");
        gamePanel.repaint();
    }

    private void resetScores() {
        crossScore = 0;
        noughtScore = 0;
        crossScoreLabel.setText("X: 0");
        noughtScoreLabel.setText("O: 0");
    }

    class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(COLOR_BG);

            // Draw grid
            g.setColor(COLOR_GRID);
            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                        BOARD_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
                        GRID_WIDTH, BOARD_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
            }

            // Draw board
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (cells[row][col].content == Seed.CROSS) {
                        g2d.setColor(COLOR_CROSS);
                        int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                        int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                        g2d.drawLine(x1, y1, x2, y2);
                        g2d.drawLine(x2, y1, x1, y2);
                    } else if (cells[row][col].content == Seed.NOUGHT) {
                        g2d.setColor(COLOR_NOUGHT);
                        g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                    }
                }
            }

            // Update status bar
            if (currentState == GameState.PLAYING) {
                statusBar.setForeground(Color.BLACK);
                if (isAIMode && currentPlayer == Seed.NOUGHT) {
                    statusBar.setText("AI is thinking...");
                } else {
                    statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
                }
            } else if (currentState == GameState.DRAW) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("It's a Draw! Click 'New Game' to play again");
            } else if (currentState == GameState.CROSS_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'X' Won! Click 'New Game' to play again");
            } else if (currentState == GameState.NOUGHT_WON) {
                statusBar.setForeground(Color.RED);
                String winner = isAIMode ? "AI" : "'O'";
                statusBar.setText(winner + " Won! Click 'New Game' to play again");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TTTGraphics());
    }
}