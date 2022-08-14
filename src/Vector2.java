public class Vector2 {
    double x;
    double y;

    public Vector2(double a, double b) {
        x = a;
        y = b;
    }

    public Vector2 multiply(double s) {
        return new Vector2(this.x * s, this.y * s);
    }
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
    public Vector2Int toVector2Int() {
        return new Vector2Int((int)x, (int)y);
    }
}
