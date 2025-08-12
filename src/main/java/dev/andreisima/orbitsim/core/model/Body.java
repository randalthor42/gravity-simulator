package dev.andreisima.orbitsim.core.model;

import dev.andreisima.orbitsim.core.util.Constants;
import dev.andreisima.orbitsim.core.util.Vector2D;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Represents a physical body participating in the simulation.
 */
public class Body {
    private final String name;
    private final BodyType type;
    private double mass;
    private double radius;
    private final Vector2D position;
    private final Vector2D velocity; // meters / second
    private final Vector2D acceleration; // meters / second^2
    private final Deque<Vector2D> trail = new ArrayDeque<>();
    private boolean trailEnabled = true;

    public Body(String name, BodyType type, double mass, double radius, Vector2D position, Vector2D velocity) {
        this.name = name;
        this.type = type;
        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = new Vector2D();
    }

    public String getName() { return name; }
    public BodyType getType() { return type; }
    public double getMass() { return mass; }
    public void setMass(double mass) { this.mass = mass; }
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    public Vector2D getPosition() { return position; }
    public void setVelocity(Vector2D velocity) {
        this.velocity.x = velocity.x;
        this.velocity.y = velocity.y;
    }

    public Vector2D getVelocity() { return velocity; }
    public Vector2D getAcceleration() { return acceleration; }

    public Deque<Vector2D> getTrail() { return trail; }
    public boolean isTrailEnabled() { return trailEnabled; }
    public void setTrailEnabled(boolean trailEnabled) { this.trailEnabled = trailEnabled; }

    /** Adds the current position to the trail, trimming its length. */
    public void updateTrail(int maxLength) {
        if (!trailEnabled) return;
        trail.addLast(position.copy());
        while (trail.size() > maxLength) {
            trail.removeFirst();
        }
    }

    /** Schwarzschild radius for black holes. */
    public double getSchwarzschildRadius() {
        return 2 * Constants.G * mass / (Constants.C * Constants.C);
    }
}