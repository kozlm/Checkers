package gamelogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class CheckersAI {
    private final int depth;
    private final Board board;
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
        double alpha = Double.NEGATIVE_INFINITY, beta = Double.POSITIVE_INFINITY;
        List<Pair<Coordinates, Coordinates>> possibleMoves = board.getPossibleMoves(colour);
        Collections.shuffle(possibleMoves);
        Pair<Coordinates, Coordinates> bestMove = null;
        double bestValue = colour == Colour.WHITE ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        for (Pair<Coordinates, Coordinates> move : possibleMoves) {
            Board clonedBoard = board.cloneBoard();
            Colour nextMove = enemyColour;
            int depth = this.depth;
            if (!clonedBoard.movePiece
                    (clonedBoard.getGameboard()[move.getKey().getX()][move.getKey().getY()], move.getValue().getX(), move.getValue().getY())) {
                nextMove = colour;
                depth = depth + 1;
            }
            double evaluation = minimaxFunction(clonedBoard, depth, alpha, beta, nextMove);
            if (colour == Colour.WHITE) {
                alpha = Double.max(evaluation, alpha);
                if (evaluation > bestValue) {
                    bestValue = evaluation;
                    bestMove = move;
                }
            }
            else {
                beta = Double.min(evaluation, beta);
                if (evaluation < bestValue) {
                    bestValue = evaluation;
                    bestMove = move;
                }
            }
        }
        if (bestMove == null) throw new IllegalArgumentException("No moves available for given parameters!");
        return bestMove;
    }

    public double minimaxFunction(Board position, int currentDepth, double alpha, double beta, Colour whoseMove) {
         if (currentDepth == 0 || position.isOver()) return position.getCurrentValue();
        else {
            if (whoseMove == Colour.WHITE) {
                double maxEvaluation = Double.NEGATIVE_INFINITY;
                for (Pair<Coordinates, Coordinates> move : position.getPossibleMoves(Colour.WHITE)) {
                    Board clonedBoard = position.cloneBoard();
                    Colour nextMove = Colour.BLACK;
                    int nextDepth = currentDepth - 1;
                    if (!clonedBoard.movePiece(clonedBoard.getGameboard()[move.getKey().getX()][move.getKey().getY()], move.getValue().getX(), move.getValue().getY())) {
                        nextMove = Colour.WHITE;
                        nextDepth = currentDepth;
                    }
                    double evaluation = minimaxFunction(clonedBoard, nextDepth, alpha, beta, nextMove);
                    maxEvaluation = Double.max(maxEvaluation, evaluation);
                    alpha = Double.max(alpha, maxEvaluation);
                    if (alpha >= beta) break;
                }
                return maxEvaluation;
            } else {
                double minEvaluation = Double.POSITIVE_INFINITY;
                for (Pair<Coordinates, Coordinates> move : position.getPossibleMoves(Colour.BLACK)) {
                    Board clonedBoard = position.cloneBoard();
                    Colour nextMove = Colour.WHITE;
                    int nextDepth = currentDepth - 1;
                    if (!clonedBoard.movePiece(clonedBoard.getGameboard()[move.getKey().getX()][move.getKey().getY()], move.getValue().getX(), move.getValue().getY())) {
                        nextMove = Colour.BLACK;
                        nextDepth = currentDepth;
                    }
                    double evaluation = minimaxFunction(clonedBoard, nextDepth, alpha, beta, nextMove);
                    minEvaluation = Double.min(minEvaluation, evaluation);
                    beta = Double.min(beta, minEvaluation);
                    if (alpha >= beta) break;
                }
                return minEvaluation;
            }
        }
    }


}
