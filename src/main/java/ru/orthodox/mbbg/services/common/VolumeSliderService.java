package ru.orthodox.mbbg.services.common;

import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.proxy.create.VolumeSlider;
import ru.orthodox.mbbg.utils.screen.ScreenService;

import static ru.orthodox.mbbg.utils.hierarchy.ElementFinder.findElementByTypeAndStyleclass;
import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Service
public class VolumeSliderService {
    @Autowired
    private PlayMediaService playMediaService;
    @Autowired
    private ScreenService screenService;

    public VolumeSlider createNewSlider() {
        HBox volumeSliderContainerTemplate = (HBox) screenService.getParentNode("volumeSlider");
        HBox volumeSliderContainer = (HBox) createDeepCopy(volumeSliderContainerTemplate);
        HBox imageContainer = findElementByTypeAndStyleclass(volumeSliderContainer, "image-container");

        Slider slider = findElementByTypeAndStyleclass(volumeSliderContainer, "slider");

        return new VolumeSlider(volumeSliderContainer, slider, imageContainer, playMediaService);
    }

    public void switchMute(VolumeSlider volumeSlider) {
/*        if (playMediaService.isMuted()) {

        } else {
            playMediaService.mute();

        }*/
        playMediaService.switchMute();
        volumeSlider.actualizeValue();
    }
}
