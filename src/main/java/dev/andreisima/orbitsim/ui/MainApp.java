package dev.andreisima.orbitsim.ui;

import dev.andreisima.orbitsim.core.model.SystemState;
import dev.andreisima.orbitsim.core.physics.LeapfrogIntegrator;
import dev.andreisima.orbitsim.core.physics.PhysicsEngine;
import dev.andreisima.orbitsim.core.presets.PresetFactory;
import dev.andreisima.orbitsim.core.util.Vector2D;
import dev.andreisima.orbitsim.ui.controls.ControlPanel;
import dev.andreisima.orbitsim.ui.render.Camera;
import dev.andreisima.orbitsim.ui.render.Renderer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX application.
 */
public class MainApp extends Application {
    private final SystemState state = PresetFactory.earthMoonElliptical();
    private final PhysicsEngine physics = new PhysicsEngine(new LeapfrogIntegrator());
    private final Canvas canvas = new Canvas(800, 600);
    private final Camera camera = new Camera();
    private Renderer renderer;
    private ControlPanel controls;
    private boolean running = true;

    @Override
    public void start(Stage stage) {
        renderer = new Renderer(canvas);
        controls = new ControlPanel(() -> running = true, () -> running = false);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setBottom(controls);

        Scene scene = new Scene(root);
        stage.setTitle("Orbit Simulator");
        stage.setScene(scene);
        stage.show();

        setupInteraction(scene);
        // Fit ~4 AU across the canvas
        final double AU = 1.496e11;
        double desiredWidthMeters = 4 * AU;
        double targetZoom = canvas.getWidth() / desiredWidthMeters;

// Apply absolute zoom using the existing relative zoom(factor, anchor)
        double factor = targetZoom / camera.getZoom();
        camera.zoom(factor, canvas.getWidth() / 2, canvas.getHeight() / 2);

// Re-center after zoom
        centerCamera();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    double dt = controls.getDt();
                    physics.step(state, dt);
                }
                renderer.render(state, camera);
            }
        };
        timer.start();
    }

    private void setupInteraction(Scene scene) {
        // zoom with scroll
        canvas.setOnScroll(e -> {
            double factor = e.getDeltaY() > 0 ? 1.1 : 0.9;
            camera.zoom(factor, e.getX(), e.getY());
        });

        // pan with mouse drag
        final double[] last = new double[2];
        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                last[0] = e.getX();
                last[1] = e.getY();
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                camera.pan(e.getX() - last[0], e.getY() - last[1]);
                last[0] = e.getX();
                last[1] = e.getY();
            }
        });

        // keyboard shortcuts
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                running = !running;
            } else if (e.getCode() == KeyCode.C) {
                centerCamera();
            }
        });
    }

    private void centerCamera() {
        Vector2D bary = state.computeBarycenter();
        camera.centerOn(bary, canvas.getWidth(), canvas.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}