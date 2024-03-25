package gamelogic;

public enum Colour {
    BLACK,
    WHITE;

    @Override
    public String toString() {
        return this == BLACK ? "B" : "W";
    }
}
