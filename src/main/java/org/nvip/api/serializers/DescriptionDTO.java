package org.nvip.api.serializers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DescriptionDTO {
    String description;
    String createdDate;
    int isUserGenerated;
}