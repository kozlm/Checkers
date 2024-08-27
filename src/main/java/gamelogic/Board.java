package gamelogic;

import gamelogic.pieces.*;

import java.util.*;

public class Board {

    public Board() {
        this(false);
    }

    public Board(boolean emptyIndicator) {
        gameboard = new Piece[10][10];
        blackPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        pdnBuffer = new PDNBuffer();
        fakeMovesCounter = 0;
        if (!emptyIndicator) initializeBoard();
    }

    private PDNBuffer pdnBuffer;
    private final Piece[][] gameboard;
    private final List<Piece> whitePieces;
    private final List<Piece> blackPieces;
    private int fakeMovesCounter;

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

    public boolean movePiece(Piece piece, int xDestination, int yDestination, boolean shouldRemoveTransparent) {
        int xCurrent = piece.getCords().getX();
        int yCurrent = piece.getCords().getY();
        Piece capturedPiece = null;
        if (xDestination > 9 || xDestination < 0 || yDestination < 0 || yDestination > 9)
            throw new IllegalArgumentException("Illegal values of destination coordinates!");
        if (Math.abs(xDestination - xCurrent) != Math.abs(yDestination - yCurrent))
            throw new IllegalArgumentException("Illegal move of a piece!");
        if (piece instanceof Man) {
            if ((Math.abs(yDestination - yCurrent) != 2 && Math.abs(yDestination - yCurrent) != 1)
                    || (Math.abs(xDestination - xCurrent) != 2 && Math.abs(xDestination - xCurrent) != 1))
                throw new IllegalArgumentException("Illegal move of a man piece!");
            if (Math.abs(xDestination - xCurrent) == 2) {
                capturedPiece = gameboard[xCurrent + (xDestination - xCurrent) / 2][yCurrent + (yDestination - yCurrent) / 2];
            }
            fakeMovesCounter = 0;
        } else {
            int xIncrement = (xDestination - xCurrent) / Math.abs(xDestination - xCurrent);
            int yIncrement = (yDestination - yCurrent) / Math.abs(yDestination - yCurrent);
            int x = xCurrent + xIncrement;
            int y = yCurrent + yIncrement;
            while (x != xDestination && y != yDestination) {
                if (gameboard[x][y] != null) {
                    capturedPiece = gameboard[x][y];
                    break;
                }
                x += xIncrement;
                y += yIncrement;
            }
        }

        if (capturedPiece != null) {
            fakeMovesCounter = 0;
            capturedPiece.setTransparent(true);
        }


        piece.move(xDestination, yDestination);
        gameboard[xDestination][yDestination] = piece;
        gameboard[xCurrent][yCurrent] = null;

        if (capturedPiece != null && isCaptureAvailable(piece)) {
            pdnBuffer.addMove(
                    new Coordinates(xCurrent, yCurrent).getCheckersNotation(),
                    new Coordinates(xDestination, yDestination).getCheckersNotation(),
                    true,
                    false
            );
            return false;
        } else {
            if (shouldRemoveTransparent) {
                Iterator<Piece> iterator;
                iterator = (piece.getColour() == Colour.WHITE) ? blackPieces.iterator() : whitePieces.iterator();
                while (iterator.hasNext()) {
                    Piece p = iterator.next();
                    if (p.isTransparent()) {
                        gameboard[p.getCords().getX()][p.getCords().getY()] = null;
                        iterator.remove();
                    }
                }
            }
            promotePieces(piece.getColour());
            if (piece instanceof King) fakeMovesCounter++;
            pdnBuffer.addMove(
                    new Coordinates(xCurrent, yCurrent).getCheckersNotation(),
                    new Coordinates(xDestination, yDestination).getCheckersNotation(),
                    capturedPiece != null,
                    true
            );
            return true;
        }
    }

    private void promotePieces(Colour colour) {
        int promotionLine = colour == Colour.WHITE ? 9 : 0;
        List<Piece> pieces = colour == Colour.WHITE ? whitePieces : blackPieces;
        for (int i = 0; i < 10; i++) {
            if (gameboard[i][promotionLine] != null && gameboard[i][promotionLine].getColour() == colour
                    && gameboard[i][promotionLine] instanceof Man) {
                pieces.remove(gameboard[i][promotionLine]);
                King king = new King(colour, new Coordinates(i, promotionLine));
                gameboard[i][promotionLine] = king;
                pieces.add(king);
            }
        }
    }

    public boolean movePiece(Piece piece, int xDestination, int yDestination) {
        return movePiece(piece, xDestination, yDestination, true);
    }

    public List<Coordinates> getPossibleMoves(Piece piece) {
        if (!isCaptureAvailable(piece) && isCaptureAvailable(piece.getColour())) return new ArrayList<>();
        if (piece instanceof King) {
            if (isCaptureAvailable(piece)) return verifyCaptures(piece, getKingsMoves((King) piece));
            else return getKingsMoves((King) piece);
        } else {
            if (isCaptureAvailable(piece)) return verifyCaptures(piece, getMansMoves((Man) piece));
            else return getMansMoves((Man) piece);
        }
    }

