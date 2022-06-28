package ru.orthodox.mbbg.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.Round;
import ru.orthodox.mbbg.ui.modelExtensions.newGameScene.RoundDimensionsDealer;
import ru.orthodox.mbbg.ui.modelExtensions.newGameScene.RoundTab;

import java.util.UUID;

@Service
public class TabToRoundMapper {

    public Round generateFromUIContent(RoundTab roundTab){
        return Round.builder()
                .id(UUID.randomUUID())
                .name(roundTab.getNewRoundNameTextField().getText())
                .firstStrikeCondition(roundTab.getFirstPrizeCondition().getValue())
                .secondStrikeCondition(roundTab.getSecondPrizeCondition().getValue())
                .thirdStrikeCondition(roundTab.getThirdPrizeCondition().getValue())
                .width(RoundDimensionsDealer.getBlankDimension(roundTab.getBlankDimensions()))
                .height(RoundDimensionsDealer.getBlankDimension(roundTab.getBlankDimensions()))
                .numberOfBlanks(Integer.parseInt(roundTab.getNumberOfBlanks().getText()))
                .audioTracks(roundTab.getAudioTracksTable().getAudioTracks())
                .build();
    }
}
