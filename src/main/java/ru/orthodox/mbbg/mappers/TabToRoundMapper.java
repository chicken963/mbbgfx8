package ru.orthodox.mbbg.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.create.RoundDimensionsManager;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;

import java.util.UUID;

@Service
public class TabToRoundMapper {
    @Autowired
    private RoundDimensionsManager roundDimensionsManager;

    public Round generateFromUIContent(RoundTab roundTab){
        return Round.builder()
                .id(UUID.randomUUID())
                .name(roundTab.getNewRoundNameTextField().getText())
                .firstStrikeCondition(roundTab.getFirstPrizeCondition().getValue())
                .secondStrikeCondition(roundTab.getSecondPrizeCondition().getValue())
                .thirdStrikeCondition(roundTab.getThirdPrizeCondition().getValue())
                .width(roundDimensionsManager.getBlankDimension(roundTab.getBlankDimensions()))
                .height(roundDimensionsManager.getBlankDimension(roundTab.getBlankDimensions()))
                .numberOfBlanks(Integer.parseInt(roundTab.getNumberOfBlanks().getText()))
                .audioTracks(roundTab.getEditAudioTracksTable().getAudioTracks())
                .build();
    }
}
