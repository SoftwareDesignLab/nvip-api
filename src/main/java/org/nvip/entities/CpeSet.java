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
public class CpeSet {
    @Id
    private int cpeSetId;

    @Basic
    private LocalDateTime createdDate;

    private int userId;
}
