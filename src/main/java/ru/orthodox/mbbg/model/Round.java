package ru.orthodox.mbbg.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import ru.orthodox.mbbg.enums.WinCondition;
import ru.orthodox.mbbg.services.model.AudioTrackService;

import javax.annotation.PostConstruct;
import javax.persistence.Column;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Round {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "tracksIds")
    private List<UUID> tracksIds;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "firstStrikeCondition")
    private WinCondition firstStrikeCondition;

    @Column(name = "secondStrikeCondition")
    private WinCondition secondStrikeCondition;

    @Column(name = "thirdStrikeCondition")
    private WinCondition thirdStrikeCondition;


}
