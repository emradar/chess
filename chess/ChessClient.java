// Emir Adar
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

/**This class handles communication between two players of the chess game*/
public class ChessClient implements Runnable, MoveListener{

    // declaring variables
    private Thread thread = new Thread(this);
    private Socket socket = null;
    private volatile boolean running = true;
    private Board board;
    private BufferedReader in;
    private PrintWriter out;
    private static String host = "atlas.dsv.su.se";
    private static int port = 9494;
    public static boolean playersDetermined = false;
    private final Pattern format = Pattern.compile("[a-z]\\d[a-z]\\d( Queen|Rook|Bishop|Knight)?");
    private final Pattern castlingFormat = Pattern.compile("[a-z]\\d[a-z]\\d . [a-z]\\d[a-z]\\d");

    /**
     * This is the constructor for this class
     * @param board It takes a board as a parameter
     * */
    public ChessClient(Board board) {
        // setting the board for this instance
        this.board = board;
        this.board.setMoveListener(this);
        // trying to connect to the socket, opening in and out communication and determining which player THIS player is
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            determineColor();
        } catch (IOException | InterruptedException e) {
            // if an exception occurs, THIS instance is disconnected with the kill method
            JOptionPane.showMessageDialog(null, "Could not connect to the server", "Error", JOptionPane.ERROR_MESSAGE);
            kill();
        }
        thread.start();
    }

    /**
     * This method is used for listening to the incoming messages
     * */
    @Override
    public void run() {

        // determining which player THIS is
        while(!playersDetermined) {
            try {
                determinePlayers();
            } catch (IOException | InterruptedException e) {
                JOptionPane.showMessageDialog(null, "Could not determine players.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // when both players have been determined, they listen to incoming moves
        while(running){

            try {
                String response = in.readLine();
                if(response.equals("END"))
                    JOptionPane.showMessageDialog(null, "This is the end of the game, your opponent has won!", "Error", JOptionPane.ERROR_MESSAGE);
                // however only the player who isn't playing is listening to the moves until the player is switched
                if(!board.isThisPlayersTurn()){
                    listenForMoves();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method is for determining the color of THIS player
     * @throws IOException if there's a problem with the reading and writing
     * @throws InterruptedException if there's a problem where the method is interrupted
     * */
    private void determineColor() throws IOException, InterruptedException {

        // to make sure that there's no timing issues when receiving the message
        socket.setSoTimeout(1000);
        try{
            // if the incoming message is "WHITE" it means that the white player has connected to the server
            String initialMessage = in.readLine();
            if (initialMessage.startsWith("WHITE")) {
                board.setWhitePlayer(false);
                out.println("BLACK");
                board.startGame();//board.flipBoard();
            }
        } catch (SocketTimeoutException e){
            // if there's no incoming message it means that this is the first player connecting, hence it's the white player
            board.setWhitePlayer(true);
            board.startGame();
            out.println("WHITE");
        } finally{
            // when both are connected it's the white's turn and the timeout isn't needed
            board.setWhitesTurn(true);
            socket.setSoTimeout(0);
        }
    }

    /**
     * This method is used for determining the player
     * @throws IOException if there's a problem with the reading and writing
     * @throws InterruptedException if there's a problem where the method is interrupted
     * */
    private synchronized void determinePlayers() throws IOException, InterruptedException {
        String response;
        // once the black player has connected it sends a message that ends the loop
        while ((response = in.readLine()) != null && !response.equals("START")) {
            if (response.equals("BLACK")) {
                playersDetermined = true;
                out.println("START");
            } else if(!playersDetermined && response.equals("WHITE")){
                out.println(response);
                wait(500);
            }
        }
    }

    /**
     * This method is for listening to the incoming moves
     * */
    private synchronized void listenForMoves() {
        try {
            String response;
            while ((response = in.readLine()) != null) {

                if(castlingFormat.matcher(response).matches()){
                    String[] moveParts = response.split(" . ");
                    System.out.println(moveParts[0]);
                    updateBoard(moveParts[0], null);
                    System.out.println(moveParts[1]);
                    updateBoard(moveParts[1], null);
                    switchPlayer();
                    break;
                }

                String[] moveParts = response.split(" ");
                String promotedPieceType = null;
                if (moveParts.length > 1 && (moveParts[1].equals("Queen") || moveParts[1].equals("Rook") || moveParts[1].equals("Bishop") || moveParts[1].equals("Knight"))) {
                    promotedPieceType = moveParts[1];
                }
                if(format.matcher(moveParts[0]).matches()){
                    updateBoard(moveParts[0], promotedPieceType);
                    switchPlayer();
                    break;
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Could not connect to the socket.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is for sending the move to the server
     * @param source the square where the mouse pressed
     * @param destination the square where the mouse released
     * @param rookSource the square where the rook is
     * @param rookDestination the square where the rook should be after the castling move
     * @param promotedPieceType if there is a promotion, this holds the type of the piece
     * */
    private synchronized void sendMove(Square source, Square destination, Square rookSource, Square rookDestination, String promotedPieceType) {
        try {
            String move = Board.toSquareName(source.getRow(), source.getCol()) +
                    Board.toSquareName(destination.getRow(), destination.getCol());

            if(board.isTheEndOfTheGame())
                move = "END";

            if (rookSource != null && rookDestination != null) {
                move += " . " + Board.toSquareName(rookSource.getRow(), rookSource.getRow()) +
                        Board.toSquareName(rookDestination.getRow(), rookDestination.getCol());
            }

            // Add the promoted piece type to the move string
            if (promotedPieceType != null) {
                move += " " + promotedPieceType;
            }

            out.println(move);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not connect to the socket.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is attached to the board and recognizes when a move is made
     * @param source the square where the mouse pressed
     * @param destination the square where the mouse released
     * @param rookSource the square where the rook is
     * @param rookDestination the square where the rook should be after the castling move
     * @param promotedPieceType if there is a promotion, this holds the type of the piece
     * */
    @Override
    public void moveMade(Square source, Square destination, Square rookSource, Square rookDestination, String promotedPieceType) {
        if(board.isThisPlayersTurn()){
            sendMove(source, destination, rookSource, rookDestination, promotedPieceType);
            switchPlayer();
        }
    }

    /**
     * This method is used for updating THIS player's board with the other player's moves
     * @param move the move that the other player made
     * @param promotedPieceType if there is a promotion, this holds the type of the piece
     * */
    private synchronized void updateBoard(String move, String promotedPieceType) {

        // parsing the incoming String
        String sourceSquareName = move.substring(0, 2);
        String destinationSquareName = move.substring(2, 4);

        // translating the String to Integers that correlates to coordinates on the board
        int[] sourceIndices = Board.fromSquareName(sourceSquareName);
        int[] destinationIndices = Board.fromSquareName(destinationSquareName);

        // getting the squares with the given coordinates
        Square sourceSquare = board.getSquare(sourceIndices[0], sourceIndices[1]);
        Square destinationSquare = board.getSquare(destinationIndices[0], destinationIndices[1]);

        // updating the board with the given squares
        Piece piece = sourceSquare.getPiece();
        sourceSquare.setPiece(null);
        if (promotedPieceType != null) {
            switch (promotedPieceType) {
                case "Queen":
                    piece = new Queen(piece.isWhite(), destinationSquare);
                    break;
                case "Rook":
                    piece = new Rook(piece.isWhite(), destinationSquare);
                    break;
                case "Bishop":
                    piece = new Bishop(piece.isWhite(), destinationSquare);
                    break;
                case "Knight":
                    piece = new Knight(piece.isWhite(), destinationSquare);
                    break;
            }
        }
        destinationSquare.setPiece(piece);
    }

    /**
     * This method switches the players' turn
     * */
    private void switchPlayer() {
        board.setWhitesTurn(!board.isWhitesTurn());
        out.println("NEXT");
    }

    /**
     * This method closes the connections to the server
     * */
    private synchronized void kill() {
        running = false;
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not exit the program.", "Error", JOptionPane.ERROR_MESSAGE);;
        }
        System.exit(1);
    }
}



