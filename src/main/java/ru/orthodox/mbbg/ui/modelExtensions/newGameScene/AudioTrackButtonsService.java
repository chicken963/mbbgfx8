package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;

public class AudioTrackButtonsService {
    public static Button prepareButtonView(String imageUrl) {
        Image img = new Image(imageUrl);
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(18.0);
        imgv.setFitWidth(18.0);
        imgv.setPickOnBounds(true);
        imgv.setPreserveRatio(true);
        Button btn = new Button(null, imgv);
        btn.setStyle("-fx-background-color: orange; -fx-background-radius: 50%");
        btn.setMaxSize(25.0, 25.0);
        btn.setPrefSize(25.0, 25.0);
        btn.setMinSize(25.0, 25.0);
//        btn.setTranslateX(-10);
        btn.setPickOnBounds(true);
        btn.setCursor(Cursor.HAND);

        return btn;
    }
}
