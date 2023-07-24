package org.nvip.api.serializers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CvssDTO {
    String cveId;
    String baseScore;
    // double severityConfidence;
    String impactScore;
    // double impactConfidence;
}
