// Emir Adar
import javax.swing.*;
import java.awt.*;

/**
 * This class represents the board
 * */
public class Board extends JFrame{

    // declaring variables
    private Square[][] squares;
    private Square draggedSquare;
    private MoveListener moveListener;
    private boolean isWhitesTurn;
    private boolean isWhitePlayer;
    private boolean isTheEndOfTheGame;
    private King whiteKing;
    private King blackKing;

    public Board(){initializeGUI();}

    public void startGame(){
        initBoard(isWhitePlayer);
        pack();
        setVisible(true);
    }

    /**
     * This method initializes the GUI for the board
     * */
    private void initializeGUI() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    /**
     * This method initializes the board with empty squares
     * */
    private void initBoard(boolean isWhitePlayer){
        // initializing variables
        this.squares = new Square[8][8];
        String[] columnNames = {"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] rowNames = {"8", "7", "6", "5", "4", "3", "2", "1"};

        // adding the column names to the board
        JPanel columnNamesPanel = new JPanel(new GridLayout(1, 9));
        columnNamesPanel.add(new JLabel(""));
        for (String columnName : columnNames) {
            columnNamesPanel.add(new JLabel(columnName, SwingConstants.CENTER));
        }

        // adding the panel where the squares are going to be
        JPanel squaresPanel = new JPanel(new GridLayout(8, 9));
        for(int i = 0; i <= 7; i++){
            // adding the row names before the squares
            squaresPanel.add(new JLabel(rowNames[i], SwingConstants.CENTER));
            // adding the squares to the panel
            for(int j = 0; j <= 7; j++){
                int row = isWhitePlayer ? i : 7 - i;
                int col = isWhitePlayer ? j : 7 - j;
                if ((row + col) % 2 == 0){
                    this.squares[row][col] = new Square(row, col, null, true, this);
                    squaresPanel.add(this.squares[row][col]);
                } else {
                    this.squares[row][col] = new Square(row, col, null, false, this);
                    squaresPanel.add(this.squares[row][col]);
                }
            }
        }
        add(columnNamesPanel, BorderLayout.NORTH);
        add(squaresPanel, BorderLayout.CENTER);
        setPieces();
    }

    /**
     * This method sets the pieces on the board
     * */
    private void setPieces(){

        // putting ROOKS on the board
        this.squares[0][0].setPiece(new Rook(false, this.squares[0][0]));
        this.squares[0][7].setPiece(new Rook(false, this.squares[0][7]));
        this.squares[7][0].setPiece(new Rook(true, this.squares[7][0]));
        this.squares[7][7].setPiece(new Rook(true, this.squares[7][7]));

        // putting KNIGHTS on the board
        this.squares[0][1].setPiece(new Knight(false, this.squares[0][1]));
        this.squares[0][6].setPiece(new Knight(false, this.squares[0][6]));
        this.squares[7][1].setPiece(new Knight(true, this.squares[7][1]));
        this.squares[7][6].setPiece(new Knight(true, this.squares[7][6]));

        // putting up BISHOPS on the board
        this.squares[0][2].setPiece(new Bishop(false, this.squares[0][2]));
        this.squares[0][5].setPiece(new Bishop(false, this.squares[0][5]));
        this.squares[7][2].setPiece(new Bishop(true, this.squares[7][2]));
        this.squares[7][5].setPiece(new Bishop(true, this.squares[7][5]));

        // putting up QUEENS on the board
        this.squares[0][3].setPiece(new Queen(false, this.squares[0][3]));
        this.squares[7][3].setPiece(new Queen(true, this.squares[7][3]));

        blackKing = new King(false, this.squares[0][4]);
        whiteKing = new King(true, this.squares[7][4]);
        // putting up KINGS on the board
        this.squares[0][4].setPiece(blackKing);
        this.squares[7][4].setPiece(whiteKing);

        // putting PAWNS on the board
        for(int i = 0; i <= 7; i++){
            this.squares[1][i].setPiece(new Pawn(false, this.squares[1][i]));
            this.squares[6][i].setPiece(new Pawn(true, this.squares[6][i]));
        }
    }

    /**
     * This method gets the square with the given coordinates
     * @param row the column of the square
     * @param col the row of the square
     * @return the square with the given coordinates
     * */
    public Square getSquare(int row, int col) {
        if (col < 0 || col > 8 || row < 0 || row > 8) {
            throw new IndexOutOfBoundsException("Outside of borders");
        }
        return this.squares[row][col];
    }

    /**
     * This method gets the square with the given point on the screen
     * @param screenLocation the coordinates for where the mouse is interacting
     * */
    public Square getSquare(Point screenLocation) {
        for (Square[] row : squares) {
            for (Square square : row) {
                Point locationOnScreen = square.getLocationOnScreen();
                if (locationOnScreen != null) {
                    Rectangle squareBoundsOnScreen = new Rectangle(locationOnScreen, square.getSize());
                    if (squareBoundsOnScreen.contains(screenLocation)) {
                        return square;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method gets all the squares on the board
     * @return the squares
     * */
    public Square[][] getSquares() {
        return squares;
    }

    /**
     * This method sets the given square as the dragged square
     * @param square the square that is being dragged
     * */
    public void setDraggedSquare(Square square) {
        this.draggedSquare = square;
    }

    /**
     * This method gets the dragged square
     * @return the dragged square
     * */
    public Square getDraggedSquare() {
        return this.draggedSquare;
    }

    // converting a row and column index to a chess square name, for example a1
    /**
     * This method converts the coordinates to a square name, for example a1
     * @param col the column of the square
     * @param row the row of the square
     * */
    public static String toSquareName(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    // converting a chess square name to a row and column index, for example a1 = 0, 0
    /**
     * This method converts a square name to coordinates, for example a1 = 0, 0
     * @param squareName the square's name
     * */
    public static int[] fromSquareName(String squareName) {
        int col = squareName.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(squareName.charAt(1));
        return new int[] { row, col };
    }

    /**
     * This method sets a given MoveListener to the board
     * @param moveListener the MoveListener that shall be set on the board
     * */
    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    /**
     * This method gets the MoveListener of the board
     * @return the MoveListener attached to the board
     * */
    public MoveListener getMoveListener() {
        return moveListener;
    }

    /**
     * This method sets the turn to be white's turn
     * @param isWhitesTurn should be set to true if it's the white player's turn
     * */
    public void setWhitesTurn(boolean isWhitesTurn) {
        this.isWhitesTurn = isWhitesTurn;
    }

    /**
     * This method checks if it's the white player's turn
     * @return true if it's white player's turn
     * */
    public boolean isWhitesTurn() {
        return isWhitesTurn;
    }

    /**
     * This method sets the color of the players
     * @param whitePlayer should be set to true if this is the white player
     * */
    public void setWhitePlayer(boolean whitePlayer) {
        this.isWhitePlayer = whitePlayer;
    }

    /**
     * This method checks what color the player is
     * @return true if this is the white player, false if it's the black player
     * */
    public boolean isWhitePlayer() {
        return isWhitePlayer;
    }

    /**
     * This method checks if it's this player's turn
     * @return true if it's this player's turn
     * */
    public boolean isThisPlayersTurn(){
        return isWhitesTurn() == isWhitePlayer();
    }

    public boolean isTheEndOfTheGame() {
        return isTheEndOfTheGame;
    }

    public void setTheEndOfTheGame(boolean theEndOfTheGame) {
        this.isTheEndOfTheGame = theEndOfTheGame;
    }

    public King getThisKing() {
        if(isWhitePlayer)
            return whiteKing;
        else
            return blackKing;
    }
}
