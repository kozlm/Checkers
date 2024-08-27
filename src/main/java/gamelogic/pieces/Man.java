package gamelogic.pieces;

public class Man extends Piece{


    public Man(Colour colour, Coordinates cords) {
        super(colour, cords);
    }

    @Override
    public Piece clonePiece() {
        Man clonedMan = new Man(getColour(),new Coordinates(getCords().getX(), getCords().getY()));
        if (isTransparent()) clonedMan.setTransparent(true);
        return clonedMan;
    }

    @Override
    public String toString() {
        if (getColour()==Colour.WHITE) return "whiteMan";
        else return "blackMan";
    }
}
