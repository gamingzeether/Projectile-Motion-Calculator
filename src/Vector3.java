public class Vector3 {
    double x;
    double y;
    double z;

    public Vector3(double a, double b, double c) {
        x = a;
        y = b;
        z = c;
    }

    public Vector3 diff(Vector3 other) {
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    public double distance(Vector3 other) {
        double xDiff = this.x - other.x;
        double yDiff = this.y - other.y;
        double zDiff = this.z - other.z;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
