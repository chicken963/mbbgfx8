package ru.orthodox.mbbg.utils.ui.newGameScene;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import lombok.Builder;
import org.controlsfx.control.RangeSlider;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.AudioTrack;
import ru.orthodox.mbbg.services.PlayService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toDoubleFormat;
import static ru.orthodox.mbbg.utils.TimeRepresentationConverter.toStringFormat;

/**
 * Manages range slider for audio track on the stage of bounds configuring.
 */
@Builder
public class RangeSliderDealer {
    private PlayService playService;
    private AudioTrack currentTrack;
    private RangeSlider rangeSlider;
    private DecimalFormat decimalFormat;
    private Label currentTrackStartLabel;
    private Label currentTrackEndLabel;
    private Label currentSnippetLength;
    private Label currentSnippetRate;
    private Label currentTrackInfo;



    public RangeSlider createAndIntegrateRangeSlider(PlayService playService) {
        this.playService = playService;
        this.currentTrack = playService.getCurrentTrack();
        this.rangeSlider = new RangeSlider(
                0,
                currentTrack.getLengthInSeconds(),
                currentTrack.getStartInSeconds(),
                currentTrack.getFinishInSeconds()
        );
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setShowTickMarks(true);
        rangeSlider.setPrefWidth(600);
        rangeSlider.setMinWidth(600);
        rangeSlider.setMajorTickUnit(60);
        rangeSlider.setBlockIncrement(5);
        rangeSlider.setLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return toStringFormat(object.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                return new Number() {
                    @Override
                    public int intValue() {
                        return (int) toDoubleFormat(string);
                    }

                    @Override
                    public long longValue() {
                        return (long) toDoubleFormat(string);
                    }

                    @Override
                    public float floatValue() {
                        return (float) toDoubleFormat(string);
                    }

                    @Override
                    public double doubleValue() {
                        return toDoubleFormat(string);
                    }
                };
            }
        });
        currentTrackStartLabel.setText(toStringFormat(currentTrack.getStartInSeconds()));
        currentTrackEndLabel.setText(toStringFormat(currentTrack.getFinishInSeconds()));
        currentSnippetLength.setText(toStringFormat(currentTrack.getFinishInSeconds() - currentTrack.getStartInSeconds()));
        currentSnippetRate.setText("00:00");
        preconfigureDecimalFormatForCss();
        defineSliderBoundsLogic(rangeSlider, currentSnippetLength, currentTrackStartLabel, currentTrackEndLabel);
        return rangeSlider;
    }

    private void defineSliderBoundsLogic(RangeSlider slider,
                                         Label currentTrackLength,
                                         Label currentTrackStartLabel,
                                         Label currentTrackEndLabel) {
        slider.highValueProperty().addListener((observable, oldValue, newValue) -> {
            if (playService != null) playService.stop();
            String currentTrackFinish = toStringFormat(newValue.doubleValue());
            currentTrack.setFinishInSeconds(newValue.doubleValue());
            currentTrackEndLabel.setText(currentTrackFinish);
            currentTrackLength.setText(toStringFormat(currentTrack.getFinishInSeconds() - currentTrack.getStartInSeconds()));
        });
        slider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
            if (playService != null) playService.stop();
            String currentTrackStart = toStringFormat(newValue.doubleValue());
            currentTrack.setStartInSeconds(newValue.doubleValue());
            currentTrackStartLabel.setText(currentTrackStart);
            currentTrackLength.setText(toStringFormat(toDoubleFormat(currentTrackEndLabel.getText()) -
                    toDoubleFormat(currentTrackStartLabel.getText())));
        });
    }



    public void updateRangeSlider() {
        if (rangeSlider != null && playService != null) {
            Node sliderBar = rangeSlider.lookup(".range-bar");

            double snippetLength = currentTrack.getFinishInSeconds() - currentTrack.getStartInSeconds();
            double currentTimeFromTrackBeginning = playService.getCurrentTime();
            double playedInSnippet = playService.isStopped()
                    ? 0
                    : currentTimeFromTrackBeginning - currentTrack.getStartInSeconds();

            currentSnippetRate.setText(playService.isStopped()
                    ? "00:00"
                    : toStringFormat(playedInSnippet));

            if (sliderBar != null) {
                sliderBar.setStyle(new StringBuilder("-fx-background-color: ")
                        .append("linear-gradient(to right, green ")
                        .append(decimalFormat.format(playedInSnippet / snippetLength * 100))
                        .append("%, red ")
                        .append(decimalFormat.format((playedInSnippet / snippetLength) * 100))
                        .append("%)")
                        .toString());
            }
        }
    }

    private void preconfigureDecimalFormatForCss() {
        if (decimalFormat == null) {
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
            decimalFormatSymbols.setDecimalSeparator('.');
            decimalFormat = new DecimalFormat("#0.00", decimalFormatSymbols);
        }
    }
}
