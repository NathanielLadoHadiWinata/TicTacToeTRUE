/**
 * The Board class models the TTT game-board of 3x3 cells.
 */
public class Board {
    // Define named constants for the grid
    public static final int ROWS = 3;
    public static final int COLS = 3;

    // Define properties (package-visible)
    /** A board composes of [ROWS]x[COLS] Cell instances */
    Cell[][] cells;

    /** Constructor to initialize the game board */
    public Board() {
        initGame();
    }

    /** Initialize the board (run once) */
    public void initGame() {
        cells = new Cell[ROWS][COLS];  // allocate the array
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                // Allocate element of the array
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    /** Initialize the contents of the game board */
    public void init() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].clear();  // clear the cell content
            }
        }
    }

    /** Return true if it is a draw (i.e., no more empty cell) */
    public boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.EMPTY) {
                    return false;  // an empty cell found, not a draw
                }
            }
        }
        return true;  // no empty cell, it's a draw
    }

    /** Return true if the player with "theSeed" has won after placing at
     (currentRow, currentCol) */
    public boolean hasWon(Seed theSeed) {
        return (cells[0][0].content == theSeed && cells[0][1].content == theSeed && cells[0][2].content == theSeed    // row 0
                || cells[1][0].content == theSeed && cells[1][1].content == theSeed && cells[1][2].content == theSeed // row 1
                || cells[2][0].content == theSeed && cells[2][1].content == theSeed && cells[2][2].content == theSeed // row 2
                || cells[0][0].content == theSeed && cells[1][0].content == theSeed && cells[2][0].content == theSeed // col 0
                || cells[0][1].content == theSeed && cells[1][1].content == theSeed && cells[2][1].content == theSeed // col 1
                || cells[0][2].content == theSeed && cells[1][2].content == theSeed && cells[2][2].content == theSeed // col 2
                || cells[0][0].content == theSeed && cells[1][1].content == theSeed && cells[2][2].content == theSeed // diagonal
                || cells[0][2].content == theSeed && cells[1][1].content == theSeed && cells[2][0].content == theSeed); // reverse diagonal
    }

    /** Paint itself */
    public void paint() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                System.out.print(" ");
                cells[row][col].paint();   // each cell paints itself
                System.out.print(" ");
                if (col < COLS - 1) System.out.print("|");  // column separator
            }
            System.out.println();
            if (row < ROWS - 1) {
                System.out.println("-----------");  // row separator
            }
        }
        System.out.println();
    }
}