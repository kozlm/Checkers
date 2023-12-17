package gamelogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class CheckersAI {
    private final int depth;
    private Board board;
    private final Colour colour, enemyColour;
    private int number = 0;
    int valueIndicator;

    public CheckersAI(Colour colour, int depth, Board board) {
        this.depth = depth;
        this.colour = colour;
        this.board = board;
        this.enemyColour = colour == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
        this.valueIndicator = colour == Colour.WHITE ? 1 : -1;
    }

    public Pair<Coordinates, Coordinates> findBestMove() {
        number = 0;
        double alpha = Double.NEGATIVE_INFINITY, beta = Double.POSITIVE_INFINITY;
        PriorityQueue<Pair<Double, Pair<Coordinates, Coordinates>>> valuedMoves = new PriorityQueue<>(Collections.reverseOrder());
        for (Pair<Coordinates, Coordinates> move : board.getPossibleMoves(colour)) {
            Board clonedBoard = board.cloneBoard();
            double evaluation = minimaxFunction(clonedBoard, depth, alpha, beta, colour);
            valuedMoves.add(new Pair<>(evaluation * valueIndicator, move));
            if (colour == Colour.WHITE) alpha = Double.max(evaluation, alpha);
            else beta = Double.min(evaluation, beta);
            if (alpha >= beta) break;
        }
        if (valuedMoves.isEmpty()) throw new IllegalArgumentException("No moves available for given parameters!");
        return valuedMoves.peek().getValue();
    }

    public double minimaxFunction(Board position, int currentDepth, double alpha, double beta, Colour whoseMove) {
        System.out.println(number++);
        if (currentDepth == 0 || position.isOver()) return board.getCurrentValue();
        else {
            if (whoseMove == Colour.WHITE) {
                double maxEvaluation = Double.NEGATIVE_INFINITY;
                List<Pair<Coordinates, Coordinates>> pMoves = position.getPossibleMoves(colour);
                for (int i = 0; i < pMoves.size(); i++) {
                    Pair<Coordinates, Coordinates> move = pMoves.get(i);
                    Board clonedPosition = position.cloneBoard();
                    Board nextPosition = i == pMoves.size() - 1 ? position : clonedPosition;
                    Colour nextMove = Colour.BLACK;
                    int nextDepth = currentDepth - 1;
                    if (!nextPosition.movePiece(nextPosition.getGameboard()[move.getKey().getX()][move.getKey().getY()], move.getValue().getX(), move.getValue().getY())) {
                        nextMove = Colour.WHITE;
                        nextDepth = currentDepth;
                    }
                    double evaluation = minimaxFunction(nextPosition, nextDepth, alpha, beta, nextMove);
                    maxEvaluation = Double.max(maxEvaluation, evaluation);
                    alpha = Double.max(alpha, evaluation);
                    if (alpha >= beta) break;
                }
                return maxEvaluation;
            } else {
                double minEvaluation = Double.POSITIVE_INFINITY;
                List<Pair<Coordinates, Coordinates>> pMoves = position.getPossibleMoves(colour);
                for (int i = 0; i < pMoves.size(); i++) {
                    Pair<Coordinates, Coordinates> move = pMoves.get(i);
                    Board clonedPosition = position.cloneBoard();
                    Board nextPosition = i == pMoves.size() - 1 ? position : clonedPosition;
                    Colour nextMove = Colour.WHITE;
                    int nextDepth = currentDepth - 1;
                    if (!nextPosition.movePiece(nextPosition.getGameboard()[move.getKey().getX()][move.getKey().getY()], move.getValue().getX(), move.getValue().getY())) {
                        nextMove = Colour.BLACK;
                        nextDepth = currentDepth;
                    }
                    double evaluation = minimaxFunction(nextPosition, nextDepth, alpha, beta, nextMove);
                    minEvaluation = Double.min(minEvaluation, evaluation);
                    beta = Double.min(beta, evaluation);
                    if (alpha >= beta) break;
                }
                return minEvaluation;
            }
        }
    }


}
