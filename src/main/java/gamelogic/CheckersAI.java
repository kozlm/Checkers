package gamelogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class CheckersAI {
    private final int depth;
    private Board board;
    private final Colour colour, enemyColour;

    int valueIndicator;

    public CheckersAI(Colour colour, int depth, Board board) {
        this.depth = depth;
        this.colour = colour;
        this.board = board;
        this.enemyColour = colour == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
        this.valueIndicator = colour == Colour.WHITE ? 1 : -1;
    }

    public Pair<Coordinates, Coordinates> findBestMove() {
        PriorityQueue<Pair<Double, Pair<Coordinates, Coordinates>>> valuedMoves = new PriorityQueue<>();
        for (Pair<Coordinates, Coordinates> move : board.getPossibleMoves(colour)) {
            Board clonedBoard = board.cloneBoard();
            valuedMoves.add(new Pair<>(findMinMoveValue(clonedBoard, depth, colour, move), move));
        }
        if (valuedMoves.isEmpty()) throw new IllegalArgumentException("No moves available for given parameters!");
        return valuedMoves.peek().getValue();
    }

    public double findMinMoveValue(Board board, int currentDepth, Colour whoseTurn, Pair<Coordinates, Coordinates> move) {
        if (currentDepth == 0) return board.getCurrentValue() * valueIndicator;
        else {
            PriorityQueue<Double> values = new PriorityQueue<>();
            //enemy's turn
            if (whoseTurn == enemyColour) {
                //if it's the last move of enemy's turn
                if (board.movePiece(board.getGameboard()[move.getKey().getX()][move.getKey().getY()], move.getValue().getX(), move.getValue().getY())) {
                    //checking all moves of AI
                    List<Pair<Coordinates, Coordinates>> pMoves = board.getPossibleMoves(colour);
                    for (int i = 0; i < pMoves.size() - 1; i++) {
                        Board clonedBoard = board.cloneBoard();
                        values.add(findMinMoveValue(clonedBoard, currentDepth - 1, colour, pMoves.get(i)));
                    }
                    //for the last move there is no need for cloning another board
                    values.add(findMinMoveValue(board, currentDepth - 1, colour, pMoves.get(pMoves.size() - 1)));
                } else {
                    //checking all moves of AI's enemy
                    List<Pair<Coordinates, Coordinates>> pMoves = board.getPossibleMoves(enemyColour);
                    for (int i = 0; i < pMoves.size() - 1; i++) {
                        Board clonedBoard = board.cloneBoard();
                        values.add(findMinMoveValue(clonedBoard, currentDepth, enemyColour, pMoves.get(i)));
                    }
                    //for the last move there is no need for cloning another board
                    values.add(findMinMoveValue(board, currentDepth, enemyColour, pMoves.get(pMoves.size() - 1)));
                }
                //AI's turn
            } else {
                //if it's the last move of AI's turn
                if (board.movePiece(board.getGameboard()[move.getKey().getX()][move.getKey().getY()], move.getValue().getX(), move.getValue().getY())) {
                    //checking all moves of AI's enemy
                    List<Pair<Coordinates, Coordinates>> pMoves = board.getPossibleMoves(enemyColour);
                    for (int i = 0; i < pMoves.size() - 1; i++) {
                        Board clonedBoard = board.cloneBoard();
                        values.add(findMinMoveValue(clonedBoard, currentDepth, enemyColour, pMoves.get(i)));
                    }
                    //for the last move there is no need for cloning another board
                    values.add(findMinMoveValue(board, currentDepth, enemyColour, pMoves.get(pMoves.size() - 1)));
                } else {
                    //checking all moves of AI
                    List<Pair<Coordinates, Coordinates>> pMoves = board.getPossibleMoves(colour);
                    for (int i = 0; i < pMoves.size() - 1; i++) {
                        Board clonedBoard = board.cloneBoard();
                        values.add(findMinMoveValue(clonedBoard, currentDepth, colour, pMoves.get(i)));
                    }
                    //for the last move there is no need for cloning another board
                    values.add(findMinMoveValue(board, currentDepth, colour, pMoves.get(pMoves.size() - 1)));
                }
            }
            return values.isEmpty() ? -500.0 * valueIndicator : values.poll();
        }
    }


}
