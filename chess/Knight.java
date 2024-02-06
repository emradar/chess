// Emir Adar

/**
 * This class represents a Knight piece
 * */
public class Knight extends Piece {

    public Knight(boolean white, Square position) {
        super(white, white ? "resources/Pictures/Knight_white.png":"resources/Pictures/Knight_black.png");
        super.setPosition(position);
    }

    @Override
    public boolean canMove(Board board, Square start, Square end) {

        // checking if the end square is occupied by a piece of the same color
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false;
        }

        int x = Math.abs(start.getCol() - end.getCol());
        int y = Math.abs(start.getRow() - end.getRow());

        return (x == 2 && y == 1) || (x == 1 && y == 2);
    }
}