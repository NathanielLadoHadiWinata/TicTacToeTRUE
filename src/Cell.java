/**
 * The Cell class models each individual cell of the TTT 3x3 grid.
 */
public class Cell {  // save as "Cell.java"
    // Define properties (package-visible)
    /** Content of this cell (CROSS, NOUGHT, EMPTY) */
    Seed content;
    /** Row and column of this cell */
    int row, col;

    /** Constructor to initialize this cell */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        clear();
    }

    /** Clear this cell's content to EMPTY */
    public void clear() {
        this.content = Seed.EMPTY;
    }

    /** Paint itself */
    public void paint() {
        switch (content) {
            case CROSS:   System.out.print("X"); break;
            case NOUGHT:  System.out.print("O"); break;
            case EMPTY:   System.out.print(" "); break;
        }
    }
}