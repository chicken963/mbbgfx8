package ru.orthodox.mbbg.services.create;

import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.Getter;
import org.controlsfx.control.RangeSlider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.AudioTrack;
import ru.orthodox.mbbg.model.proxy.create.AudioTrackEditUIView;
import ru.orthodox.mbbg.services.common.PlayMediaService;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static ru.orthodox.mbbg.utils.common.TimeRepresentationConverter.toStringFormat;

/**
 * Manages range slider for audio track on the stage of bounds configuring.
 */
@Service
public class RangeSliderService {

    private DecimalFormat decimalFormat;

    @Autowired
    private AudioTrackUIViewService audioTrackUIViewService;
    @Autowired
    private PlayMediaService playMediaService;
    @Getter
    private AudioTrackEditUIView activeRow;

    private Node sliderBar;
    private double lastSavedPlayedInSnippetValue = 0;

    @PostConstruct
    public void setup() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        this.decimalFormat = new DecimalFormat("#0.00", decimalFormatSymbols);
    }

    public void updateRangeSlider() {
        if (playMediaService.getCurrentTrack() == null) {
            return;
        }

        AudioTrack currentTrack = playMediaService.getCurrentTrack();
        if (activeRow != null && activeRow.getAudioTrack() == currentTrack) {

            Label progressLabel = activeRow.getProgressLabel();

            double snippetLength = currentTrack.getFinishInSeconds() - currentTrack.getStartInSeconds();
            double currentTimeFromTrackBeginning = playMediaService.getCurrentTime();
            double playedInSnippet = playMediaService.isStopped() || currentTimeFromTrackBeginning < currentTrack.getStartInSeconds()
                    ? 0
                    : currentTimeFromTrackBeginning - currentTrack.getStartInSeconds();
            progressLabel.setText(toStringFormat(playedInSnippet) + "/" + toStringFormat(currentTrack.getFinishInSeconds() - currentTrack.getStartInSeconds()));
            if (sliderBar != null && playedInSnippet != lastSavedPlayedInSnippetValue) {
                updateSliderProgressGradient(snippetLength, playedInSnippet);
                lastSavedPlayedInSnippetValue = playedInSnippet;
            }
        }
    }

    private void updateSliderProgressGradient(double snippetLength, double playedInSnippet) {
        sliderBar.setStyle(new StringBuilder("-fx-background-color: ")
                .append("linear-gradient(to right, orange ")
                .append(decimalFormat.format((playedInSnippet / snippetLength) * 100))
                .append("%, #baa ")
                .append(decimalFormat.format((playedInSnippet / snippetLength) * 100))
                .append("%)")
                .toString());
    }

    public void setActiveRow(AudioTrackEditUIView row) {
        this.activeRow = row;
        RangeSlider rangeSlider = activeRow.getRangeSlider();
        this.sliderBar = rangeSlider.lookup(".range-bar");
    }
}
