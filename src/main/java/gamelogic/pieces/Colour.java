package gamelogic.pieces;

public enum Colour {
    BLACK,
    WHITE;

    @Override
    public String toString() {
        return this == BLACK ? "B" : "W";
    }

    public Colour negate(){
        if (this == BLACK) return WHITE;
        else return BLACK;
    }
}
