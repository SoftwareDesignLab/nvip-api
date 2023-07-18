package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Entity
public class RunHistory {
    @NonNull @Id @Column(name="runhistory_id") Integer id;

    @NonNull @Basic LocalDateTime runStartDate;
    @NonNull @Basic LocalDateTime runEndDate;

}
