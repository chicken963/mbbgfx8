package ru.orthodox.mbbg;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import ru.orthodox.mbbg.configuration.ControllersConfig;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Paths;

@Lazy
@SpringBootApplication
public class Application extends AbstractJavaFxApplication {

    @Value("${ui.title:JavaFX приложение}")//
    private String windowTitle;

    @Autowired
    private ControllersConfig.View view;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(view.getView()));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
/*        Media pick = new Media(Paths.get("src/main/resources/music/armin van buuren falling.mp3").toUri().toString()); // replace this with your own audio file
        MediaPlayer player = new MediaPlayer(pick);

        // Add a mediaView, to display the media. Its necessary !
        // This mediaView is added to a Pane
        MediaView mediaView = new MediaView(player);

        // Add to scene
        Group root = new Group(mediaView);
        Scene scene = new Scene(root, 500, 200);

        // Show the stage
        primaryStage.setTitle("Media Player");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Play the media once the stage is shown
        player.play();*/
    }

    public static void main(String[] args) {
        launchApp(Application.class, args);
    }

}