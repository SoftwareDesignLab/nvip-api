package org.nvip.api.serializers;

import jakarta.persistence.Basic;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class PatchCommitDTO {
    String cveId;
    String sourceUrl;
    String commitSha;
    String commitMessage;
    String uniDiff;
    String timeline;
    String timeToPatch;
    String commitDate;
    int linesChanged;
}
