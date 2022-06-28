package ru.orthodox.mbbg.ui;

import javafx.scene.control.TableRow;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;

@Service
public class ActiveTableRowDealer {
    private TableRow<AudioTrack> activeRow;
    private TableRow<AudioTrack> hoveredRow;

    public void updateActiveRow(TableRow<AudioTrack> row) {
        if (!row.isEmpty()) {
            if (activeRow != null) {
                activeRow.getStyleClass().remove("highlighted");
            }
            row.getStyleClass().add("highlighted");
            activeRow = row;
        }
    }

    public void updateHoveredRow(TableRow<AudioTrack> row) {
        if (!row.isEmpty()) {
            if (row != activeRow) {
                row.getStyleClass().add("hovered");
            }
            hoveredRow = row;
        }
    }

    public void deactivateHoveredRow(TableRow<AudioTrack> row) {
        row.getStyleClass().remove("hovered");

    }
}
