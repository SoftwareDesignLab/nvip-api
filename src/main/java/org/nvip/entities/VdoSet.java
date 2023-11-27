package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VdoSet {
    @Id private int vdoSetId;

    @Basic private LocalDateTime createdDate;

    private double cvssBaseScore;

    private int userId;
}
