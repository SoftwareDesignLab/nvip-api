package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="ssvc")
@Getter
@Setter
@ToString
public class SSVC {
    @Id private int id;
    @OneToOne @JoinColumn(name="cve_id", referencedColumnName="cveId")
    private Vulnerability vulnerability;
    @Column(columnDefinition = "BIT", length = 1)
    private boolean automatable;
    private String exploitStatus;
    @Column(columnDefinition = "BIT", length = 1)
    private boolean technicalImpact;
}
