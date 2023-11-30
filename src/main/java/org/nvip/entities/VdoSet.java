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
@Table(name = "vdoset")
public class VdoSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vdoSetId;

    @Basic private LocalDateTime createdDate;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="vdo_set_id", referencedColumnName = "vdoSetId")
    List<VdoCharacteristic> vdoCharacteristics;

    @OneToOne(mappedBy = "vdoSet")
    VulnerabilityVersion vulnerabilityVersion;

    private double cvssBaseScore;

    private Integer userId;

    private String cveId;

    public VdoSet(LocalDateTime createdDate, List<VdoCharacteristic> vdoCharacteristics, double cvssBaseScore, int userId, String cveId) {
        this.createdDate = createdDate;
        this.vdoCharacteristics = vdoCharacteristics;
        this.cvssBaseScore = cvssBaseScore;
        this.userId = userId;
        this.cveId = cveId;
    }
}