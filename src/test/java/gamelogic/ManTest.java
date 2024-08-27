package gamelogic;

import gamelogic.pieces.Colour;
import gamelogic.pieces.Coordinates;
import gamelogic.pieces.Man;
import gamelogic.pieces.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManTest {
    Man man;
    gamelogic.pieces.Coordinates startingCoordinates,Coordinates;
    @BeforeEach
    public void setup(){
        startingCoordinates = new Coordinates(2,0);
        Coordinates = new Coordinates(2,0);
        man = new Man(Colour.WHITE,Coordinates);

    }

    @Test
    public void moveTest(){
        assertEquals(man.getCords(), startingCoordinates);
        man.move(3,1);
        assertSame(man.getCords(), Coordinates);
        assertNotEquals(man.getCords(),startingCoordinates);
        assertEquals(man.getCords().getX(),3);
        assertEquals(man.getCords().getY(),1);
    }
    @Test
    public void cloneTest(){
        Piece clonedMan = man.clonePiece();
        assertTrue(clonedMan instanceof Man);
        assertEquals(man.getColour(),clonedMan.getColour());
        assertEquals(man.getCords(),clonedMan.getCords());
        assertNotSame(man.getCords(),clonedMan.getCords());
        assertNotSame(man,clonedMan);
    }

}