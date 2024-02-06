// Emir Adar

/**
 * This class represents a Queen piece
 * */
public class Queen extends Piece {

    public Queen(boolean white, Square position) {
        super(white, white ? "resources/Pictures/Queen_white.png":"resources/Pictures/Queen_black.png");
        super.setPosition(position);
    }

    @Override
    public boolean canMove(Board board, Square start, Square end) {

        // checking if the end square is occupied by a piece of the same color
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite())
            return false;

        // adding temporary pieces to use their methods
        Piece rook = new Rook(this.isWhite(), null);
        Piece bishop = new Bishop(this.isWhite(), null);

        return rook.canMove(board, start, end) || bishop.canMove(board, start, end);
    }
}
