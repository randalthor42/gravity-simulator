package dev.andreisima.orbitsim.ui.render;

import dev.andreisima.orbitsim.core.model.Body;
import dev.andreisima.orbitsim.core.model.BodyType;
import dev.andreisima.orbitsim.core.model.SystemState;
import dev.andreisima.orbitsim.core.util.Vector2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for drawing the current system state.
 */
public class Renderer {
    private final Canvas canvas;
    private final List<Vector2D> stars = new ArrayList<>();

    public Renderer(Canvas canvas) {
        this.canvas = canvas;
        generateStarfield(400);
    }

    private void generateStarfield(int count) {
        Random rnd = new Random();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        for (int i = 0; i < count; i++) {
            stars.add(new Vector2D(rnd.nextDouble() * w, rnd.nextDouble() * h));
        }
    }

    public void render(SystemState state, Camera camera) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, w, h);
        gc.setFill(Color.WHITE);
        for (Vector2D s : stars) {
            gc.fillRect(s.x, s.y, 1, 1);
        }

        // trails
        for (Body b : state.getBodies()) {
            drawTrail(gc, b, camera);
        }

        // bodies
        for (Body b : state.getBodies()) {
            drawBody(gc, b, camera);
        }
    }

    private void drawTrail(GraphicsContext gc, Body body, Camera camera) {
        var trail = body.getTrail();
        if (trail.isEmpty()) return;
        Vector2D prev = null;
        int i = 0;
        int n = trail.size();
        Color base = colorFor(body.getType());
        for (Vector2D p : trail) {
            Vector2D screen = camera.worldToScreen(p);
            if (prev != null) {
                double alpha = (double) i / n;
                gc.setStroke(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
                gc.strokeLine(prev.x, prev.y, screen.x, screen.y);
            }
            prev = screen;
            i++;
        }
    }

    private void drawBody(GraphicsContext gc, Body body, Camera camera) {
        Vector2D screen = camera.worldToScreen(body.getPosition());
        double basePx = switch (body.getType()) {
            case STAR -> 14; case PLANET -> 8; case MOON -> 5; case ASTEROID -> 3; case BLACK_HOLE -> 10;
        };
        double radius = Math.max(2, Math.min(basePx, 50)); // ignore camera.getZoom() for size

        radius = Math.max(2, Math.min(radius, 50));

        Color color = colorFor(body.getType());

        if (body.getType() == BodyType.BLACK_HOLE) {
            double rs = body.getSchwarzschildRadius() * camera.getZoom();
            gc.setFill(Color.BLACK);
            gc.fillOval(screen.x - rs, screen.y - rs, rs * 2, rs * 2);
            gc.setStroke(Color.PURPLE);
            gc.strokeOval(screen.x - 1.5 * rs, screen.y - 1.5 * rs, rs * 3, rs * 3);
            return;
        }

        // glow using radial gradient
        RadialGradient gradient = new RadialGradient(0, 0, screen.x, screen.y, radius * 2, false,
                CycleMethod.NO_CYCLE, new Stop(0, color), new Stop(1, Color.TRANSPARENT));
        gc.setGlobalBlendMode(BlendMode.ADD);
        gc.setFill(gradient);
        gc.fillOval(screen.x - radius * 2, screen.y - radius * 2, radius * 4, radius * 4);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);

        gc.setFill(color);
        gc.fillOval(screen.x - radius, screen.y - radius, radius * 2, radius * 2);
    }

    private Color colorFor(BodyType type) {
        return switch (type) {
            case STAR -> Color.GOLD;
            case PLANET -> Color.CORNFLOWERBLUE;
            case MOON -> Color.LIGHTGRAY;
            case ASTEROID -> Color.DARKGRAY;
            case BLACK_HOLE -> Color.BLACK;
        };
    }
}
