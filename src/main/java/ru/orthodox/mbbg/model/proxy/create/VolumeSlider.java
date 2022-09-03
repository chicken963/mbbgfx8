package ru.orthodox.mbbg.model.proxy.create;

import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import static ru.orthodox.mbbg.utils.common.ThreadUtils.runTaskInSeparateThread;
import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;

@Getter
public class VolumeSlider {
    private final HBox root;
    private final Slider slider;
    private final HBox imageContainer;
    private final PlayMediaService playMediaService;
    private StackPane volumeSliderStackPane;

    public VolumeSlider(HBox root, Slider slider, HBox imageContainer, PlayMediaService playMediaService) {
        this.root = root;
        this.slider = slider;
        this.imageContainer = imageContainer;
        this.playMediaService = playMediaService;

        ImageView lowVolumeButton = findElementByTypeAndStyleclass(imageContainer, "low-volume");
        ImageView mediumVolumeButton = findElementByTypeAndStyleclass(imageContainer, "medium-volume");
        ImageView highVolumeButton = findElementByTypeAndStyleclass(imageContainer, "high-volume");

        lowVolumeButton.managedProperty().bind(lowVolumeButton.visibleProperty());
        mediumVolumeButton.managedProperty().bind(mediumVolumeButton.visibleProperty());
        highVolumeButton.managedProperty().bind(highVolumeButton.visibleProperty());

        lowVolumeButton.setVisible(false);
        mediumVolumeButton.setVisible(false);

        slider.setValue(playMediaService.getVolume());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            playMediaService.setVolume(newValue.doubleValue());
            if (newValue.doubleValue() < 33.3) {
                mediumVolumeButton.setVisible(false);
                highVolumeButton.setVisible(false);
                lowVolumeButton.setVisible(true);
            } else if (newValue.doubleValue() < 66.6) {
                lowVolumeButton.setVisible(false);
                highVolumeButton.setVisible(false);
                mediumVolumeButton.setVisible(true);
            } else {
                lowVolumeButton.setVisible(false);
                mediumVolumeButton.setVisible(false);
                highVolumeButton.setVisible(true);
            }
        });

        runTaskInSeparateThread(this::loadVolumeSlider, "load-volume-slider-" + this.hashCode());

    }

    private void loadVolumeSlider() {
        if (volumeSliderStackPane == null) {
            volumeSliderStackPane = (StackPane) getSlider().lookup(".track");
            if (volumeSliderStackPane != null) {
                configureVolumeSlider(playMediaService.getVolume());
            }
        }
    }

    private void configureVolumeSlider(int value) {
        StackPane volumeSliderStackPane = (StackPane) getSlider().lookup(".track");
        getSlider().valueProperty().addListener((ov, old_val, new_val) -> {
            String style = String.format("-fx-background-color: linear-gradient(to right, orange %d%%, #969696 %d%%);",
                    new_val.intValue(), new_val.intValue());
            volumeSliderStackPane.setStyle(style);
        });

        volumeSliderStackPane.setStyle(
                String.format("-fx-background-color: linear-gradient(to right, orange %d%%, #969696 %d%%);", value, value));

    }
}