    public List<Pair<Coordinates, Coordinates>> getPossibleMoves(Colour colour) {
        List<Piece> pieces = colour == Colour.WHITE ? whitePieces : blackPieces;
        List<Pair<Coordinates, Coordinates>> moves = new ArrayList<>();
        for (Piece piece : pieces) {
            Coordinates src = piece.getCords();
            for (Coordinates cords : getPossibleMoves(piece)) {
                moves.add(new Pair<>(src, cords));
            }
        }
        return moves;
    }

    public List<Coordinates> getAvailableCaptures(Piece piece) {
        if (isCaptureAvailable(piece)) {
            if (piece instanceof King) return getKingsMoves((King) piece);
            else return getMansMoves((Man) piece);
        } else return new ArrayList<>();
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
        if (piece instanceof King) {
            return (isKingsCaptureAvailableOnDiagonal((King) piece, 1, 1) || isKingsCaptureAvailableOnDiagonal((King) piece, 1, -1)
                    || isKingsCaptureAvailableOnDiagonal((King) piece, -1, 1) || isKingsCaptureAvailableOnDiagonal((King) piece, -1, -1));
        } else {
            int x = piece.getCords().getX();
            int y = piece.getCords().getY();
            Colour colour = piece.getColour();
            //checking possible jumps

            //diagonal + +
            if (x < 8 && y < 8 && gameboard[x + 1][y + 1] != null) {
                if (gameboard[x + 1][y + 1].getColour() != colour && !gameboard[x + 1][y + 1].isTransparent()
                        && gameboard[x + 2][y + 2] == null)
                    return true;
            }
            //diagonal - -
            if (x > 1 && y > 1 && gameboard[x - 1][y - 1] != null) {
                if (gameboard[x - 1][y - 1].getColour() != colour && !gameboard[x - 1][y - 1].isTransparent()
                        && gameboard[x - 2][y - 2] == null)
                    return true;
            }
            //diagonal + -
            if (x < 8 && y > 1 && gameboard[x + 1][y - 1] != null) {
                if (gameboard[x + 1][y - 1].getColour() != colour && !gameboard[x + 1][y - 1].isTransparent()
                        && gameboard[x + 2][y - 2] == null)
                    return true;
            }
            //diagonal - +
            if (x > 1 && y < 8 && gameboard[x - 1][y + 1] != null) {
                if (gameboard[x - 1][y + 1].getColour() != colour && !gameboard[x - 1][y + 1].isTransparent()
                        && gameboard[x - 2][y + 2] == null)
                    return true;
            }

            return false;

        }

    }

