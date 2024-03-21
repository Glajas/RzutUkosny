package Logic;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {
    public static final double GRAVITY = 9.807;
    public Options options;

    public record TrajectoryPoint(double x, double y, double velocityX, double velocityY, double time) {
    }

    public PhysicsEngine() {
        this.options = new Options();
    }

    public List<TrajectoryPoint> simulateProjectile(Options options) {
        double posX = options.getX();
        double posY = options.getY();
        double velX = options.getVelocityX();
        double velY = options.getVelocityY();
        double airResistance = options.getResistance();
        boolean useImprovedEuler = options.isUpgradedEulersMethod();
        double deltaT = options.getDeltaT();
        List<TrajectoryPoint> trajectory = new ArrayList<>();
        double time = 0.0;

        while (posY >= 0) {
            trajectory.add(new TrajectoryPoint(posX, posY, velX, velY, time));
            double accX = -airResistance * velX;
            double accY = -GRAVITY - airResistance * velY;

            if (useImprovedEuler) {
                double velXHalfStep = velX + accX * (deltaT / 2);
                double velYHalfStep = velY + accY * (deltaT / 2);
                posX += velXHalfStep * deltaT;
                posY += velYHalfStep * deltaT;
            } else {
                posX += velX * deltaT;
                posY += velY * deltaT;
            }
            velX += accX * deltaT;
            velY += accY * deltaT;

            time += deltaT;
        }

        return trajectory;
    }
}
