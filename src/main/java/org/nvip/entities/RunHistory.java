package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name="runhistory")
@Entity
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RunHistory {
    @Column (name="runhistory_id")
    @Id private int runhistory__Id;

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
