import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    public static final double GRAVITY = 9.807;
    public Options options;

    public PhysicsEngine() {
        this.options = new Options();
    }

    public List<Point2D.Double> simulateProjectile(double posX, double posY, double velX, double velY, double airResistance, boolean useImprovedEuler, double deltaT) {
        List<Point2D.Double> trajectory = new ArrayList<>();
        final double gravity = 9.807;

        while (posY >= 0) {
            trajectory.add(new Point2D.Double(posX, posY));
            double accX = -airResistance * velX;
            double accY = -gravity - airResistance * velY;

            if (useImprovedEuler) {
                double velXHalfStep = velX + accX * (deltaT / 2);
                double velYHalfStep = velY + accY * (deltaT / 2);
                posX += velXHalfStep * deltaT;
                posY += velYHalfStep * deltaT;
                velX += accX * deltaT;
                velY += accY * deltaT;
            } else {
                posX += velX * deltaT;
                posY += velY * deltaT;
                velX += accX * deltaT;
                velY += accY * deltaT;
            }
            if (posY < 0) {
                break;
            }
        }
        return trajectory;
    }
}
