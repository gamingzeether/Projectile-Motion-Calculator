public class Vector2Int {
    int x;
    int y;

    public Vector2Int(int a, int b) {
        x = a;
        y = b;
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }
    public Vector2Int add(Vector2Int other) {
        return new Vector2Int(x + other.x, y + other.y);
    }
    public Vector2Int add(int x, int y) {
        return new Vector2Int(this.x + x, this.y + y);
    }
}
