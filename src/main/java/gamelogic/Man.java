package gamelogic;

public class Man extends Piece{


    public Man(Colour colour, Coordinates cords) {
        super(colour, cords);
    }

    @Override
    public Piece clonePiece() {
        return (new Man(getColour(),new Coordinates(getCords().getX(), getCords().getY())));
    }
}
