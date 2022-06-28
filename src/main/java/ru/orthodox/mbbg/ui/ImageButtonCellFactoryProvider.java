package ru.orthodox.mbbg.ui;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.orthodox.mbbg.enums.ButtonType;
import ru.orthodox.mbbg.model.AudioTrack;

import java.util.function.Consumer;

@Component
public class ImageButtonCellFactoryProvider implements CellFactoryProvider {

    @Autowired
    private ActiveTableRowDealer activeTableRowDealer;

    @Override
    public Callback<TableColumn<AudioTrack, String>, TableCell<AudioTrack, String>> provide(String imageUrl, Consumer<AudioTrack> consumer, ButtonType mode) {
        return new Callback<TableColumn<AudioTrack, String>, TableCell<AudioTrack, String>>() {
            @Override
            public TableCell call(final TableColumn<AudioTrack, String> param) {
                Image img = new Image(imageUrl);
                ImageView imgv = new ImageView(img);
                imgv.setFitHeight(18);
                imgv.setFitWidth(18);
                imgv.setPickOnBounds(true);
                imgv.setPreserveRatio(true);
                Button btn = new Button(null, imgv);
                btn.setPrefWidth(18);
                btn.setPrefHeight(18);
                btn.setTranslateX(-10);
                btn.setPickOnBounds(true);
                btn.setCursor(Cursor.HAND);
                btn.setStyle("-fx-background-color: transparent;");
                return new TableCell<AudioTrack, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                if (ButtonType.PLAY.equals(mode)) {
                                    activeTableRowDealer.updateActiveRow((TableRow<AudioTrack>) btn.getParent().getParent());
                                }
                                AudioTrack audioTrack = getTableView().getItems().get(getIndex());
                                consumer.accept(audioTrack);
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            }
        };
    }
}
