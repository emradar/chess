// Emir Adar

/**
 * This class represents a Rook piece
 * */
public class Rook extends Piece {
    public Rook(boolean white, Square position) {
        super(white, white ? "resources/Pictures/Rook_white.png":"resources/Pictures/Rook_black.png");
        super.setPosition(position);
    }

    public boolean canMove(Board board, Square start, Square end) {

        // checking if the end square is occupied by a piece of the same color
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite())
            return false;

        int x = Math.abs(start.getCol() - end.getCol());
        int y = Math.abs(start.getRow() - end.getRow());

        // checking if the move is either in a row or a column
        if (x != 0 && y != 0) {
            return false;
        }

        // check for obstructions in the path
        if (x == 0) {
            int col = start.getCol();
            int startRow = Math.min(start.getRow(), end.getRow());
            int endRow = Math.max(start.getRow(), end.getRow());

            for (int row = startRow + 1; row < endRow; row++) {
                if (board.getSquare(row, col).getPiece() != null) {
                    return false;
                }
            }
        } else if(y == 0){
            int row = start.getRow();
            int startCol = Math.min(start.getCol(), end.getCol());
            int endCol = Math.max(start.getCol(), end.getCol());

            for (int col = startCol + 1; col < endCol; col++) {
                if (board.getSquare(row, col).getPiece() != null) {
                    return false;
                }
            }
        }

        // if all checks pass, the move is valid
        return true;
    }
}