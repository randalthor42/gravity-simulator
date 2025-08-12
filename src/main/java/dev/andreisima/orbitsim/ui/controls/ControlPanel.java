package dev.andreisima.orbitsim.ui.controls;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Basic control panel with play/pause button and time step slider.
 */
public class ControlPanel extends HBox {
    private final Button playPause = new Button("Pause");
    private final Slider dtSlider = new Slider(1, 25000, 60); // seconds per step
    private boolean running = true;

    public ControlPanel(Runnable onPlay, Runnable onPause) {
        setPadding(new Insets(5));
        setSpacing(10);

        playPause.setOnAction(e -> {
            running = !running;
            if (running) {
                playPause.setText("Pause");
                onPlay.run();
            } else {
                playPause.setText("Play");
                onPause.run();
            }
        });

        dtSlider.setShowTickLabels(true);
        dtSlider.setShowTickMarks(true);
        dtSlider.setMajorTickUnit(900);
        dtSlider.setMinorTickCount(4);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(playPause, spacer, dtSlider);
    }

    public double getDt() {
        return dtSlider.getValue();
    }
}
