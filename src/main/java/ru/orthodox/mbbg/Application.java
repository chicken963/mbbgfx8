package ru.orthodox.mbbg;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Lazy;
import ru.orthodox.mbbg.utils.screen.ScreenService;

@Lazy
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class Application extends AbstractJavaFxApplication {

    @Value("${ui.title:JavaFX приложение}")//
    private String windowTitle;

    @Autowired
    private ScreenService screenService;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(windowTitle);
        stage.getIcons().add(new Image(Application.class.getResourceAsStream("/icon.png")));
        Scene startScene = screenService.activate("startMenu");
//        startScene.getStylesheets().add("styleSheets/start-scene.css");
        stage.setScene(startScene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launchApp(Application.class, args);
    }

}