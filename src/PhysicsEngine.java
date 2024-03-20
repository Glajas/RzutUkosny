import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    public static final double GRAVITY = 9.807;
    public Options options;

    public PhysicsEngine() {
        this.options = new Options();
    }

    public List<Point2D.Double> simulateProjectile(Options options) {
        double posX = options.getX();
        double posY = options.getY();
        double velX = options.getVelocityX();
        double velY = options.getVelocityY();
        double airResistance = options.getResistance();
        boolean useImprovedEuler = options.isUpgradedEulersMethod();
        double deltaT = options.getDeltaT();
        List<Point2D.Double> trajectory = new ArrayList<>();

        while (posY >= 0) {
            trajectory.add(new Point2D.Double(posX, posY));
            double accX = -airResistance * velX;
            double accY = -GRAVITY - airResistance * velY;

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
