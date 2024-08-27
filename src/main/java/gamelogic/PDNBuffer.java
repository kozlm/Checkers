package gamelogic;

import gamelogic.pieces.Colour;

import java.util.ArrayList;

public class PDNBuffer {
    ArrayList<String> whiteMoves, blackMoves;
    ArrayList<Boolean> isWhiteMoveCapture, isBlackMoveCapture;
    Colour currentColour;
    int currentTurn;

    public PDNBuffer() {
        this.currentTurn = 0;
        this.currentColour = Colour.WHITE;
        this.whiteMoves = new ArrayList<>();
        this.blackMoves = new ArrayList<>();
        this.isWhiteMoveCapture = new ArrayList<>();
        this.isBlackMoveCapture = new ArrayList<>();
    }

    public String getCurrentMove() {
        if (currentColour == Colour.WHITE) return whiteMoves.get(currentTurn);
        else return blackMoves.get(currentTurn);
    }

    public String getPreviousMove() {
        if (currentColour == Colour.WHITE && currentTurn == 0)
            throw new ArrayIndexOutOfBoundsException("No previous move for move No 1.");
        else if (currentColour == Colour.WHITE) return blackMoves.get(currentTurn - 1);
        else return whiteMoves.get(currentTurn);
    }

    private void nextMove() {
        if (currentColour == Colour.BLACK) currentTurn++;
        currentColour = currentColour.negate();
    }

    public void addMove(String origin, String destination, boolean isCapture, boolean isFinal) {
        char moveSign = isCapture ? 'x' : '-';
        ArrayList<String> moves = currentColour == Colour.WHITE ? whiteMoves : blackMoves;
        ArrayList<Boolean> isMoveCapture = currentColour == Colour.WHITE ? isWhiteMoveCapture : isBlackMoveCapture;

        if (moves.size() < currentTurn + 1) isMoveCapture.add(isCapture);
        else {
            String[] parts = moves.remove(currentTurn).substring(0, 2).split("[-x]");
            origin = parts[0];
        }

        moves.add(origin + moveSign + destination);
        if (isFinal) nextMove();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= currentTurn; i++) {
            if (whiteMoves.size() >= i + 1) result.append(i + 1).append(". ").append(whiteMoves.get(i));
            if (blackMoves.size() >= i + 1) result.append(" ").append(blackMoves.get(i)).append(" ");
        }
        return result.toString();
    }
}
