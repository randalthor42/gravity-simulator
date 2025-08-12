# Gravity Simulator

A simple JavaFX-based N-body gravity simulator built with Gradle to explore celestial mechanics and orbital physics.

## Overview
Gravity Simulator models the motion of bodies under Newtonian gravity using a **Velocity-Verlet (Leapfrog) integrator** for improved energy conservation over time.  
The app renders the simulation in real time with smooth trails, zooming, and panning, allowing you to visualize planetary systems, moons, or custom gravitational setups.

## Why I Built This
I started this project to **learn physics**
- How **gravity** works beyond the basic equations.
- How to simulate **realistic orbits** (including eccentricity, perihelion, and aphelion).
- How **numerical integration** methods like Leapfrog differ from simple Euler methods.
- How parameters like mass, radius, and velocity affect orbital stability.
- How gravitational systems behave when multiple bodies interact.

This simulator gave me a hands-on way to **experiment with real constants** (like *G*, AU, and planetary masses) while reinforcing programming skills in Java and JavaFX.

## Features
- N-body simulation with Newtonian gravity.
- Configurable **collision modes** (merge, ignore, black hole accretion).
- Adjustable **time step** for accuracy vs. speed.
- **Trail rendering** for visualizing orbits.
- **Zoom & pan** for exploring large or small systems.
- Presets for **Earth–Moon**, **Solar System**, and custom configurations.

## Tech Stack
- **Java 17** (via Gradle toolchain)
- **JavaFX** for UI & rendering
- **Gradle** for build & run (`./gradlew run`)
- Custom physics engine

## Getting Started
```bash
# Run the simulator
./gradlew run
