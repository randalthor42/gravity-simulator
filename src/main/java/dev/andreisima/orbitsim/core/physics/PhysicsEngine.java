package dev.andreisima.orbitsim.core.physics;

import dev.andreisima.orbitsim.core.model.Body;
import dev.andreisima.orbitsim.core.model.BodyType;
import dev.andreisima.orbitsim.core.model.SystemState;
import dev.andreisima.orbitsim.core.util.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates integration and collision handling for the system.
 */
public class PhysicsEngine {
    private final Integrator integrator;
    private CollisionMode collisionMode = CollisionMode.MERGE;

    public PhysicsEngine(Integrator integrator) {
        this.integrator = integrator;
    }

    public void setCollisionMode(CollisionMode mode) {
        this.collisionMode = mode;
    }

    public void step(SystemState state, double dt) {
        integrator.step(state.getBodies(), dt);
        handleCollisions(state.getBodies());
    }

    private void handleCollisions(List<Body> bodies) {
        if (collisionMode == CollisionMode.IGNORE && bodies.stream().noneMatch(b -> b.getType() == BodyType.BLACK_HOLE)) {
            return;
        }
        List<Body> toRemove = new ArrayList<>();
        int n = bodies.size();
        for (int i = 0; i < n; i++) {
            Body a = bodies.get(i);
            for (int j = i + 1; j < n; j++) {
                Body b = bodies.get(j);
                double dist = Vector2D.subtract(b.getPosition(), a.getPosition()).magnitude();

                // Black hole accretion
                if (a.getType() == BodyType.BLACK_HOLE) {
                    double rs = a.getSchwarzschildRadius();
                    if (dist < 3 * rs) {
                        accrete(a, b);
                        toRemove.add(b);
                        continue;
                    }
                }
                if (b.getType() == BodyType.BLACK_HOLE) {
                    double rs = b.getSchwarzschildRadius();
                    if (dist < 3 * rs) {
                        accrete(b, a);
                        toRemove.add(a);
                        continue;
                    }
                }

                // other collisions
                if (collisionMode == CollisionMode.MERGE && dist < a.getRadius() + b.getRadius()) {
                    merge(a, b);
                    toRemove.add(b);
                }
            }
        }
        bodies.removeAll(toRemove);
    }

    private void merge(Body a, Body b) {
        // conserve momentum
        Vector2D momentum = Vector2D.add(Vector2D.scale(a.getVelocity(), a.getMass()),
                Vector2D.scale(b.getVelocity(), b.getMass()));
        double newMass = a.getMass() + b.getMass();
        a.setVelocity(Vector2D.scale(momentum, 1.0 / newMass));
        a.setMass(newMass);
        // volume proportional radius: simple sum of radii
        a.setRadius(Math.cbrt(Math.pow(a.getRadius(),3) + Math.pow(b.getRadius(),3)));
    }

    private void accrete(Body blackHole, Body victim) {
        Vector2D momentum = Vector2D.add(Vector2D.scale(blackHole.getVelocity(), blackHole.getMass()),
                Vector2D.scale(victim.getVelocity(), victim.getMass()));
        double newMass = blackHole.getMass() + victim.getMass();
        blackHole.setVelocity(Vector2D.scale(momentum, 1.0 / newMass));
        blackHole.setMass(newMass);
    }
}
