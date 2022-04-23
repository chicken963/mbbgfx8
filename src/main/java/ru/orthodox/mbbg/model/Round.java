package ru.orthodox.mbbg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.orthodox.mbbg.enums.WinCondition;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Round implements MarkedWithId {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "tracksIds")
    private List<UUID> tracksIds;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "firstStrikeCondition")
    private WinCondition firstStrikeCondition;

    @Column(name = "secondStrikeCondition")
    private WinCondition secondStrikeCondition;

    @Column(name = "thirdStrikeCondition")
    private WinCondition thirdStrikeCondition;

    @JsonIgnore
    private List<AudioTrack> audioTracks = new ArrayList<>();


}
