// Emir Adar

/**
 * This is an abstract class that represents a Piece
 * */
public abstract class Piece {

    protected final boolean white;
    protected boolean firstMove = true;
    protected Square position;
    protected String imageURL;

    public Piece(boolean white, String imageURL){
        this.white = white;
        this.imageURL = imageURL;
    }

    /**
     * This method checks which color the piece is
     * @return true if the piece is white
     * */
    public boolean isWhite() {
        return white;
    }

    /**
     * This method checks if it's this piece's first move
     * @return true if it's the first move
     * */
    protected boolean isFirstMove() {
        return firstMove;
    }

    /**
     * This method sets the firstmove variable for this piece
     * */
    protected void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    /**
     * This method sets the position of the piece
     * */
    public void setPosition(Square position) {
        this.position = position;
    }

    /**
     * This method checks the position of the piece
     * @return the position of this piece
     * */
    public Square getPosition() {
        return position;
    }

    /**
     * Determines if the piece can move from the start square to the end square on the given board.
     * @param board the chess board
     * @param start the starting square
     * @param end the ending square
     * @return true if the move is valid, false otherwise
     */
    protected abstract boolean canMove(Board board, Square start, Square end);

    /**
     * This method gets the ImageURL of the image associated with this piece
     * */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * This method checks if the given square(piece) is under attack
     * @param board the board that is being checked
     * */
    public boolean isAttacked(Board board) {
        for (Square[] row : board.getSquares()) {
            for (Square square : row) {
                Piece piece = square.getPiece();
                if (piece != null) {
                    if (piece.isWhite() != this.isWhite()) {
                        if (piece instanceof King) {
                            continue;
                        }
                        if (piece.canMove(board, square, board.getThisKing().getPosition())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * This method checks if the king is in check after a performed move
     * @param board the board that is being checked
     * @param start the source square
     * @param end the destination square
     * */
    public boolean kingIsInCheckAfterMove(Board board, Square start, Square end) {
        // saving the original pieces at the start and end squares
        Piece originalStartPiece = start.getPiece();
        Piece originalEndPiece = end.getPiece();

        // simulating the move
        start.setPiece(null);
        end.setPiece(this);

        // checking if the king of the current player is in check
        King currentKing = board.getThisKing();
        boolean kingInCheck = currentKing != null && currentKing.isAttacked(board);

        // undoing the move
        start.setPiece(originalStartPiece);
        end.setPiece(originalEndPiece);

        // If the king is in check after the move, the move is not valid
        return kingInCheck;
    }
}