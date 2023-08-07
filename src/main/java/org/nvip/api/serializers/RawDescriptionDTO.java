package org.nvip.api.serializers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RawDescriptionDTO {
    String cveId;
    String rawDescription;
    String createdDate;
    String publishedDate;
    String lastModifiedDate;
    String sourceUrl;
    int isGarbage;
    String sourceType;
    String parserType;
}
