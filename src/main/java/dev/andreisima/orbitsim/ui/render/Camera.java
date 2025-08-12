package dev.andreisima.orbitsim.ui.render;

import dev.andreisima.orbitsim.core.util.Vector2D;

/**
 * Simple 2D camera supporting pan and zoom.
 */
public class Camera {
    private double offsetX;
    private double offsetY;
    private double zoom = 1.0; // pixels per meter

    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public double getZoom() { return zoom; }

    public void pan(double dx, double dy) {
        offsetX += dx;
        offsetY += dy;
    }

    public void zoom(double factor, double anchorX, double anchorY) {
        double oldZoom = zoom;
        zoom *= factor;
        offsetX = anchorX - (anchorX - offsetX) * (zoom / oldZoom);
        offsetY = anchorY - (anchorY - offsetY) * (zoom / oldZoom);
    }

    public Vector2D worldToScreen(Vector2D world) {
        return new Vector2D(world.x * zoom + offsetX, world.y * zoom + offsetY);
    }

    public Vector2D screenToWorld(double x, double y) {
        return new Vector2D((x - offsetX) / zoom, (y - offsetY) / zoom);
    }

    public void centerOn(Vector2D world, double screenWidth, double screenHeight) {
        offsetX = screenWidth / 2.0 - world.x * zoom;
        offsetY = screenHeight / 2.0 - world.y * zoom;
    }
}
