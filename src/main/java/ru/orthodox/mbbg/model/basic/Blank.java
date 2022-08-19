package ru.orthodox.mbbg.model.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Blank implements MarkedWithId {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "number")
    private String number;

    @Column(name = "blankItems")
    private Set<BlankItem> blankItems;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @JsonIgnore
    private Double progress;

    @JsonIgnore
    private Button miniatureButton;

    @JsonIgnore
    private Double nextProgress;

    @JsonIgnore
    private Set<BlankItem> winningSet = new HashSet<>();
}