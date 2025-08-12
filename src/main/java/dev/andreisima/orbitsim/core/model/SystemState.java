package dev.andreisima.orbitsim.core.model;

import dev.andreisima.orbitsim.core.util.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the current collection of bodies in the simulation.
 */
public class SystemState {
    private final List<Body> bodies = new ArrayList<>();

    public List<Body> getBodies() {
        return bodies;
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    /** Computes the barycenter (center of mass) of the system. */
    public Vector2D computeBarycenter() {
        Vector2D sum = new Vector2D();
        double totalMass = 0;
        for (Body b : bodies) {
            sum.add(Vector2D.scale(b.getPosition(), b.getMass()));
            totalMass += b.getMass();
        }
        if (totalMass == 0) return new Vector2D();
        return sum.scale(1.0 / totalMass);
    }
}