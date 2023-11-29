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
public class CpeSet {
    @Id
    private int cpeSetId;

    @Basic
    private LocalDateTime createdDate;

    @ToString.Exclude
    @OneToMany
    @JoinColumn(name="cpe_set_id", referencedColumnName = "vdoSetId")
    List<AffectedProduct> affectedProducts;

    private int userId;

    public CpeSet(LocalDateTime createdDate, List<AffectedProduct> affectedProducts, int userId) {
        this.createdDate = createdDate;
        this.affectedProducts = affectedProducts;
        this.userId = userId;
    }
}
