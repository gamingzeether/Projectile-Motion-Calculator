import java.text.DecimalFormat;
import java.util.ArrayList;

public class FiringSolution {
    double azimuth;
    double elevation;
    int chargeSetting;
    double speed;
    double travelTime;
    double distance2D;
    double distance;
    double gravity;

    static double RAD_TO_DEG = 180 / Math.PI;

    public FiringSolution(double a, double e, int c, double s, double t, double d2, double d3, double g) {
        azimuth = a;
        elevation = e;
        chargeSetting = c;
        speed = s;
        travelTime = t;
        distance2D = d2;
        distance = d3;
        gravity = g;
    }

    public double maxHeight() {
        double sin = Math.sin(elevation);
        return (speed * speed * sin * sin) / (2 * gravity);
    }
    public double yAtX(double x) {
        double time = x / (speed * Math.cos(elevation));
        return yAtT(time);
    }
    public double yAtT(double t) {
        return (speed * t * Math.sin(elevation)) - ((gravity * t * t) / 2);
    }
    public String toString() {
        DecimalFormat decimalFormatter = new DecimalFormat("###.##");
        String azimuthRounded = decimalFormatter.format(azimuth * RAD_TO_DEG);
        String elevationRounded = decimalFormatter.format(elevation * RAD_TO_DEG);
        String speedRounded = decimalFormatter.format(speed);
        String travelTimeRounded = decimalFormatter.format(travelTime);

        return String.format("AZ: %1$s°, EL: %2$s°, SPEED: %3$s m/s (%4$s charges), TIME: %5$ss", azimuthRounded, elevationRounded, speedRounded, chargeSetting + 1, travelTimeRounded);
    }
    public static ArrayList<FiringSolution> calculateSolutions (Vector3 launchPos, Vector3 targetPos, double[] chargesSpeeds) {
        ArrayList<FiringSolution> solutions = new ArrayList<>();

        Vector3 targetOffset = targetPos.diff(launchPos);
        double distance = launchPos.distance(targetPos);
        double distance2D = Math.sqrt(targetOffset.x * targetOffset.x + targetOffset.y * targetOffset.y);
        Vector2 targetOffset2D = new Vector2(distance2D, targetOffset.z);
        double azimuth = Math.atan2(targetOffset.x, targetOffset.y);
        if (azimuth < 0) {
            azimuth += 2 * Math.PI;
        }

        for (int i = 0; i < chargesSpeeds.length; i++) {
            // Trajectory formula from https://www.forrestthewoods.com/blog/solving_ballistic_trajectories/
            // t = x / v cos(a)
            // x = v * t * cos(a)
            // y = v * t * sin(a) - (g * t * t) / 2
            double s = chargesSpeeds[i];
            if (s < 0) {
                continue;
            }
            double g = Main.GRAVITY;
            double x = targetOffset2D.x;
            double y = targetOffset2D.y;
            double d = (s * s * s * s) - g * (g * x * x + 2 * s * s * y);
            if (d > 0) {
                double sqrt = Math.sqrt(d);

                // Firing solution 1
                double elev1 = Math.atan((s * s + sqrt) / (g * x));
                double travelTime1 = distance2D / (s * Math.cos(elev1));
                solutions.add(new FiringSolution(azimuth, elev1, i, s, travelTime1, distance2D, distance, g));

                // Firing solution 2
                if (sqrt != 0) {
                    double elev2 = Math.atan((s * s - sqrt) / (g * x));
                    double travelTime2 = distance2D / (s * Math.cos(elev2));
                    solutions.add(new FiringSolution(azimuth, elev2, i, s, travelTime2, distance2D, distance, g));
                }
            }
        }

        return solutions;
    }
}
