package gamelogic;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    private final Board board;
    private Colour whoseTurn;
    private final Map<String, Integer> positionsCounterOnWhite, positionsCounterOnBlack;

    public Game() {
        this.positionsCounterOnBlack = new HashMap<>();
        this.positionsCounterOnWhite = new HashMap<>();
        this.board = new Board();
        this.whoseTurn = Colour.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public Colour whoseTurn() {
        return whoseTurn;
    }

    public String getFEN() {
        StringBuilder fen = new StringBuilder(whoseTurn.toString() + ":");
        StringBuilder white = new StringBuilder("W");
        StringBuilder black = new StringBuilder("B");
        for (int i = 9; i >= 0; i--) {
            for (int j = 0; j <= 9; j++) {
                Piece piece = getPosition(j, i);
                if (piece != null) {
                    String pieceNotation = (piece instanceof King ? "K" : "") + piece.getCords().getCheckersNotation();
                    if (piece.getColour() == Colour.WHITE) white.append(pieceNotation).append(",");
                    else black.append(pieceNotation).append(",");
                }
            }
        }

        if (black.charAt(black.length() - 1) == ',') black.deleteCharAt(black.length() - 1);
        if (white.charAt(white.length() - 1) == ',') white.deleteCharAt(white.length() - 1);

        fen.append(white).append(":").append(black);

        return fen.toString();
    }

    public String getPDN() {
        return board.getPDN();
    }

    public char whoWon() {
        // checking divided into two parts due to time cost
        // less expensive conditions
        if (board.getWhitePieces().isEmpty() || board.getPossibleMoves(Colour.WHITE).isEmpty())
            return 'b';
        if (board.getBlackPieces().isEmpty() || board.getPossibleMoves(Colour.BLACK).isEmpty())
            return 'w';

        if (board.getFakeMovesCounter()>=50) return 'd';

        // more expensive conditions
        if ((positionsCounterOnWhite.containsKey(getFEN()) && positionsCounterOnWhite.get(getFEN()) >= 3)
                || (positionsCounterOnBlack.containsKey(getFEN()) && positionsCounterOnBlack.get(getFEN()) >= 3) )
            return 'd';

        if (board.getWhitePieces().size() == 1 && board.getBlackPieces().size() == 1
                && board.getWhitePieces().get(0) instanceof King && board.getBlackPieces().get(0) instanceof King)
            return 'd';

        return '0';
    }

    public Piece getPosition(int x, int y) {
        return board.getGameboard()[x][y];
    }

    public List<Coordinates> getMovesForPosition(int x, int y) {
        if (board.getGameboard()[x][y] == null || board.getGameboard()[x][y].getColour() != whoseTurn)
            return new ArrayList<>();
        else return board.getPossibleMoves(board.getGameboard()[x][y]);
    }

    private void switchTurn() {
        if (whoseTurn == Colour.WHITE) {
            whoseTurn = Colour.BLACK;
            positionsCounterOnBlack.merge(getFEN(), 1, Integer::sum);
        } else {
            whoseTurn = Colour.WHITE;
            positionsCounterOnWhite.merge(getFEN(), 1, Integer::sum);
        }
    }

    public void makeMove(int xOrigin, int yOrigin, int xDestination, int yDestination) {
        Piece movingPiece = board.getGameboard()[xOrigin][yOrigin];
        if (movingPiece == null)
            throw new IllegalArgumentException("No piece on given position: " + xOrigin + "x" + yOrigin);
        if (movingPiece.getColour() != whoseTurn)
            throw new IllegalArgumentException("Move of player with no turn (" + movingPiece.getColour().name() + ") submitted");
        else if (board.movePiece(movingPiece, xDestination, yDestination)) switchTurn();
    }
}
