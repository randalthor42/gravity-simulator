package dev.andreisima.orbitsim.core.physics;

import dev.andreisima.orbitsim.core.model.Body;

import java.util.List;

/**
 * Interface for numerical integrators that advance the state of all bodies.
 */
public interface Integrator {
    void step(List<Body> bodies, double dt);
}
