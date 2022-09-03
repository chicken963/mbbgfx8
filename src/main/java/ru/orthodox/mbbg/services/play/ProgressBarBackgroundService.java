package ru.orthodox.mbbg.services.play;

import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.AudioTrack;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Service
public class ProgressBarBackgroundService {
    private static final String NOT_COVERED_PROGRESS_RANGE_COLOR = "#eee";
    private static final String COVERED_PROGRESS_RANGE_COLOR = "#999";
    private static final String COVERED_PROGRESS_RANGE_COLOR_INTERMEDIATE = "#aaa";
    private DecimalFormat decimalFormat;
    private ProgressBar songProgressBar;
    private Node sliderBar;

    @PostConstruct
    private void preconfigureDecimalFormatForCss() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("#0.00", decimalFormatSymbols);
    }

    public void configureUIElements(ProgressBar sliderBar) {
        this.songProgressBar = sliderBar;
    }

    public void recalculateProgressBarBackgroundRange(AudioTrack currentTrack) {
        double rangeStripStart = currentTrack.getStartInSeconds() / currentTrack.getLengthInSeconds() * 100;
        double rangeStripStartBlurred = rangeStripStart - 2;

        double rangeStripMiddle = (currentTrack.getStartInSeconds() + currentTrack.getFinishInSeconds()) / 2 / currentTrack.getLengthInSeconds() * 100;

        double rangeStripEnd = currentTrack.getFinishInSeconds() / currentTrack.getLengthInSeconds() * 100;
        double rangeStripEndBlurred = Math.min(100, rangeStripEnd + 2);

        if (sliderBar == null) {
            this.sliderBar = songProgressBar.lookup(".track");
        }
        if (sliderBar != null) { //-fx-background-color: linear-gradient(to right, #eee 30%, #aaa 30%, #999 50%, #aaa 70%, #eee 70%)
            sliderBar.setStyle(new StringBuilder("-fx-background-color: ")
                    .append("linear-gradient(to right, ")
                    .append(NOT_COVERED_PROGRESS_RANGE_COLOR + " ")
                    .append(decimalFormat.format(rangeStripStartBlurred))
                    .append("%, ")
                    .append(COVERED_PROGRESS_RANGE_COLOR_INTERMEDIATE + " ")
                    .append(decimalFormat.format(rangeStripStart))
                    .append("%, ")
                    .append(COVERED_PROGRESS_RANGE_COLOR + " ")
                    .append(decimalFormat.format(rangeStripMiddle))
                    .append("%, ")
                    .append(COVERED_PROGRESS_RANGE_COLOR_INTERMEDIATE + " ")
                    .append(decimalFormat.format(rangeStripEnd))
                    .append("%, ")
                    .append(NOT_COVERED_PROGRESS_RANGE_COLOR + " ")
                    .append(decimalFormat.format(rangeStripEndBlurred))
                    .append("%)")
                    .toString());
        }
    }
}
