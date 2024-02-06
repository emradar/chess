// Emir Adar
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class represents a square on the board
 * */
public class Square extends JPanel {

    // declaring variables
    private Board board;
    private Piece piece;
    private final int row;
    private final int col;

    /**
     * This is the constructor for this class
     * @param row the row of the square (0-7)
     * @param col the column of the square (0-7)
     * @param piece the piece that is on the square
     * @param isWhite the color of the square
     * @param board the board on which the square is on
     * */
    public Square(int row, int col, Piece piece, boolean isWhite, Board board) {
        this.row = row;
        this.col = col;
        this.piece = piece;
        this.board = board;

        if (isWhite) {
            setBackground(Color.WHITE);
        } else {
            setBackground(Color.GRAY);
        }

        addMouseListener(new SquareMouseListener());
        setPreferredSize(new Dimension(60, 60));
    }

    /**
     * This inner class listens to the mouse, it specifically listens to the mouse being pressed and the mouse being released
     * */
    private class SquareMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent me) {
            Piece piece = Square.this.getPiece();
            // if there is a piece on the square and it's the same color as the player and the player can play
            // the square is being saved
            if (piece != null && piece.isWhite() == board.isWhitePlayer() && board.isThisPlayersTurn()) {
                board.setDraggedSquare(Square.this);
            } else if(board.isTheEndOfTheGame()) {
                System.out.println("End of the game reached. Your opponent won!");
            } else {
                System.out.println("Not your piece");
            }
        }

        public void mouseReleased(MouseEvent me) {
            Square releaseSquare = board.getSquare(me.getLocationOnScreen());
            Board initialState = board;
            Piece draggedPiece = board.getDraggedSquare().getPiece();

            if (releaseSquare != null && draggedPiece != null && !Square.this.equals(releaseSquare) && !board.getThisKing().kingIsInCheckAfterMove(board, board.getDraggedSquare(), releaseSquare)) {

                // if the piece can move to the given square and the move doesn't put the king in check, allow the move
                if (draggedPiece.canMove(board, board.getDraggedSquare(), releaseSquare) && (!board.getThisKing().isAttacked(board) || !board.getThisKing().kingIsInCheckAfterMove(board, board.getDraggedSquare(), releaseSquare))){
                    if (draggedPiece instanceof King)
                        handleKingMove(releaseSquare, draggedPiece);
                    else
                        handleOtherPieceMove(releaseSquare, draggedPiece);
                }
            } else {
                board = initialState;
            }
            board.setDraggedSquare(null);
        }

        private void handleKingMove(Square releaseSquare, Piece draggedPiece) {
            // checking if it's checkmate
            if (((King) draggedPiece).isCheckmate(board)) {
                board.setTheEndOfTheGame(true);
                return;
            }

            // checking for castling move
            if (releaseSquare.getPiece() instanceof Rook && draggedPiece.isWhite() == releaseSquare.getPiece().isWhite()) {
                ((King) draggedPiece).handleCastling(board, board.getDraggedSquare(), releaseSquare);
                return;
            }

            performMove(draggedPiece, releaseSquare);
            if (board.getMoveListener() != null)
                board.getMoveListener().moveMade(board.getDraggedSquare(), releaseSquare, null, null, null);
        }

        private void handleOtherPieceMove(Square releaseSquare, Piece draggedPiece) {
            if(!board.getThisKing().kingIsInCheckAfterMove(board, board.getDraggedSquare(), releaseSquare)){

                if(draggedPiece instanceof Pawn){
                    if((releaseSquare.getRow() == 7 && !board.isWhitePlayer()) || (releaseSquare.getRow() == 0 && board.isWhitePlayer())){
                        board.getDraggedSquare().setPiece(((Pawn) draggedPiece).promotePawn());
                        board.getMoveListener().moveMade(board.getDraggedSquare(), releaseSquare, null, null, board.getDraggedSquare().getPiece().getClass().getSimpleName());
                    }
                }
                performMove(board.getDraggedSquare().getPiece(), releaseSquare);
                if (board.getMoveListener() != null)
                    board.getMoveListener().moveMade(board.getDraggedSquare(), releaseSquare, null, null, null);
            }
        }

        private void performMove(Piece draggedPiece, Square releaseSquare){
            Piece capturedPiece = releaseSquare.getPiece();
            Square originalSquare = draggedPiece.getPosition();
            originalSquare.setPiece(null);
            draggedPiece.setPosition(releaseSquare);

            if (capturedPiece != null)
                releaseSquare.setPiece(null);

            releaseSquare.setPiece(draggedPiece);
            board.getDraggedSquare().setPiece(null);
        }
    }

    /**
     * This method is for setting a piece on the square
     * @param piece the piece that is to be put on the square
     * */
    public void setPiece(Piece piece) {
        if (this.piece != piece) {
            removeAll();
            if (piece != null) {
                ImageIcon icon = new ImageIcon(piece.getImageURL());
                Image image = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                JLabel label = new JLabel(new ImageIcon(image));
                label.setSize(new Dimension(60, 60));
                add(label);
            }
            this.piece = piece;
            revalidate();
            repaint();
        }
    }

    /**
     * This method is for getting the row of the square
     * */
    public int getRow() {
        return row;
    }

    /**
     * This method is for getting the column of the square
     * */
    public int getCol() {
        return col;
    }

    /**
     * This method is for getting the piece on the square
     * */
    public Piece getPiece() {
        return piece;
    }

    /**
     * This method checks if the square is occupied with a square
     * */
    public boolean isOccupied() {
        return piece != null;
    }

    @Override
    public String toString() {
        return "Square[row=" + row + ", col=" + col + "]";
    }
}