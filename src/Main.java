import java.util.Scanner;


/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group 17
 * 1 - 5026231019 - Nathaniel Lado Hadi Winata
 * 2 - 5026231031 - Marco Indrajaya
 */

public class Main {
    private Board board;            // the game board
    private GameState currentState; // the current state of the game
    private Seed currentPlayer;     // the current player
    private AIPlayer aiPlayer;      // AI player
    private boolean isAIMode;       // true if playing against AI
    private static Scanner in = new Scanner(System.in);
    private SoundEffect soundEffect; // Sound effects manager

    // Score tracking
    private int crossScore = 0;
    private int noughtScore = 0;

    /** Constructor to setup the game */
    public Main() {
        board = new Board();  // allocate game board

        // Ask for game mode
        System.out.println("Choose game mode:");
        System.out.println("1. Player vs Player");
        System.out.println("2. Player vs AI");
        int choice = in.nextInt();

        isAIMode = (choice == 2);
        if (isAIMode) {
            aiPlayer = new AIPlayerMinimax(board);
            aiPlayer.setSeed(Seed.NOUGHT);  // AI plays O
        }

        initGame();  // initialize the game board contents and game variables
    }

    /** Initialize the game-board contents and the current states */
    public void initGame() {
        // Initialize sound if not already done
        if (soundEffect == null) {
            soundEffect = new SoundEffect();
        }
        soundEffect.stopAllSounds();
        soundEffect.playBackgroundMusic();

        board.init();  // clear the board contents
        currentPlayer = Seed.CROSS;       // CROSS plays first
        currentState = GameState.PLAYING; // ready to play
    }

    /** Update the currentState after the player with "theSeed" has moved */
    public void updateGame(Seed theSeed) {
        if (board.hasWon(theSeed)) {  // check for win
            currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
        } else if (board.isDraw()) {  // check for draw
            currentState = GameState.DRAW;
        }
        // Otherwise, no change to current state (still GameState.PLAYING).
    }

    /** The main game-loop */
    public void gameLoop() {
        do {
            board.paint();  // display board
            playerMove(currentPlayer); // update the content
            updateGame(currentPlayer); // update currentState

            // Print message if game over
            if (currentState == GameState.CROSS_WON) {
                board.paint();
                crossScore++;
                System.out.println("'X' won!");
                if (isAIMode) {
                    soundEffect.playPlayerWinMusic();  // Player wins against AI
                } else {
                    soundEffect.playVictoryMusic();    // PVP victory
                }
                displayScores();
                if (playAgain()) {
                    initGame();
                    continue;
                }
                soundEffect.stopAllSounds();
                System.out.println("Thanks for playing! Bye!");
                break;
            } else if (currentState == GameState.NOUGHT_WON) {
                board.paint();
                noughtScore++;
                System.out.println("'O' won!");
                if (isAIMode) {
                    soundEffect.playAIWinMusic();      // AI wins
                } else {
                    soundEffect.playVictoryMusic();    // PVP victory
                }
                displayScores();
                if (playAgain()) {
                    initGame();
                    continue;
                }
                soundEffect.stopAllSounds();
                System.out.println("Thanks for playing! Bye!");
                break;
            } else if (currentState == GameState.DRAW) {
                board.paint();
                System.out.println("It's Draw!");
                soundEffect.stopBackgroundMusic();  // Stop background music on draw
                displayScores();
                if (playAgain()) {
                    initGame();
                    continue;
                }
                soundEffect.stopAllSounds();
                System.out.println("Thanks for playing! Bye!");
                break;
            }

            // Switch player
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
        } while (currentState == GameState.PLAYING);  // repeat until game over
    }

    /** Player move */
    public void playerMove(Seed theSeed) {
        boolean validInput = false;
        do {
            if (isAIMode && theSeed == Seed.NOUGHT) {
                // AI's turn
                int[] move = aiPlayer.move();
                if (move != null && board.cells[move[0]][move[1]].content == Seed.EMPTY) {
                    board.cells[move[0]][move[1]].content = theSeed;
                    System.out.println("Computer chose: row " + (move[0] + 1) + " column " + (move[1] + 1));
                    validInput = true;
                }
            } else {
                // Human's turn
                System.out.print("Player '" + theSeed + "', enter your move (row[1-3] column[1-3]): ");
                int row = in.nextInt() - 1;
                int col = in.nextInt() - 1;
                if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                        && board.cells[row][col].content == Seed.EMPTY) {
                    board.cells[row][col].content = theSeed;
                    validInput = true;
                } else {
                    System.out.println("This move at (" + (row + 1) + "," + (col + 1)
                            + ") is not valid. Try again...");
                }
            }
        } while (!validInput);
    }

    /** Display current scores */
    public void displayScores() {
        System.out.println("\n=== SCORES ===");
        System.out.println("Player X: " + crossScore);
        System.out.println("Player O: " + noughtScore);
        System.out.println("============\n");
    }

    /** Ask if players want to play again */
    private boolean playAgain() {
        System.out.print("Play again? (y/n): ");
        char response = in.next().charAt(0);
        return (response == 'y' || response == 'Y');
    }

    /** The entry main() method */
    public static void main(String[] args) {
        new Main().gameLoop();  // start the game
    }
}