package ru.orthodox.mbbg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import java.util.List;
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
    private List<BlankItem> blankItems;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @JsonIgnore
    private Double progress;


}