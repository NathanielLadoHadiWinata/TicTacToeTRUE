/**
 * Abstract superclass for all AI players with different strategies.
 */
public abstract class AIPlayer {
    protected int ROWS = 3;  // number of rows
    protected int COLS = 3;  // number of columns

    protected Cell[][] cells; // the board's ROWS-by-COLS array of Cells
    protected Seed mySeed;    // computer's seed
    protected Seed oppSeed;   // opponent's seed

    /** Constructor with reference to game board */
    public AIPlayer(Board board) {
        cells = board.cells;
    }

    /** Set/change the seed used by computer and opponent */
    public void setSeed(Seed seed) {
        this.mySeed = seed;
        oppSeed = (mySeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    /** Abstract method to get next move. Return int[2] of {row, col} */
    abstract int[] move();  // to be implemented by subclasses
}