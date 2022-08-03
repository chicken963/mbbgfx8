package ru.orthodox.mbbg.utils.screen;

import javafx.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.orthodox.mbbg.configuration.ControllersConfig;

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
    @Qualifier("viewBlanksView")
    private ControllersConfig.View viewBlanksView;

    @Autowired
    @Qualifier("audioTracksLibraryView")
    private ControllersConfig.View audioTracksLibraryView;

    @Bean
    public ScreenViewResource startMenuScene() {
        return new ScreenViewResource("startMenu", startMenuView, "styleSheets/start-scene.css");
    }

    @Bean
    public ScreenViewResource playGameScene() {
        return new ScreenViewResource("play", playView, "styleSheets/play-game.css");
    }

    @Bean
    public ScreenViewResource newGameScene() {
        return new ScreenViewResource("newGame", newGameView, "styleSheets/new-game.css");
    }

    @Bean
    public ScreenViewResource popupScene() {
        return new ScreenViewResource("popup", popupView, "styleSheets/start-scene.css");
    }
    @Bean
    public ScreenViewResource viewBlanksScene() {
        return new ScreenViewResource("viewBlanks", viewBlanksView, "styleSheets/start-scene.css");
    }
    @Bean
    public ScreenViewResource audioTracksLibraryScene() {
        return new ScreenViewResource("audioTracksLibrary", audioTracksLibraryView, "styleSheets/new-game.css");
    }

}
