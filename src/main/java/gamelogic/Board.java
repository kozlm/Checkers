package gamelogic;

import javafx.util.Pair;

import java.security.Key;
import java.util.*;

public class Board {

    public Board() {
        gameboard = new Piece[10][10];
        blackPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        initializeBoard();
    }

    public Board(boolean emptyIndicator) {
        gameboard = new Piece[10][10];
        blackPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        if (!emptyIndicator) initializeBoard();
    }

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

    public void movePiece(Piece piece, int xDestination, int yDestination) {
        int xCurrent = piece.getCords().getX();
        int yCurrent = piece.getCords().getY();
        Piece capturedPiece = null;
        if (piece instanceof Man) {
            if (Math.abs(xDestination - xCurrent) == 2) {
                capturedPiece = gameboard[xCurrent + (xDestination - xCurrent) / 2][yCurrent + (yDestination - yCurrent) / 2];
                gameboard[xCurrent + (xDestination - xCurrent) / 2][yCurrent + (yDestination - yCurrent) / 2] = null;
            }
        } else {
            int xIncrement = (xDestination - xCurrent) / Math.abs(xDestination - xCurrent);
            int yIncrement = (yDestination - yCurrent) / Math.abs(yDestination - yCurrent);
            int x = xCurrent + xIncrement;
            int y = yCurrent + yIncrement;
            while (x != xDestination && y != yDestination) {
                if (gameboard[x][y] != null) {
                    capturedPiece = gameboard[x][y];
                    gameboard[x][y] = null;
                    break;
                }
                x += xIncrement;
                y += yIncrement;
            }
        }
        if (capturedPiece != null) {
            whitePieces.remove(capturedPiece);
            blackPieces.remove(capturedPiece);
        }
        piece.move(xDestination, yDestination);
        gameboard[xDestination][yDestination] = piece;
    }

    public List<Coordinates> getAllPossibleMoves(Piece piece) {
        if (isCaptureAvailable(piece)) return verifyCaptures(piece,getAvailableCaptures(piece));
        else if (isCaptureAvailable(piece.getColour())) return null;
        else return getAvailableRegularMoves(piece);
    }

    public List<Coordinates> getAvailableRegularMoves(Piece piece) {
        if (piece instanceof King) return getKingsMoves((King) piece);
        else {
            List<Coordinates> regularMovesList = new ArrayList<>();
            int direction = piece.getColour() == Colour.WHITE ? 1 : -1;
            int x = piece.getCords().getX();
            int y = piece.getCords().getY();
            if (gameboard[x + 1][y + direction] == null) regularMovesList.add(new Coordinates(x + 1, y + direction));
            if (gameboard[x - 1][y + direction] == null) regularMovesList.add(new Coordinates(x - 1, y + direction));
            return regularMovesList;
        }
    }


    public List<Coordinates> getAvailableCaptures(Piece piece) {
        if (piece instanceof King) return getKingsMoves((King) piece);
        else return getMansCaptures((Man) piece);
    }

    public boolean isCaptureAvailable(Colour colour) {
        if (colour == Colour.WHITE) {
            for (Piece piece : whitePieces) {
                if (isCaptureAvailable(piece)) return true;
            }
        }
        if (colour == Colour.BLACK) {
            for (Piece piece : blackPieces) {
                if (isCaptureAvailable(piece)) return true;
            }
        }
        return false;
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

    private List<Coordinates> getKingsMoves(King king) {
        List<Coordinates> possibleCapturesList = new ArrayList<>();
        possibleCapturesList.addAll(getKingsMovesOnDiagonal(king, 1, 1));
        possibleCapturesList.addAll(getKingsMovesOnDiagonal(king, -1, -1));
        possibleCapturesList.addAll(getKingsMovesOnDiagonal(king, -1, 1));
        possibleCapturesList.addAll(getKingsMovesOnDiagonal(king, 1, -1));
        return possibleCapturesList;
    }

    private List<Coordinates> getKingsMovesOnDiagonal(King king, int xIncrement, int yIncrement) {
        int x = king.getCords().getX() + xIncrement;
        int y = king.getCords().getY() + yIncrement;
        Colour colour = king.getColour();
        List<Coordinates> possibleMovesList = new ArrayList<>();
        while (x >= 0 && x < 10 && y >= 0 && y < 10) {
            if (gameboard[x][y] != null && gameboard[x][y].getColour() == colour)
                break;
            if (gameboard[x][y] != null && gameboard[x][y].getColour() != colour) {
                possibleMovesList.clear();
            }
            if (gameboard[x][y] == null)
                possibleMovesList.add(new Coordinates(x, y));
            x += xIncrement;
            y += yIncrement;
        }
        return possibleMovesList;
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

    private List<Coordinates> verifyCaptures(Piece piece, List<Coordinates> allCaptures) {
        List<Coordinates> verifiedCaptures = new ArrayList<>();
        PriorityQueue<Pair<Integer, Coordinates>> valuedCaptures = new PriorityQueue<>();
        for (Coordinates capture : allCaptures) {
            Board clonedBoard = cloneBoard();
            Piece clonedPiece = clonedBoard.gameboard[piece.getCords().getX()][piece.getCords().getY()];
            valuedCaptures.add(new Pair<>(valueCapture(clonedBoard, clonedPiece, capture), capture));
        }
        if (valuedCaptures.isEmpty()) return null;
        int highestValue = valuedCaptures.peek().getKey();
        for (Pair<Integer,Coordinates> pair:valuedCaptures){
            if (pair.getKey()==highestValue) verifiedCaptures.add(pair.getValue());
            else break;
        }
        return verifiedCaptures;
    }

    private int valueCapture(Board board, Piece piece, Coordinates capture) {
        board.movePiece(piece, capture.getX(), capture.getY());
        if (!isCaptureAvailable(piece)) return 1;

        else {
            List<Coordinates> capturesList = board.getAvailableCaptures(piece);
            int maxValue = 0;
            for (Coordinates captureFromList : capturesList) {
                Board clonedBoard = cloneBoard();
                Piece clonedPiece = clonedBoard.gameboard[piece.getCords().getX()][piece.getCords().getY()];
                int captureFromListsValue = valueCapture(clonedBoard, clonedPiece, captureFromList) + 1;
                if (captureFromListsValue > maxValue) maxValue = captureFromListsValue;
            }
            return maxValue;
        }
    }

    private Board cloneBoard() {
        Board clonedBoard = new Board(true);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (gameboard[i][j] == null) clonedBoard.gameboard[i][j] = null;
                else {
                    clonedBoard.gameboard[i][j] = gameboard[i][j].clonePiece();
                    if (clonedBoard.gameboard[i][j].getColour() == Colour.WHITE)
                        clonedBoard.whitePieces.add(clonedBoard.gameboard[i][j]);
                    else clonedBoard.blackPieces.add(clonedBoard.gameboard[i][j]);
                }
            }
        }
        return clonedBoard;
    }
}
