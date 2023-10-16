package gamelogic;

public class Coordinates {
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int x, y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates) {
            return (((Coordinates) obj).getX() == x && ((Coordinates) obj).getY() == y);
        } else return super.equals(obj);
    }
}
