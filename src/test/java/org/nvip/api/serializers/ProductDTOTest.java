package org.nvip.api.serializers;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class ProductDTOTest {
    @Test
    public void testGettersAndSetters() {
        ProductDTO  productDTO = new   ProductDTO(0,"old productName","old domain","old cpe", "old version", "old purl");
        productDTO.setProductId(1);
        productDTO.setProductName("new productName");
        productDTO.setDomain("new domain");
        productDTO.setCpe("new cpe");
        productDTO.setVersion("new version");
        productDTO.setPurl("new purl");


        assertEquals(1,  productDTO.getProductId());
        assertEquals("new productName",  productDTO.getProductName());
        assertEquals("new domain",  productDTO.getDomain());
        assertEquals("new cpe",  productDTO.getCpe());
        assertEquals("new version",  productDTO.getVersion());
        assertEquals("new purl",  productDTO.getPurl());



    }

    @Test
    public void testBuilder() {
        ProductDTO  productDTO = ProductDTO.builder()
                .productId(1)
                .productName("new productName")
                .domain("new domain")
                .cpe("new cpe")
                .version("new version")
                .purl("new purl")
                .build();

        assertEquals(1,  productDTO.getProductId());
        assertEquals("new productName",  productDTO.getProductName());
        assertEquals("new domain",  productDTO.getDomain());
        assertEquals("new cpe",  productDTO.getCpe());
        assertEquals("new version",  productDTO.getVersion());
        assertEquals("new purl",  productDTO.getPurl());
    }
}
