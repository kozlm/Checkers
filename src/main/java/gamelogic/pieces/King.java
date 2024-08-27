package gamelogic.pieces;

public class King extends Piece{


    public King(Colour colour, Coordinates cords) {
        super(colour, cords);
    }

    @Override
    public Piece clonePiece() {
        King clonedKing = new King(getColour(),new Coordinates(getCords().getX(), getCords().getY()));
        if (isTransparent()) clonedKing.setTransparent(true);
        return clonedKing;
    }

    @Override
    public String toString() {
        if (getColour()==Colour.WHITE) return "whiteKing";
        else return "blackKing";
    }
}
