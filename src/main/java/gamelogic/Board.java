package gamelogic;

import java.util.ArrayList;
import java.util.List;

public class Board {

    public Board() {
        gameboard = new Piece[10][10];
        blackPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        initializeBoard();
    }

    //private boolean isPositionOccupied(int x, int y) {
    //    return gameboard[x][y] != null;
    //}

    private void initializeBoard() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 10; j++) {
                if ((i + j) % 2 == 0) {
                    gameboard[j][i] = new Man(Colour.WHITE, new Coordinates(j, i));
                    whitePieces.add(gameboard[j][i]);
                } else gameboard[j][i] = null;
            }
        }

        for (int i = 4; i < 6; i++) {
            for (int j = 0; j < 10; j++) gameboard[j][i] = null;
        }

        for (int i = 6; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if ((i + j) % 2 == 0) {
                    gameboard[j][i] = new Man(Colour.BLACK, new Coordinates(j, i));
                    blackPieces.add(gameboard[j][i]);
                } else gameboard[j][i] = null;
            }
        }
    }

    private Piece[][] gameboard;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;


    public List<Piece> getAvailableCaptures(Colour colour) {
        List<Piece> availableCaptures = new ArrayList<>();
        if (colour == Colour.WHITE) {
            for (Piece piece : whitePieces) {
                if (isCaptureAvailable(piece)) availableCaptures.add(piece);
            }
        }
        if (colour == Colour.BLACK) {
            for (Piece piece : blackPieces) {
                if (isCaptureAvailable(piece)) availableCaptures.add(piece);
            }
        }
        return availableCaptures;
    }

    public boolean isCaptureAvailable(Piece piece) {

        if (piece instanceof King) return isKingsCaptureAvailable((King) piece);
        else return isMansCaptureAvailable((Man) piece);

    }

    private boolean isKingsCaptureAvailable(King king) {
        return (isKingsCaptureAvailableOnDiagonal(king, 1, 1) || isKingsCaptureAvailableOnDiagonal(king, 1, -1)
                || isKingsCaptureAvailableOnDiagonal(king, -1, 1) || isKingsCaptureAvailableOnDiagonal(king, -1, -1));
    }

    private boolean isKingsCaptureAvailableOnDiagonal(King king, int xIncrement, int yIncrement) {
        int x = king.getCords().getX() + xIncrement;
        int y = king.getCords().getY() + yIncrement;
        Colour colour = king.getColour();
        boolean isEnemyPieceOnDiagonal = false;

        while (x >= 0 && x < 10 && y >= 0 && y < 10) {
            if (gameboard[x][y] != null && gameboard[x][y].getColour() == colour)
                return false;
            if (gameboard[x][y] != null && gameboard[x][y].getColour() != colour)
                isEnemyPieceOnDiagonal = true;
            if (gameboard[x][y] == null && isEnemyPieceOnDiagonal)
                return true;
            x += xIncrement;
            y += yIncrement;
        }
        return false;
    }

    private List<Coordinates> getKingsCaptures(King king) {
        List<Coordinates> possibleCapturesList = new ArrayList<>();
        possibleCapturesList.addAll(getKingsCapturesOnDiagonal(king, 1, 1));
        possibleCapturesList.addAll(getKingsCapturesOnDiagonal(king, -1, -1));
        possibleCapturesList.addAll(getKingsCapturesOnDiagonal(king, -1, 1));
        possibleCapturesList.addAll(getKingsCapturesOnDiagonal(king, 1, -1));
        return possibleCapturesList;
    }

    private List<Coordinates> getKingsCapturesOnDiagonal(King king, int xIncrement, int yIncrement) {
        int x = king.getCords().getX() + xIncrement;
        int y = king.getCords().getY() + yIncrement;
        Colour colour = king.getColour();
        List<Coordinates> possibleCapturesList = new ArrayList<>();
        boolean isEnemyPieceOnDiagonal = false;

        while (x >= 0 && x < 10 && y >= 0 && y < 10) {
            if (gameboard[x][y] != null && gameboard[x][y].getColour() == colour)
                break;
            if (gameboard[x][y] != null && gameboard[x][y].getColour() != colour)
                isEnemyPieceOnDiagonal = true;
            if (gameboard[x][y] == null && isEnemyPieceOnDiagonal)
                possibleCapturesList.add(new Coordinates(x, y));
            x += xIncrement;
            y += yIncrement;
        }
        return possibleCapturesList;
    }

    private boolean isMansCaptureAvailable(Man man) {
        int x = man.getCords().getX();
        int y = man.getCords().getY();
        Colour colour = man.getColour();
        //checking possible jumps

        //diagonal + +
        if (x < 8 && y < 8 && gameboard[x + 1][y + 1] != null) {
            return gameboard[x + 1][y + 1].getColour() != colour && gameboard[x + 2][y + 2] == null;
        }
        //diagonal - -
        if (x > 1 && y > 1 && gameboard[x - 1][y - 1] != null) {
            return gameboard[x - 1][y - 1].getColour() != colour && gameboard[x - 2][y - 2] == null;
        }
        //diagonal + -
        if (x < 8 && y > 1 && gameboard[x + 1][y - 1] != null) {
            return gameboard[x + 1][y - 1].getColour() != colour && gameboard[x + 2][y - 2] == null;
        }
        //diagonal - +
        if (x > 1 && y < 8 && gameboard[x - 1][y + 1] != null) {
            return gameboard[x - 1][y + 1].getColour() != colour && gameboard[x - 2][y + 2] == null;
        }

        return false;
    }


    private List<Coordinates> getMansCaptures(Man man) {
        int x = man.getCords().getX();
        int y = man.getCords().getY();
        Colour colour = man.getColour();
        List<Coordinates> possibleCapturesList = new ArrayList<>();
        //checking possible jumps

        //diagonal + +
        if (x < 8 && y < 8 && gameboard[x + 1][y + 1] != null) {
            if (gameboard[x + 1][y + 1].getColour() != colour && gameboard[x + 2][y + 2] == null)
                possibleCapturesList.add(new Coordinates(x + 2, y + 2));
        }
        //diagonal - -
        if (x > 1 && y > 1 && gameboard[x - 1][y - 1] != null) {
            if (gameboard[x - 1][y - 1].getColour() != colour && gameboard[x - 2][y - 2] == null)
                possibleCapturesList.add(new Coordinates(x - 2, y - 2));
        }
        //diagonal + -
        if (x < 8 && y > 1 && gameboard[x + 1][y - 1] != null) {
            if (gameboard[x + 1][y - 1].getColour() != colour && gameboard[x + 2][y - 2] == null)
                possibleCapturesList.add(new Coordinates(x + 2, y - 2));
        }
        //diagonal - +
        if (x > 1 && y < 8 && gameboard[x - 1][y + 1] != null) {
            if (gameboard[x - 1][y + 1].getColour() != colour && gameboard[x - 2][y + 2] == null)
                possibleCapturesList.add(new Coordinates(x - 2, y + 2));
        }

        return possibleCapturesList;
    }
}
