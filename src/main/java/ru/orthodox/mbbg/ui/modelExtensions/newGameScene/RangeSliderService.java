package ru.orthodox.mbbg.ui.modelExtensions.newGameScene;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.PlayService;
import ru.orthodox.mbbg.utils.GridRowUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;

import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toDoubleFormat;
import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toStringFormat;

/**
 * Manages range slider for audio track on the stage of bounds configuring.
 */
public class RangeSliderService {

    private final static DecimalFormat DECIMAL_FORMAT = preconfigureFormatForSliderBarProgressCss();

    private static DecimalFormat preconfigureFormatForSliderBarProgressCss() {
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            return new DecimalFormat("#0.00", decimalFormatSymbols);
    }
    public static void updateRangeSlider(PlayService playService, List<AudioTrackGridRow> gridRows) {
        if (playService == null) {
            return;
        }

        AudioTrack currentTrack = playService.getCurrentTrack();
        AudioTrackGridRow currentRow = GridRowUtils.findByAudioTrack(gridRows, currentTrack);
        RangeSlider rangeSlider = currentRow.getRangeSlider();
        Label progressLabel = currentRow.getProgressLabel();
        if (rangeSlider != null) {
            Node sliderBar = rangeSlider.lookup(".range-bar");

            double snippetLength = currentTrack.getFinishInSeconds() - currentTrack.getStartInSeconds();
            double currentTimeFromTrackBeginning = playService.getCurrentTime();
            double playedInSnippet = playService.isStopped() || currentTimeFromTrackBeginning < currentTrack.getStartInSeconds()
                    ? 0
                    : currentTimeFromTrackBeginning - currentTrack.getStartInSeconds();
            progressLabel.setText(toStringFormat(playedInSnippet) + "/" + toStringFormat(currentTrack.getFinishInSeconds() - currentTrack.getStartInSeconds()));
            if (sliderBar != null) {
                sliderBar.setStyle(new StringBuilder("-fx-background-color: ")
                        .append("linear-gradient(to right, orange ")
                        .append(DECIMAL_FORMAT.format((playedInSnippet / snippetLength) * 100))
                        .append("%, #baa ")
                        .append(DECIMAL_FORMAT.format((playedInSnippet / snippetLength) * 100))
                        .append("%)")
                        .toString());
            }
        }
    }
}
