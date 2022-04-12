package ru.orthodox.mbbg.mappers;

import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.utils.ui.newGameScene.RoundTab;

import java.util.UUID;

@Service
public class TabToRoundMapper {
    public Round generateFromUIContent(RoundTab roundTab){
        return Round.builder()
                .id(UUID.randomUUID())
                .name(roundTab.getNewRoundNameTextField().getText())
                .firstStrikeCondition(WinCondition.findByMessage(roundTab.getFirstPrizeCondition().getValue().toString()))
                .secondStrikeCondition(WinCondition.findByMessage(roundTab.getSecondPrizeCondition().getValue().toString()))
                .thirdStrikeCondition(WinCondition.findByMessage(roundTab.getThirdPrizeCondition().getValue().toString()))
                .width(roundTab.getRowsNumber().getValue())
                .height(roundTab.getColumnsNumber().getValue())
                .audioTracks(roundTab.getAudioTracksTable().getAudioTracks())
                .build();
    }
}
