package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="nvdmitrestatus")
@Getter
@Setter
@ToString
public class NvdMitreStatus {
    @Id @Column(name="nvdmitrestatus_id")
    private int nvdMitreStatusId;

    @Basic private LocalDateTime createdDate;

    @OneToOne @JoinColumn(name = "cve_id", referencedColumnName = "cveId")
    private Vulnerability vulnerability;

    private int existsNvd;

    private int existsMitre;
}
