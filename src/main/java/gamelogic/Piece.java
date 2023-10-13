package gamelogic;

public abstract class Piece {
    public Piece(Colour colour, Coordinates cords) {
        this.colour = colour;
        this.cords = cords;
    }

    private Colour colour;


    public Coordinates getCords() {
        return cords;
    }

    public void setCords(Coordinates cords) {
        this.cords = cords;
    }

    private Coordinates cords;

    public void move(int x, int y){
        cords.setX(x);
        cords.setY(y);
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }


}
