package ru.orthodox.mbbg.model.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.orthodox.mbbg.enums.BlanksStatus;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game implements MarkedWithId {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name="blanksStatus")
    private BlanksStatus blanksStatus = BlanksStatus.ABSENT;

    @Column(name = "roundIds")
    private List<UUID> roundIds;

    @JsonIgnore
    private boolean havingUnsavedChanges = false;

    @JsonIgnore
    private List<Round> rounds = new ArrayList<>();

}
