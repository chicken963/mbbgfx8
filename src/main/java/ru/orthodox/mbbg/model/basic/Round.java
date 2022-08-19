package ru.orthodox.mbbg.model.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.orthodox.mbbg.enums.WinCondition;

import javax.persistence.Column;
import java.util.*;

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

    @Column(name = "blanksIds")
    private List<UUID> tracksIds;

    @Column(name = "tracksIds")
    private List<UUID> blanksIds;

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

    @Column(name = "numberOfBlanks")
    private Integer numberOfBlanks;

    @JsonIgnore
    private List<AudioTrack> audioTracks = new ArrayList<>();

    @JsonIgnore
    private List<Blank> blanks = new ArrayList<>();

    @JsonIgnore
    private Set<AudioTrack> playedAudiotracks = new HashSet<>();

    @JsonIgnore
    private AudioTrack previousTrack;

    @JsonIgnore
    private AudioTrack currentTrack;

    @JsonIgnore
    private AudioTrack nextTrack;

    @JsonIgnore
    private WinCondition currentTargetWinCondition = WinCondition.ONE_LINE_STRIKE;


}
