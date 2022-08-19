package ru.orthodox.mbbg.services.create;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackUIView;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.toStringFormat;

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
    public static void updateRangeSlider(PlayMediaService playMediaService, List<AudioTrackUIView> gridRows) {
        if (playMediaService == null || playMediaService.getCurrentTrack() == null) {
            return;
        }

        AudioTrack currentTrack = playMediaService.getCurrentTrack();
        AudioTrackUIView currentRow = AudiotrackAsGridRowService.findByAudioTrack(gridRows, currentTrack);
        RangeSlider rangeSlider = currentRow.getRangeSlider();
        Label progressLabel = currentRow.getProgressLabel();
        if (rangeSlider != null) {
            Node sliderBar = rangeSlider.lookup(".range-bar");

            double snippetLength = currentTrack.getFinishInSeconds() - currentTrack.getStartInSeconds();
            double currentTimeFromTrackBeginning = playMediaService.getCurrentTime();
            double playedInSnippet = playMediaService.isStopped() || currentTimeFromTrackBeginning < currentTrack.getStartInSeconds()
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
