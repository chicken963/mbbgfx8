package ru.orthodox.mbbg.services.play.blank;

import javafx.animation.Animation;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.events.NextTrackChangedEvent;
import ru.orthodox.mbbg.model.basic.Blank;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.model.proxy.BlinkingColorPair;
import ru.orthodox.mbbg.model.proxy.HorizontalGradient;
import ru.orthodox.mbbg.services.model.RoundService;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.orthodox.mbbg.utils.hierarchy.NodeDeepCopyProvider.createDeepCopy;

@Setter
@Service
public class MiniaturesGridService implements ApplicationListener<NextTrackChangedEvent>  {

    private static final double GRID_WIDTH = 5.0;

    @Autowired
    private RoundService roundService;
    @Autowired
    private ProgressService progressService;
    @Getter
    @Autowired
    private AnimationBackgroundService animationProvider;

    private Button changeWinCondition;
    private GridPane miniaturesGrid;
    private RowConstraints rowConstrainsTemplate;
    private Button blankMiniatureTemplate;
    private Round round;
    List<Button> blankMiniatures;

    public void configureUIElements(
            Button changeWinCondition,
            GridPane miniaturesProgressGrid,
            RowConstraints progressRowsConstraints,
            Button blankMiniatureTemplate) {
        this.changeWinCondition = changeWinCondition;
        this.miniaturesGrid = miniaturesProgressGrid;
        this.rowConstrainsTemplate = progressRowsConstraints;
        this.blankMiniatureTemplate = blankMiniatureTemplate;
    }

    public void setActiveRound(Round round) {
        this.round = round;
        replicateRowConstraints();
        replicateMiniatureButtons();
    }

    private void replicateRowConstraints() {
        miniaturesGrid.getRowConstraints().setAll(
                Stream.generate(() -> createDeepCopy(rowConstrainsTemplate))
                        .limit((int) Math.ceil(round.getBlanksIds().size() / GRID_WIDTH))
                        .collect(Collectors.toList()));
    }

    private void replicateMiniatureButtons() {
        AtomicInteger counter = new AtomicInteger(0);
        blankMiniatures = round.getBlanks().stream()
            .map(blank -> {
                String blankNumber = blank.getNumber();
                Button button = (Button) createDeepCopy(blankMiniatureTemplate);
                button.setText(blankNumber);
                GridPane.setColumnIndex(button, counter.get() % (int) GRID_WIDTH);
                GridPane.setRowIndex(button, counter.get() / (int) GRID_WIDTH);
                counter.getAndIncrement();
                blank.setMiniatureButton(button);
                return button;
            })
            .collect(Collectors.toList());
        miniaturesGrid.getChildren().setAll(blankMiniatures);
    }


    public Button findBlankMiniature(Blank blank) {
        return blankMiniatures.stream()
                .filter(button -> button.getText().equals(blank.getNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no blank miniature with number " + blank.getNumber()));
    }

    public void shiftCurrentTargetWinCondition() {
        roundService.shiftCurrentTargetWinCondition(round);
        round.getBlanks().forEach(blank -> {
            blank.setProgress(0.0);
            blank.setNextProgress(0.0);
            blank.setWinningSet(new HashSet<>());
        });
        progressService.recalculateRoundProgress(round);
        updateAnimations(round);
    }

    public void checkWinCondition() {
        double currentMaxProgress = progressService.recalculateRoundProgress(round);
        updateAnimations(round);
        if (currentMaxProgress == 1.0 && !round.getThirdStrikeCondition().equals(round.getCurrentTargetWinCondition())) {
            changeWinCondition.setDisable(false);
        }
    }

    private void updateAnimations(Round round) {
        round.getBlanks().forEach(blank -> {
            final Animation animation;
            if (!blank.getWinningSet().isEmpty()) {
                animation = animationProvider.provide(
                    blank,
                    BlinkingColorPair.forNextProgress(1.0));
            } else {
                animation = animationProvider.provide(
                    blank,
                    HorizontalGradient.forNextProgress(blank.getNextProgress()));
            }
            animation.play();
        });
    }

    @Override
    public void onApplicationEvent(NextTrackChangedEvent nextTrackChangedEvent) {
        checkWinCondition();
    }
}
