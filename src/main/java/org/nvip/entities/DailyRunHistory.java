package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity
@Table(name="dailyrunhistory")
public class DailyRunHistory {
    @NonNull @Id @Column(name="run_id") Integer id;

    @NonNull LocalDate runDateTime;
    @NonNull Integer notInNvdCount;
    @NonNull Integer notInMitreCount;
    @NonNull Double avgTimeGapNvd;
    @NonNull Double avgTimeGapMitre;
    @NonNull Integer addedCveCount;
    @NonNull Integer updatedCveCount;

    @OneToMany(mappedBy="dailyRunHistory")
    List<VulnerabilityUpdate> vulnerabilityUpdates = new ArrayList<>();
}
