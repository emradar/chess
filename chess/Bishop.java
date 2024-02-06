// Emir Adar

/**
 * This class represents a Bishop piece
 * */
public class Bishop extends Piece {

    public Bishop(boolean white, Square position) {
        super(white, white ? "resources/Pictures/Bishop_white.png":"resources/Pictures/Bishop_black.png");
        super.setPosition(position);
    }

    @Override
    public boolean canMove(Board board, Square start, Square end) {

        // checking if the end square is occupied by a piece of the same color
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite())
            return false;

        int startX = start.getCol();
        int startY = start.getRow();
        int endX = end.getCol();
        int endY = end.getRow();

        int diffX = Math.abs(startX - endX);
        int diffY = Math.abs(startY - endY);

        // checking if the path is clear
        if (diffX == diffY) {
            int pathX = startX < endX ? 1 : -1;
            int pathY = startY < endY ? 1 : -1;
            int pathLength = diffX - 1;

            for (int i = 1; i <= pathLength; i++) {
                Square pathSquare = board.getSquare(startY + i * pathY, startX + i * pathX);
                if (pathSquare.isOccupied())
                    return false;
            }
            return true;
        }
        return false;
    }
}