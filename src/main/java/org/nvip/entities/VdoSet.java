package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VdoSet {
    @Id private int vdoSetId;

    @Basic private LocalDateTime createdDate;

    @ToString.Exclude
    @OneToMany
    @JoinColumn(name="vdo_set_id", referencedColumnName = "vdoSetId")
    List<VdoCharacteristic> vdoCharacteristics;

    private double cvssBaseScore;

    private int userId;
}
