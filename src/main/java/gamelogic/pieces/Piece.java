package gamelogic.pieces;

public abstract class Piece {
    public Piece(Colour colour, Coordinates cords) {
        this.colour = colour;
        this.cords = cords;
        isTransparent = false;
    }

    private Colour colour;

    public boolean isTransparent() {
        return isTransparent;
    }

    public void setTransparent(boolean transparent) {
        isTransparent = transparent;
    }

    private boolean isTransparent;


    public Coordinates getCords() {
        return new Coordinates(cords.getX(), cords.getY());
    }

    private Coordinates cords;

    public void move(int x, int y) {
        cords.setX(x);
        cords.setY(y);
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public abstract Piece clonePiece();

}
