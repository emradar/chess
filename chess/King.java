// Emir Adar

/**
 * This class represents a King piece
 * */
public class King extends Piece {

    private Square position;

    public King(boolean white, Square position) {
        super(white, white ? "resources/Pictures/King_white.png":"resources/Pictures/King_black.png");
        this.position = position;
    }

    @Override
    public boolean canMove(Board board, Square start, Square end) {

        // checking if the end square is occupied by a piece of the same color and it's not a Rook
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite() && !(end.getPiece() instanceof Rook)) {
            return false;
        }

        int y = Math.abs(start.getRow() - end.getRow());
        int x = Math.abs(start.getCol() - end.getCol());

        // Check for a normal move
        if ((x == 1 || y == 1) && !this.isCastlingMove(board, start, end)) {
            return true;
        }

        return this.isCastlingMove(board, start, end);
    }


    @Override
    public Square getPosition() {
        return position;
    }

    private boolean pathIsClear(Board board, Square start, Square end) {
        int startCol = start.getCol();
        int endCol = end.getCol();
        int row = start.getRow();

        int step = startCol < endCol ? 1 : -1;

        for (int col = startCol + step; col != endCol; col += step) {
            Square square = board.getSquare(row, col);
            if (square.getPiece() != null)
                return false;
        }
        return true;
    }

    private boolean kingIsInCheck(Board board) {
        return this.isAttacked(board);
    }

    /**
     * This method checks if the move is valid a castling move
     * @param board the board
     * @param start the start square
     * @param end the end square
     * @return true if it is a valid castling move
     * */
    public boolean isCastlingMove(Board board, Square start, Square end) {
        // checking if the piece at the end square is a rook of the same color
        if (end.getPiece() instanceof Rook && end.getPiece().isWhite() == this.isWhite()) {
            // checking if the king and the rook have not moved yet
            if (this.isFirstMove() && end.getPiece().isFirstMove()) {
                // checking if the path between the king and the rook is clear and the king is not in check after the move
                if (pathIsClear(board, start, end) && !isInCheckAfterCastling(board, start, end)) {
                    // The path is clear and the king is not in check, so this is a valid castling move
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInCheckAfterCastling(Board board, Square start, Square end) {
        // saving the original positions
        Piece originalKing = start.getPiece();
        Piece originalRook = end.getPiece();

        // determining the direction of the castling move
        int direction = end.getCol() > start.getCol() ? 1 : -1;

        // simulating the castling move
        start.setPiece(null);
        Square kingEnd = board.getSquare(start.getRow(), start.getCol() + 2*direction);
        Square rookEnd = board.getSquare(start.getRow(), start.getCol() + direction);

        kingEnd.setPiece(originalKing);
        rookEnd.setPiece(originalRook);
        originalKing.setPosition(kingEnd);
        originalRook.setPosition(rookEnd);

        // checking if the king is in check
        boolean isInCheck = kingIsInCheck(board);
        // undoing the move
        start.setPiece(originalKing);
        kingEnd.setPiece(null);
        rookEnd.setPiece(null);
        originalRook.setPosition(rookEnd);

        return isInCheck;
    }

    /**
     * This method checks if there has been a checkmate
     * @param board the board
     * @return true if it's checkmate
     * */
    public boolean isCheckmate(Board board) {
        if (kingIsInCheck(board)) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Square square = board.getSquare(i, j);
                    Piece piece = square.getPiece();
                    if (piece != null && piece.isWhite() == this.isWhite()) {
                        for (int k = 0; k < 8; k++) {
                            for (int l = 0; l < 8; l++) {
                                Square endSquare = board.getSquare(k, l);
                                if (piece.canMove(board, square, endSquare) && !kingIsInCheckAfterMove(board, square, endSquare)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }


    /**
     * This method handles the castling move
     * @param board the board
     * @param start the start square
     * @param end the end square
     * */
    public void handleCastling(Board board, Square start, Square end) {
        // Determine the direction of the castling move
        int direction = end.getCol() > start.getCol() ? 1 : -1;

        Square kingEnd = board.getSquare(start.getRow(), start.getCol() + 2*direction);
        Square rookStart = board.getSquare(7, end.getCol());
        Square rookEnd = board.getSquare(7, kingEnd.getCol() - direction);

        if (rookStart != null && rookEnd != null && rookStart.getPiece() instanceof Rook && rookStart.getPiece().isFirstMove()) {

            // moving the rook to its new position
            rookEnd.setPiece(rookStart.getPiece());
            rookStart.setPiece(null);
            rookEnd.getPiece().setPosition(rookEnd);

            // moving the king to its new position
            kingEnd.setPiece(this);
            start.setPiece(null);
            this.setPosition(kingEnd);

            // updating the firstMove attribute of the king and the rook
            this.setFirstMove(false);
            rookEnd.getPiece().setFirstMove(false);
            board.getMoveListener().moveMade(start, kingEnd, rookStart, rookEnd, null);
        }
    }
}
