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
    @NonNull @Id int runhistoryId;

    @NonNull @Basic LocalDateTime runStartDate;
    @NonNull @Basic LocalDateTime runEndDate;

}
