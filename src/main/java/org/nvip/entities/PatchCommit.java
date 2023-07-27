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
    private int sourceUrlId;
    @ManyToOne private Vulnerability vulnerability;
    private String commitSha;
    private String commitMessage;
    private String uniDiff;
    private String timeline;
    private String timeToPatch;
    private int linesChanged;
    @Basic LocalDateTime commitDate;
}
