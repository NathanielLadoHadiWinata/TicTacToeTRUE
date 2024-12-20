/**
 * Computer move based on simple table lookup of preferences
 */
public class AIPlayerTableLookup extends AIPlayer {

    // Moves {row, col} in order of preferences. {0, 0} at top-left corner
    private int[][] preferredMoves = {
            {1, 1},             // center
            {0, 0}, {0, 2}, {2, 0}, {2, 2},  // corners
            {0, 1}, {1, 0}, {1, 2}, {2, 1}   // sides
    };

    /** constructor */
    public AIPlayerTableLookup(Board board) {
        super(board);
    }

    /** Get next best move
     * @return int[2] of {row, col}
     */
    @Override
    public int[] move() {
        for (int[] move : preferredMoves) {
            if (cells[move[0]][move[1]].content == Seed.EMPTY) {
                return move;
            }
        }
        return null;
    }
}