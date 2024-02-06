// Emir Adar

import javax.swing.*;

public class Pawn extends Piece {

    public Pawn(boolean white, Square position) {
        super(white, white ? "resources/Pictures/Pawn_white.png":"resources/Pictures/Pawn_black.png");
        super.setPosition(position);
    }

    @Override
    public boolean canMove(Board board, Square start, Square end) {

        // checking if the end square is occupied by a piece of the same color
        if ((end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()))
            return false;

        int y = Math.abs(start.getRow() - end.getRow());
        int x = Math.abs(start.getCol() - end.getCol());

        // checking for a normal move
        if (((y == 2 && x == 0 && isFirstMove()) || (x == 0 && y == 1)) && end.getPiece() == null) {
            this.setFirstMove(false);
            return true;
        }

        // checking for a capture move
        if (x == 1 && y == 1 && end.getPiece() != null) {
            this.setFirstMove(false);
            return true;
        }

        // checking for en passant
        if (x == 1 && y == 1 && end.getPiece() == null) {
            Square left = board.getSquare(start.getRow(), start.getCol() - 1);
            Square right = board.getSquare(start.getRow(), start.getCol() + 1);
            if ((isValidEnPassantSquare(left) || isValidEnPassantSquare(right))) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method returns a chosen piece during a pawn promotion
     * */
    public Piece promotePawn() {

        String[] pieces = {"Queen", "Rook", "Bishop", "Knight"};

        // showing a dialog for the player to choose a piece
        String chosenPiece = (String) JOptionPane.showInputDialog(null, "Choose a piece", "Pawn Promotion",
                JOptionPane.QUESTION_MESSAGE, null, pieces, pieces[0]);

        // replacing the pawn with the chosen piece
        if (chosenPiece != null) {
            switch (chosenPiece) {
                case "Queen":
                    return new Queen(this.isWhite(), this.position);
                case "Rook":
                    return new Rook(this.isWhite(), this.position);
                case "Bishop":
                    return new Bishop(this.isWhite(), this.position);
                case "Knight":
                    return new Knight(this.isWhite(), this.position);
            }
        }
        return null;
    }

    /**
     * This method checks if the move made is an en passant move
     * @param square the square where the enemy is
     * */
    private boolean isValidEnPassantSquare(Square square) {
        return square != null && square.getPiece() instanceof Pawn && square.getPiece().isWhite() != this.isWhite() && square.getPiece().isFirstMove();
    }
}
