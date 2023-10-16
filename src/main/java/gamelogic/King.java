package gamelogic;

public class King extends Piece{


    public King(Colour colour, Coordinates cords) {
        super(colour, cords);
    }

    @Override
    public Piece clonePiece() {
        return (new King(getColour(),new Coordinates(getCords().getX(), getCords().getY())));
    }
}
