package gamelogic;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Board board;
    private int mode;

    private Colour whoseTurn;
    private String firstPlayersName, secondPlayersName;

    public Game(int mode) {
        this.board = new Board();
        this.mode = mode;
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

        if (black.charAt(black.length()-1)==',') black.deleteCharAt(black.length()-1);
        if (white.charAt(white.length()-1)==',') white.deleteCharAt(white.length()-1);

        fen.append(white).append(":").append(black);

        return fen.toString();
    }

    public Colour whoWon() {
        boolean whiteLost = true, blackLost = true;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board.getGameboard()[i][j] != null && board.getGameboard()[i][j].getColour() == Colour.WHITE)
                    whiteLost = false;
                if (board.getGameboard()[i][j] != null && board.getGameboard()[i][j].getColour() == Colour.BLACK)
                    blackLost = false;
            }
        }
        if (!whiteLost && !blackLost) return null;
        else if (whiteLost) return Colour.BLACK;
        else return Colour.WHITE;
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
        if (whoseTurn == Colour.WHITE) whoseTurn = Colour.BLACK;
        else whoseTurn = Colour.WHITE;
    }

    public void makeMove(int xOrigin, int yOrigin, int xDestination, int yDestination) {
        Piece movingPiece = board.getGameboard()[xOrigin][yOrigin];
        if (movingPiece == null) throw new IllegalArgumentException("No piece on given position!");
        else if (board.movePiece(movingPiece, xDestination, yDestination)) switchTurn();
    }
}
