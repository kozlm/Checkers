package gamelogic;

import gamelogic.pieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    Board board;
    King wKing, bKing;
    Man wMan, bMan, bMan1;

    public void addPiece(Board board, Piece piece) {
        board.getGameboard()[piece.getCords().getX()][piece.getCords().getY()] = piece;
        if (piece.getColour() == Colour.WHITE) board.getWhitePieces().add(piece);
        else board.getBlackPieces().add(piece);
    }

    @BeforeEach
    public void setup() {
        board = new Board(true);

        wKing = new King(Colour.WHITE, new Coordinates(0, 0));
        wMan = new Man(Colour.WHITE, new Coordinates(9, 9));
        bKing = new King(Colour.BLACK, new Coordinates(2, 6));
        bMan = new Man(Colour.BLACK, new Coordinates(2, 2));
        bMan1 = new Man(Colour.BLACK, new Coordinates(8, 8));
        addPiece(board, wKing);
        addPiece(board, wMan);
        addPiece(board, bMan);
        addPiece(board, bMan1);
        addPiece(board, bKing);
        addPiece(board, new Man(Colour.WHITE, new Coordinates(3, 5)));
        addPiece(board, new Man(Colour.WHITE, new Coordinates(1, 7)));
    }

    @Test
    void movePiece() {
        IllegalArgumentException mansMove = assertThrows(IllegalArgumentException.class, () -> {
            board.movePiece(bMan, 5, 5);
        });
        assertEquals("Illegal move of a man piece!", mansMove.getMessage());
        IllegalArgumentException kingsMove = assertThrows(IllegalArgumentException.class, () -> {
            board.movePiece(bKing, 0, 0);
        });
        assertEquals("Illegal move of a piece!", kingsMove.getMessage());
        assertEquals(board.getBlackPieces().toString(), "[blackMan, blackMan, blackKing]");
        board.movePiece(wKing, 4, 4);
        assertEquals(board.getBlackPieces().toString(), "[blackMan, blackKing]");
        assertNull(board.getGameboard()[2][2]);
        assertEquals(board.getGameboard()[4][4], wKing);
        board.movePiece(wMan, 7, 7);
        assertEquals(board.getBlackPieces().toString(), "[blackKing]");
        assertNull(board.getGameboard()[8][8]);
        assertEquals(board.getGameboard()[7][7], wMan);
        board.movePiece(wMan, 6, 8);
        assertNull(board.getGameboard()[7][7]);
        assertEquals(board.getGameboard()[6][8], wMan);
    }

    @Test
    void getPossibleMoves() {
        addPiece(board, new Man(Colour.WHITE, new Coordinates(5, 3)));
        addPiece(board, new Man(Colour.WHITE, new Coordinates(3, 1)));
        addPiece(board, new Man(Colour.WHITE, new Coordinates(7, 5)));
        Man bManTest = new Man(Colour.BLACK, new Coordinates(6, 4));
        addPiece(board, bManTest);

        assertEquals(board.getPossibleMoves(bManTest).toString(),"[4x2]");
        assertEquals(board.getPossibleMoves(bKing).toString(),"[4x4]");

        addPiece(board, new Man(Colour.WHITE, new Coordinates(1, 5)));
        addPiece(board, new Man(Colour.WHITE, new Coordinates(3, 5)));
        addPiece(board, new Man(Colour.WHITE, new Coordinates(5, 5)));

        assertEquals(board.getPossibleMoves(bManTest).toString(),"[4x6]");
    }


    @Test
    void getAvailableCaptures() {
        assertEquals(board.getAvailableCaptures(wKing).toString(), "[3x3, 4x4, 5x5, 6x6, 7x7]");

        assertEquals(board.getAvailableCaptures(bKing).toString(), "[0x8, 4x4, 5x3, 6x2, 7x1, 8x0]");
        assertEquals(board.getAvailableCaptures(bMan1).toString(), "[]");
    }

    @Test
    void isCaptureAvailable() {
        assertTrue(board.isCaptureAvailable(bKing));
        assertFalse(board.isCaptureAvailable(bMan1));
        assertFalse(board.isCaptureAvailable(bMan));
        assertFalse(board.isCaptureAvailable(board.getGameboard()[1][7]));
        addPiece(board, new Man(Colour.BLACK, new Coordinates(1, 1)));
        assertFalse(board.isCaptureAvailable(wKing));
        assertTrue(board.isCaptureAvailable(Colour.WHITE));
        assertTrue(board.isCaptureAvailable(Colour.BLACK));
        addPiece(board, new Man(Colour.BLACK, new Coordinates(7, 7)));
        assertFalse(board.isCaptureAvailable(Colour.WHITE));
    }
}