package dev.andreisima.orbitsim.core.util;

/**
 * Collection of physical constants used by the simulator.
 */
public final class Constants {
    private Constants() {}

    /** Gravitational constant in m^3 kg^-1 s^-2. */
    public static final double G = 6.67430e-11;
    /** Speed of light in m/s. */
    public static final double C = 299_792_458;
    /** Softening factor (epsilon) to avoid singularities when bodies get too close. */
    public static final double SOFTENING = 1e3; // meters
}