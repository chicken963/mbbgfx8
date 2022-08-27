package ru.orthodox.mbbg.configuration;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.orthodox.mbbg.controllers.NewGameController;
import ru.orthodox.mbbg.controllers.PlayController;
import ru.orthodox.mbbg.controllers.StartMenuController;
import ru.orthodox.mbbg.controllers.ViewBlanksController;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class ControllersConfig {

    @Bean(name = "playView")
    public View getPlayView() throws IOException {
        return loadView("view/play.fxml");
    }

    @Bean(name = "startMenuView")
    public View getStartMenuView() throws IOException {
        return loadView("view/startmenu.fxml");
    }

    @Bean(name = "newGameView")
    public View getNewGameView() throws IOException {
        return loadView("view/newgame.fxml");
    }

    @Bean(name = "popupView")
    public View getPopupView() throws IOException {
        return loadView("view/popup.fxml");
    }

    @Bean(name = "viewBlanksView")
    public View getViewBlanksView() throws IOException {
        return loadView("view/view_blanks.fxml");
    }

    @Bean(name = "audioTracksLibraryView")
    public View getAudioTracksLibraryView() throws IOException {
        return loadView("view/audiotracks-library.fxml");
    }

    @Bean(name = "audioTracksLibraryRow")
    public View getAudioTracksLibraryRow() throws IOException {
        return loadView("view/audiotracks-library-row.fxml");
    }

    @Bean
    public PlayController getPlayController() throws IOException {
        return (PlayController) getPlayView().getController();
    }

    @Bean
    public StartMenuController getStartMenuController() throws IOException {
        return (StartMenuController) getStartMenuView().getController();
    }

    @Bean
    public NewGameController getNewGameController() throws IOException {
        return (NewGameController) getNewGameView().getController();
    }

    @Bean
    public ViewBlanksController getViewBlanksController() throws IOException {
        return (ViewBlanksController) getViewBlanksView().getController();
    }

    protected View loadView(String url) throws IOException {
        InputStream fxmlStream = null;
        try {
            fxmlStream = getClass().getClassLoader().getResourceAsStream(url);
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            return new View(loader.getRoot(), loader.getController());
        } finally {
            if (fxmlStream != null) {
                fxmlStream.close();
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class View {
        private Parent parentNode;
        private Object controller;
    }

}
