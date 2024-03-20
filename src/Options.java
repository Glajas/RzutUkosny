import java.util.HashMap;
import java.util.Map;

public class Options {
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private double airResistance;
    private boolean isUpgradedEulersMethod;
    private double deltaT;
    private Map<String, String> changesLog;

    public Options() {
        this.x = 0;
        this.y = 1.0;
        this.velocityX = 2*PhysicsEngine.GRAVITY;
        this.velocityY = PhysicsEngine.GRAVITY;
        this.airResistance = 0.43;
        this.deltaT = 0.1;
        this.isUpgradedEulersMethod = false;
        this.changesLog = new HashMap<>();
    }

    void logChange(String variableName, String format, Object... args) {
        String formattedString = String.format(format, args);
        changesLog.put(variableName, formattedString);
    }

    public void setX(double x) {
        this.x = x;
        logChange("x", "Changing option X position to: %.1f (m)%n", x);
    }

    public void setY(double y) {
        this.y = y;
        logChange("y", "Changing option Y position to: %.1f (m)%n", y);
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
        logChange("velocityX", "Changing option VelocityX to: %.2f (m/s)%n", velocityX);
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
        logChange("velocityY", "Changing option VelocityY to: %.2f (m/s)%n", velocityY);
    }

    public void setAirResistance(double airResistance) {
        this.airResistance = airResistance;
        logChange("airResistance", "Changing option Air Resistance to: %.2f%n", airResistance);
    }

    public void setUpgradedEulersMethod(boolean isUpgradedEulersMethod) {
        this.isUpgradedEulersMethod = isUpgradedEulersMethod;
        logChange("upgradedEulersMethod", "Setting Upgraded Euler's Method to: %s%n", isUpgradedEulersMethod ? "enabled" : "disabled");
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getResistance() {
        return airResistance;
    }

    public boolean isUpgradedEulersMethod() {
        return isUpgradedEulersMethod;
    }

    public void printChangesLog() {
        System.out.println("Changes log:");
        changesLog.values().forEach(System.out::print);
    }

    public void clearChangesLog() {
        changesLog.clear();
    }

    public double getDeltaT() {
        return deltaT;
    }

    public void setDeltaT(double deltaT) {
        this.deltaT = deltaT;
        logChange("deltaT", "Changing option Delta t to: %.2f%n", deltaT);
    }
}
