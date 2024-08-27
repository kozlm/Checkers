package gamelogic;

import gamelogic.pieces.Colour;
import gamelogic.pieces.Coordinates;
import gamelogic.pieces.King;
import gamelogic.pieces.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KingTest {
    King king;
    gamelogic.pieces.Coordinates startingCoordinates,Coordinates;
    @BeforeEach
    public void setup(){
        startingCoordinates = new Coordinates(0,0);
        Coordinates = new Coordinates(0,0);
        king = new King(Colour.WHITE,Coordinates);

    }

    @Test
    public void moveTest(){
        assertEquals(king.getCords(), startingCoordinates);
        king.move(5,5);
        assertSame(king.getCords(), Coordinates);
        assertNotEquals(king.getCords(),startingCoordinates);
        assertEquals(king.getCords().getX(),5);
        assertEquals(king.getCords().getY(),5);
    }
    @Test
    public void cloneTest(){
        Piece clonedKing = king.clonePiece();
        assertTrue(clonedKing instanceof King);
        assertEquals(king.getColour(),clonedKing.getColour());
        assertEquals(king.getCords(),clonedKing.getCords());
        assertNotSame(king.getCords(),clonedKing.getCords());
        assertNotSame(king,clonedKing);
    }
}