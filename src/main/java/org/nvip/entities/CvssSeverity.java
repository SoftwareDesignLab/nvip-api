package org.nvip.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cvssseverity")
public class CvssSeverity {
    @Id @Column(name="cvss_severity_id")
    @Getter @Setter int id;
    @Getter @Setter String cvssSeverityClass;
}