    private boolean isKingsCaptureAvailableOnDiagonal(King king, int xIncrement, int yIncrement) {
        int x = king.getCords().getX() + xIncrement;
        int y = king.getCords().getY() + yIncrement;
        Colour colour = king.getColour();
        boolean isEnemyPieceOnDiagonal = false;

        while (x >= 0 && x < 10 && y >= 0 && y < 10) {
            if (gameboard[x][y] != null && gameboard[x][y].getColour() == colour)
                return false;
            if (gameboard[x][y] != null && gameboard[x][y].getColour() != colour && isEnemyPieceOnDiagonal)
                return false;
            if (gameboard[x][y] != null && gameboard[x][y].getColour() != colour && gameboard[x][y].isTransparent())
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
        boolean isCaptureAvailable = isCaptureAvailable(king);
        List<Coordinates> possibleCapturesList = new ArrayList<>();

        possibleCapturesList.addAll(getKingsMovesOnDiagonal(king, 1, 1, isCaptureAvailable));
        possibleCapturesList.addAll(getKingsMovesOnDiagonal(king, -1, -1, isCaptureAvailable));
        possibleCapturesList.addAll(getKingsMovesOnDiagonal(king, -1, 1, isCaptureAvailable));
        possibleCapturesList.addAll(getKingsMovesOnDiagonal(king, 1, -1, isCaptureAvailable));

        return possibleCapturesList;
    }

    private List<Coordinates> getKingsMovesOnDiagonal(King king, int xIncrement, int yIncrement, boolean isCaptureAvailable) {
        int x = king.getCords().getX() + xIncrement;
        int y = king.getCords().getY() + yIncrement;
        Colour colour = king.getColour();
        List<Coordinates> possibleMovesList = new ArrayList<>();
        boolean gotPiece = false;
        while (x >= 0 && x < 10 && y >= 0 && y < 10) {
            if (gameboard[x][y] != null && gameboard[x][y].getColour() == colour) break;
            if (gameboard[x][y] != null && gameboard[x][y].getColour() != colour && gotPiece) break;
            if (gameboard[x][y] != null && gameboard[x][y].getColour() != colour && gameboard[x][y].isTransparent())
                break;
            if (gameboard[x][y] != null && gameboard[x][y].getColour() != colour && !gotPiece) {
                possibleMovesList.clear();
                gotPiece = true;
            }
            if (gameboard[x][y] == null)
                possibleMovesList.add(new Coordinates(x, y));
            x += xIncrement;
            y += yIncrement;
        }
        if (isCaptureAvailable && !gotPiece) possibleMovesList.clear();
        return possibleMovesList;
    }


    private List<Coordinates> getMansMoves(Man man) {
        List<Coordinates> movesList = new ArrayList<>();
        int x = man.getCords().getX();
        int y = man.getCords().getY();
        Colour colour = man.getColour();
        if (isCaptureAvailable(man)) {
            //checking possible jumps
            //diagonal + +
            if (x < 8 && y < 8 && gameboard[x + 1][y + 1] != null) {
                if (gameboard[x + 1][y + 1].getColour() != colour && !gameboard[x + 1][y + 1].isTransparent()
                        && gameboard[x + 2][y + 2] == null)
                    movesList.add(new Coordinates(x + 2, y + 2));
            }
            //diagonal - -
            if (x > 1 && y > 1 && gameboard[x - 1][y - 1] != null) {
                if (gameboard[x - 1][y - 1].getColour() != colour && !gameboard[x - 1][y - 1].isTransparent()
                        && gameboard[x - 2][y - 2] == null)
                    movesList.add(new Coordinates(x - 2, y - 2));
            }
            //diagonal + -
            if (x < 8 && y > 1 && gameboard[x + 1][y - 1] != null) {
                if (gameboard[x + 1][y - 1].getColour() != colour && !gameboard[x + 1][y - 1].isTransparent()
                        && gameboard[x + 2][y - 2] == null)
                    movesList.add(new Coordinates(x + 2, y - 2));
            }
            //diagonal - +
            if (x > 1 && y < 8 && gameboard[x - 1][y + 1] != null) {
                if (gameboard[x - 1][y + 1].getColour() != colour && !gameboard[x - 1][y + 1].isTransparent()
                        && gameboard[x - 2][y + 2] == null)
                    movesList.add(new Coordinates(x - 2, y + 2));
            }

        } else {
            int direction = man.getColour() == Colour.WHITE ? 1 : -1;
            if (x < 9 && y + direction < 10 && y + direction >= 0 && gameboard[x + 1][y + direction] == null)
                movesList.add(new Coordinates(x + 1, y + direction));
            if (x > 0 && y + direction < 10 && y + direction >= 0 && gameboard[x - 1][y + direction] == null)
                movesList.add(new Coordinates(x - 1, y + direction));

        }
        return movesList;
    }

    private List<Coordinates> verifyCaptures(Piece piece, List<Coordinates> allCaptures) {
        List<Coordinates> verifiedCaptures = new ArrayList<>();
        PriorityQueue<gamelogic.Pair<Integer, Coordinates>> valuedCaptures = new PriorityQueue<>(Collections.reverseOrder());
        for (Coordinates capture : allCaptures) {
            Board clonedBoard = cloneBoard();
            Piece clonedPiece = clonedBoard.gameboard[piece.getCords().getX()][piece.getCords().getY()];
            valuedCaptures.add(new gamelogic.Pair<>(valueCapture(clonedBoard, clonedPiece, capture), capture));
        }
        if (valuedCaptures.isEmpty()) return null;
        int highestValue = valuedCaptures.peek().getKey();
        for (gamelogic.Pair<Integer, Coordinates> pair : valuedCaptures) {
            if (pair.getKey() == highestValue) verifiedCaptures.add(pair.getValue());
            else break;
        }
        return verifiedCaptures;
    }

    private int valueCapture(Board board, Piece piece, Coordinates capture) {
        board.movePiece(piece, capture.getX(), capture.getY(), false);
        if (!board.isCaptureAvailable(piece)) return 1;

        else {
            List<Coordinates> capturesList = board.getAvailableCaptures(piece);
            int maxValue = 0;
            for (Coordinates captureFromList : capturesList) {
                Board clonedBoard = board.cloneBoard();
                Piece clonedPiece = clonedBoard.gameboard[piece.getCords().getX()][piece.getCords().getY()];
                int captureFromListsValue = valueCapture(clonedBoard, clonedPiece, captureFromList) + 1;
                if (captureFromListsValue > maxValue) maxValue = captureFromListsValue;
            }
            return maxValue;
        }
    }

    public String getPDN() {
        return pdnBuffer.toString();
    }

    public Piece[][] getGameboard() {
        return gameboard;
    }

    public List<Piece> getWhitePieces() {
        return whitePieces;
    }

    public List<Piece> getBlackPieces() {
        return blackPieces;
    }

    public double getCurrentValue() {
        return whitePieces.size() - blackPieces.size();
    }

    public int getFakeMovesCounter() {
        return fakeMovesCounter;
    }

    public boolean isOver() {
        return whitePieces.isEmpty() || blackPieces.isEmpty()
                || getPossibleMoves(Colour.WHITE).isEmpty() || getPossibleMoves(Colour.WHITE).isEmpty();
    }

    protected Board cloneBoard() {
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
