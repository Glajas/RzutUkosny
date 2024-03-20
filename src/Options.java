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
        if(x != this.x)
            logChange("x", "Changing option X position to: %.1f (m)%n", x);
        this.x = x;
    }

    public void setY(double y) {
        if(y != this.y)
            logChange("y", "Changing option Y position to: %.1f (m)%n", y);
        this.y = y;
    }

    public void setVelocityX(double velocityX) {
        if(velocityX != this.velocityX)
            logChange("velocityX", "Changing option VelocityX to: %.2f (m/s)%n", velocityX);
        this.velocityX = velocityX;
    }

    public void setVelocityY(double velocityY) {
        if(velocityY != this.velocityY)
            logChange("velocityY", "Changing option VelocityY to: %.2f (m/s)%n", velocityY);
        this.velocityY = velocityY;
    }

    public void setDeltaT(double deltaT) {
        if(deltaT != this.deltaT)
            logChange("deltaT", "Changing option Delta t to: %.2f%n", deltaT);
        this.deltaT = deltaT;
    }

    public void setAirResistance(double airResistance) {
        if(airResistance != this.airResistance)
            logChange("airResistance", "Changing option Air Resistance to: %.2f%n", airResistance);
        this.airResistance = airResistance;
    }

    public void setUpgradedEulersMethod(boolean isUpgradedEulersMethod) {
        if(isUpgradedEulersMethod != this.isUpgradedEulersMethod)
            logChange("upgradedEulersMethod", "Setting Upgraded Euler's Method to: %s%n", isUpgradedEulersMethod ? "enabled" : "disabled");
        this.isUpgradedEulersMethod = isUpgradedEulersMethod;
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
        if(!changesLog.isEmpty()) {
            System.out.println("Changes log:");
            changesLog.values().forEach(System.out::print);
        }
    }

    public void clearChangesLog() {
        changesLog.clear();
    }

    public double getDeltaT() {
        return deltaT;
    }
}
