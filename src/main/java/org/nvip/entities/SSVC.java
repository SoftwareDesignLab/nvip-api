package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.type.NumericBooleanConverter;

@Entity
@Table(name="ssvc")
@Getter
@Setter
@ToString
public class SSVC {
    @Id private int id;
    @OneToOne @JoinColumn(name="cve_id", referencedColumnName="cveId")
    private Vulnerability vulnerability;
    @Convert(converter = NumericBooleanConverter.class)
    private boolean automatable;
    private String exploitStatus;
    @Convert(converter = NumericBooleanConverter.class)
    private boolean technicalImpact;
}
