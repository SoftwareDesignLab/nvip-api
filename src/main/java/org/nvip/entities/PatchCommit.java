package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="patchcommit")
@Getter
@Setter
@ToString
public class PatchCommit {
    @Id private int commitId;
    @OneToOne(optional = false) @JoinColumn(name="source_url_id", referencedColumnName="sourceUrlId")
    private PatchSourceUrl sourceUrl;
    @ManyToOne @JoinColumn(name="cve_id", referencedColumnName="cveId")
    private Vulnerability vulnerability;
    private String commitSha;
    private String commitMessage;
    private String uniDiff;
    private String timeline;
    private String timeToPatch;
    private int linesChanged;
    @Basic LocalDateTime commitDate;
}
