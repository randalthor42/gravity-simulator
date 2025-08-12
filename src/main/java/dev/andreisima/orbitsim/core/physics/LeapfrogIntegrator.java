package dev.andreisima.orbitsim.core.physics;

import dev.andreisima.orbitsim.core.model.Body;
import dev.andreisima.orbitsim.core.util.Constants;
import dev.andreisima.orbitsim.core.util.Vector2D;

import java.util.List;

/**
 * Velocity-Verlet (leapfrog) integrator. It offers good energy conservation
 * properties for gravitational n-body problems.
 */
public class LeapfrogIntegrator implements Integrator {

    @Override
    public void step(List<Body> bodies, double dt) {
        // 1. compute accelerations at current positions
        computeAccelerations(bodies);

        // 2. half velocity kick and position drift
        for (Body b : bodies) {
            b.getVelocity().add(Vector2D.scale(b.getAcceleration(), dt / 2.0));
            b.getPosition().add(Vector2D.scale(b.getVelocity(), dt));
        }

        // 3. recompute accelerations at new positions
        computeAccelerations(bodies);

        // 4. complete velocity kick
        for (Body b : bodies) {
            b.getVelocity().add(Vector2D.scale(b.getAcceleration(), dt / 2.0));
            b.updateTrail(200); // keep trails short
        }
    }

    /**
     * Computes gravitational acceleration on each body using Newton's law of
     * gravitation with a small softening term to avoid singularities.
     */
    private void computeAccelerations(List<Body> bodies) {
        // reset accelerations
        for (Body b : bodies) {
            b.getAcceleration().x = 0;
            b.getAcceleration().y = 0;
        }

        int n = bodies.size();
        for (int i = 0; i < n; i++) {
            Body bi = bodies.get(i);
            for (int j = i + 1; j < n; j++) {
                Body bj = bodies.get(j);
                Vector2D r = Vector2D.subtract(bj.getPosition(), bi.getPosition());
                double distSq = r.magnitudeSquared() + Constants.SOFTENING * Constants.SOFTENING;
                double dist = Math.sqrt(distSq);
                double factor = Constants.G / (distSq * dist);
                Vector2D accel = Vector2D.scale(r, factor);

                // a_i += G * m_j * r_ij / |r_ij|^3
                bi.getAcceleration().add(Vector2D.scale(accel, bj.getMass()));
                // a_j -= G * m_i * r_ij / |r_ij|^3
                bj.getAcceleration().add(Vector2D.scale(accel, -bi.getMass()));
            }
        }
    }
}