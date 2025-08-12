package dev.andreisima.orbitsim.core.presets;

import dev.andreisima.orbitsim.core.model.Body;
import dev.andreisima.orbitsim.core.model.BodyType;
import dev.andreisima.orbitsim.core.model.SystemState;
import dev.andreisima.orbitsim.core.util.Constants;
import dev.andreisima.orbitsim.core.util.Vector2D;

public final class PresetFactory {
    private PresetFactory() {}

    /** Sun–Earth–Moon with COM at rest. */
    public static SystemState sunEarthMoon() {
        final double M_SUN   = 1.9885e30;
        final double R_SUN   = 696_340_000;
        final double M_EARTH = 5.972e24;
        final double R_EARTH = 6_371_000;
        final double M_MOON  = 7.342e22;
        final double R_MOON  = 1_737_400;

        final double AU      = 149_597_870_700.0;
        final double R_EM    = 384_400_000.0;

        SystemState state = new SystemState();

        // Positions (m)
        Vector2D pSun   = new Vector2D(0, 0);
        Vector2D pEarth = new Vector2D(AU, 0);
        Vector2D pMoon  = new Vector2D(AU + R_EM, 0);

        // Velocities (m/s): circular v around primary, tangential +y
        double vEarthMag = circSpeed(Constants.G * M_SUN, AU);
        double vMoonRel  = circSpeed(Constants.G * M_EARTH, R_EM);

        Vector2D vSun   = new Vector2D(0, 0);
        Vector2D vEarth = new Vector2D(0,  vEarthMag);
        Vector2D vMoon  = new Vector2D(0,  vEarthMag + vMoonRel); // Earth’s orbital + Moon around Earth

        state.addBody(new Body("Sun",   BodyType.STAR,   M_SUN,   R_SUN,   pSun,   vSun));
        state.addBody(new Body("Earth", BodyType.PLANET, M_EARTH, R_EARTH, pEarth, vEarth));
        state.addBody(new Body("Moon",  BodyType.MOON,   M_MOON,  R_MOON,  pMoon,  vMoon));

        zeroTotalMomentum(state);
        return state;
    }

    /** Sun + 8 planets at perihelion (elliptical starts). */
    public static SystemState solarSystem() {
        final double M_SUN = 1.9885e30;
        final double R_SUN = 6.9634e8;

        // GM_sun (m^3/s^2) if you need it elsewhere:
        // final double GM = 1.32712440018e20;

        SystemState state = new SystemState();

        // Sun
        state.addBody(new Body("Sun", BodyType.STAR, M_SUN, R_SUN,
                new Vector2D(0, 0), new Vector2D(0, 0)));

        // ---- Planets (perihelion position r_p on +x, tangential speed v_p on +y) ----
        state.addBody(new Body("Mercury", BodyType.PLANET, 3.3011e23, 2.4397e6,
                new Vector2D(4.6003176318e10, 0), new Vector2D(0, 58974.3995)));

        state.addBody(new Body("Venus", BodyType.PLANET, 4.8675e24, 6.0518e6,
                new Vector2D(1.0746835173e11, 0), new Vector2D(0, 35260.3912)));

        state.addBody(new Body("Earth", BodyType.PLANET, 5.9720e24, 6.3710e6,
                new Vector2D(1.4709958626e11, 0), new Vector2D(0, 30286.3198)));

        state.addBody(new Body("Mars", BodyType.PLANET, 6.4171e23, 3.3895e6,
                new Vector2D(2.0665246705e11, 0), new Vector2D(0, 26498.7297)));

        state.addBody(new Body("Jupiter", BodyType.PLANET, 1.89819e27, 6.9911e7,
                new Vector2D(7.40352941697e11, 0), new Vector2D(0, 13712.0837)));

        state.addBody(new Body("Saturn", BodyType.PLANET, 5.6834e26, 5.8232e7,
                new Vector2D(1.34611961598e12, 0), new Vector2D(0, 10205.8397)));

        state.addBody(new Body("Uranus", BodyType.PLANET, 8.6813e25, 2.5362e7,
                new Vector2D(2.73805135235e12, 0), new Vector2D(0, 7121.3632)));

        state.addBody(new Body("Neptune", BodyType.PLANET, 1.02413e26, 2.4622e7,
                new Vector2D(4.45957335206e12, 0), new Vector2D(0, 5478.5880)));

        // Keep the center-of-mass at rest
        zeroTotalMomentum(state);
        return state;
    }


    /** Equal-mass binary stars at separation d with COM at rest. */
    public static SystemState binaryStars() {
        SystemState state = new SystemState();

        double m = 1.0e30;      // each star mass
        double rStar = 6e8;     // render radius
        double d = 5.0e10;      // center-to-center separation

        // Each star orbits at radius d/2; speed v = sqrt(G m / (2 d))
        double v = Math.sqrt(Constants.G * m / (2.0 * d));

        Vector2D pA = new Vector2D(-d / 2.0, 0);
        Vector2D pB = new Vector2D( d / 2.0, 0);
        Vector2D vA = new Vector2D(0, -v);
        Vector2D vB = new Vector2D(0,  v);

        state.addBody(new Body("Star A", BodyType.STAR, m, rStar, pA, vA));
        state.addBody(new Body("Star B", BodyType.STAR, m, rStar, pB, vB));

        zeroTotalMomentum(state);
        return state;
    }

    /* --- Helpers --- */

    /** Circular orbital speed given GM and radius r. */
    private static double circSpeed(double GM, double r) {
        return Math.sqrt(GM / r);
    }

    /** Subtract COM velocity from all bodies so total momentum is zero. */
    private static void zeroTotalMomentum(SystemState state) {
        var bodies = state.getBodies();
        double totalMass = 0.0;
        double Px = 0.0, Py = 0.0;

        for (var b : bodies) {
            Px += b.getVelocity().x * b.getMass();
            Py += b.getVelocity().y * b.getMass();
            totalMass += b.getMass();
        }
        if (totalMass == 0) return;

        double vxCom = Px / totalMass;
        double vyCom = Py / totalMass;

        for (var b : bodies) {
            b.getVelocity().x -= vxCom;
            b.getVelocity().y -= vyCom;
        }
    }

}
