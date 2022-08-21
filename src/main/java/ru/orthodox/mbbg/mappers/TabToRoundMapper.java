package ru.orthodox.mbbg.mappers;

import org.springframework.stereotype.Service;
import ru.orthodox.mbbg.model.basic.Round;
import ru.orthodox.mbbg.services.create.RoundDimensionsDealer;
import ru.orthodox.mbbg.model.proxy.play.RoundTab;

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
                .audioTracks(roundTab.getEditAudioTracksTable().getAudioTracks())
                .build();
    }
}
