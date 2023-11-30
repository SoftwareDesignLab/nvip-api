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
@Table(name = "cpeset")
public class CpeSet {
    @Id
    private int cpeSetId;

    @Basic
    private LocalDateTime createdDate;

    @ToString.Exclude
    @OneToMany(mappedBy = "cpeSet", cascade = CascadeType.ALL)
//    @JoinColumn(name="cpe_set_id", referencedColumnName = "cpeSetId")
    List<AffectedProduct> affectedProducts;

    private Integer userId;

    private String cveId;

    public CpeSet(LocalDateTime createdDate, List<AffectedProduct> affectedProducts, int userId) {
        this.createdDate = createdDate;
        this.affectedProducts = affectedProducts;
        this.userId = userId;
    }
}
