package org.nvip.api.serializers;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductDTO {
    int productId;
    String productName;
    String domain;
    String cpe;
    String version;
}
