package gamelogic;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public Board getBoard() {
        return board;
    }

    private Board board;
    private int mode;
    Colour whoseTurn;
    private String firstPlayersName, secondPlayersName;

    public Game(int mode) {
        board = new Board();
        this.mode = mode;
        whoseTurn = Colour.WHITE;
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
