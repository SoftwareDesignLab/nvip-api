package org.nvip.api.serializers;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class RawDescriptionDTOTest {
    @Test
    public void testGettersAndSetters() {
        RawDescriptionDTO rawDescriptionDTO = new RawDescriptionDTO("old cveid","old rawDescription","old createdDate","old publishedDate", "old lastModifiedDate",  "old sourceUrl",0,"old sourceType", "old parserType");
        rawDescriptionDTO.setCveId("new cveid");
        rawDescriptionDTO.setRawDescription("new rawDescription");
        rawDescriptionDTO.setCreatedDate("new createdDate");
        rawDescriptionDTO.setPublishedDate("new publishedDate");
        rawDescriptionDTO.setLastModifiedDate("new lastModifiedDate");
        rawDescriptionDTO.setSourceUrl("new sourceUrl");
        rawDescriptionDTO.setIsGarbage(1);
        rawDescriptionDTO.setSourceType("new sourceType");
        rawDescriptionDTO.setParserType("new parserType");

        assertEquals("new cveid",rawDescriptionDTO .getCveId());
        assertEquals("new rawDescription",rawDescriptionDTO .getRawDescription());
        assertEquals("new createdDate",rawDescriptionDTO .getCreatedDate());
        assertEquals("new publishedDate",rawDescriptionDTO .getPublishedDate());
        assertEquals("new lastModifiedDate",rawDescriptionDTO .getLastModifiedDate());
        assertEquals("new sourceUrl",rawDescriptionDTO .getSourceUrl());
        assertEquals(1,rawDescriptionDTO .getIsGarbage());
        assertEquals("new parserType",rawDescriptionDTO .getParserType());
    }

    @Test
    public void testBuilder() {
        RawDescriptionDTO rawDescriptionDTO = RawDescriptionDTO.builder()
                .cveId("new cveid")
                .rawDescription("new rawDescription")
                .createdDate("new createdDate")
                .publishedDate("new publishedDate")
                .lastModifiedDate("new lastModifiedDate")
                .sourceUrl("new sourceUrl")
                .isGarbage(1)
                .parserType("new parserType")
                .build();



        assertEquals("new cveid",rawDescriptionDTO .getCveId());
        assertEquals("new rawDescription",rawDescriptionDTO .getRawDescription());
        assertEquals("new createdDate",rawDescriptionDTO .getCreatedDate());
        assertEquals("new publishedDate",rawDescriptionDTO .getPublishedDate());
        assertEquals("new lastModifiedDate",rawDescriptionDTO .getLastModifiedDate());
        assertEquals("new sourceUrl",rawDescriptionDTO .getSourceUrl());
        assertEquals(1,rawDescriptionDTO .getIsGarbage());
        assertEquals("new parserType",rawDescriptionDTO .getParserType());
    }
}
