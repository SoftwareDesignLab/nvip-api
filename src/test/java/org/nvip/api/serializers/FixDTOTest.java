package org.nvip.api.serializers;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class FixDTOTest {
    @Test
    public void testGettersAndSetters() {
        FixDTO fixDTO = new  FixDTO("old cveid","old sourceUrl","old fixDescription");
        fixDTO.setCveId("new cveid");
        fixDTO.setSourceUrl("new sourceUrl");
        fixDTO.setFixDescription("new fixDescription");

        assertEquals("new cveid",  fixDTO.getCveId());
        assertEquals("new sourceUrl",  fixDTO.getSourceUrl());
        assertEquals("new fixDescription",  fixDTO.getFixDescription());
    }

    @Test
    public void testBuilder() {
        FixDTO  fixDTO =  new FixDTO.FixDTOBuilder()
                .cveId("new cveid")
                .sourceUrl("new sourceUrl")
                .fixDescription("new fixDescription")
                .build();

        assertEquals("new cveid",  fixDTO.getCveId());
        assertEquals("new sourceUrl",  fixDTO.getSourceUrl());
        assertEquals("new fixDescription",  fixDTO.getFixDescription());
    }
}
