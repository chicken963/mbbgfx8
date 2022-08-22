package ru.orthodox.mbbg.services.create;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.controlsfx.control.RangeSlider;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Optional;

import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.toStringFormat;

/**
 * Manages range slider for audio track on the stage of bounds configuring.
 */

public class RangeSliderService {

    private DecimalFormat decimalFormat;

    private AudioTrackUIViewService audioTrackUIViewService;
    private List<AudioTrackEditUIView> gridRows;

    public RangeSliderService(List<AudioTrackEditUIView> gridRows, AudioTrackUIViewService audioTrackUIViewService) {
        this.gridRows = gridRows;
        this.audioTrackUIViewService = audioTrackUIViewService;

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        this.decimalFormat = new DecimalFormat("#0.00", decimalFormatSymbols);
    }

    public void updateRangeSlider(PlayMediaService playMediaService) {
        if (playMediaService == null || playMediaService.getCurrentTrack() == null) {
            return;
        }

        AudioTrack currentTrack = playMediaService.getCurrentTrack();
        Optional<AudioTrackEditUIView> currentRow = audioTrackUIViewService.findByAudioTrack(gridRows, currentTrack);
        if (currentRow.isPresent()) {
            RangeSlider rangeSlider = currentRow.get().getRangeSlider();
            Label progressLabel = currentRow.get().getProgressLabel();
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
                            .append(decimalFormat.format((playedInSnippet / snippetLength) * 100))
                            .append("%, #baa ")
                            .append(decimalFormat.format((playedInSnippet / snippetLength) * 100))
                            .append("%)")
                            .toString());
                }
            }
        }
    }
}
