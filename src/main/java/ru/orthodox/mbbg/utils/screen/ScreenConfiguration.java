package ru.orthodox.mbbg.utils.screen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.orthodox.mbbg.configuration.ControllersConfig;

import java.util.Arrays;

@Configuration
public class ScreenConfiguration {
    @Autowired
    @Qualifier("playView")
    private ControllersConfig.View playView;

    @Autowired
    @Qualifier("startMenuView")
    private ControllersConfig.View startMenuView;

    @Autowired
    @Qualifier("newGameView")
    private ControllersConfig.View newGameView;

    @Autowired
    @Qualifier("popupView")
    private ControllersConfig.View popupView;

    @Autowired
    @Qualifier("popupOkCancelView")
    private ControllersConfig.View popupOkCancelView;

    @Autowired
    @Qualifier("viewBlanksView")
    private ControllersConfig.View viewBlanksView;

    @Autowired
    @Qualifier("audioTracksLibraryView")
    private ControllersConfig.View audioTracksLibraryView;

    @Autowired
    @Qualifier("audioTracksLibraryRow")
    private ControllersConfig.View audioTracksLibraryRow;

    @Autowired
    @Qualifier("volumeSlider")
    private ControllersConfig.View volumeSlider;

    @Bean
    public ScreenViewResource startMenuScene() {
        return new ScreenViewResource(
                "startMenu",
                startMenuView,
                Arrays.asList("styleSheets/start-scene.css", "styleSheets/scrollable-table.css"));
    }

    @Bean
    public ScreenViewResource playGameScene() {
        return new ScreenViewResource(
                "play",
                playView,
                Arrays.asList("styleSheets/play-game.css", "styleSheets/scrollable-table.css", "styleSheets/volume-slider.css"));
    }

    @Bean
    public ScreenViewResource newGameScene() {
        return new ScreenViewResource("newGame", newGameView,
                Arrays.asList("styleSheets/new-game.css", "styleSheets/scrollable-table.css", "styleSheets/tab-pane.css", "styleSheets/volume-slider.css"));
    }

    @Bean
    public ScreenViewResource popupScene() {
        return new ScreenViewResource("popup", popupView, Arrays.asList("styleSheets/popup.css",  "styleSheets/scrollable-table.css"));
    }

    @Bean
    public ScreenViewResource popupOkCancelScene() {
        return new ScreenViewResource("popupOkCancel", popupOkCancelView, Arrays.asList("styleSheets/popup.css",  "styleSheets/scrollable-table.css"));
    }
    @Bean
    public ScreenViewResource viewBlanksScene() {
        return new ScreenViewResource("viewBlanks", viewBlanksView,
                Arrays.asList("styleSheets/view-blanks.css", "styleSheets/scrollable-table.css", "styleSheets/tab-pane.css"));
    }
    @Bean
    public ScreenViewResource audioTracksLibraryScene() {
        return new ScreenViewResource("audioTracksLibrary", audioTracksLibraryView,
                Arrays.asList("styleSheets/scrollable-table.css", "styleSheets/new-game.css"));
    }
    @Bean
    public ScreenViewResource audioTracksLibraryRowScene() {
        return new ScreenViewResource("audioTracksLibraryRow", audioTracksLibraryRow, "styleSheets/new-game.css");
    }
    @Bean
    public ScreenViewResource audioVolumeSliderScene() {
        return new ScreenViewResource("volumeSlider", volumeSlider, "styleSheets/volume-slider.css");
    }

}
