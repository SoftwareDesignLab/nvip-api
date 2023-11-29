package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Table(name="runhistory")
@Entity
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
public class RunHistory {
    @Id @Column (name="runhistory_id")
    private int runhistoryId;
    @Basic private LocalDateTime runDateTime;
    private int totalCveCount;
    private int newCveCount;
    private int updatedCveCount;
    private int notInNvdCount;
    private int notInMitreCount;
    private int notInBothCount;
    private double avgTimeGapNvd;
    private double avgTimeGapMitre;
}
