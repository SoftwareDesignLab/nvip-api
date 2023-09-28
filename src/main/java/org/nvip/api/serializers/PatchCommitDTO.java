package org.nvip.api.serializers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
