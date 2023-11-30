package org.nvip.api.serializers;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class DescriptionDTOTest {

    @Test
    public void testGettersAndSetters() {
        DescriptionDTO descriptionDTO = new DescriptionDTO("old description","old createdDate",0);
        descriptionDTO.setDescription("New Description");
        descriptionDTO.setCreatedDate("New CreatedDate");
        descriptionDTO.setIsUserGenerated(1);

        assertEquals("New Description", descriptionDTO.getDescription());
        assertEquals("New CreatedDate",descriptionDTO.getCreatedDate());
        assertEquals(1,descriptionDTO.getIsUserGenerated());
    }

    @Test
    public void testBuilder() {
        DescriptionDTO descriptionDTO = DescriptionDTO.builder()
                .description("New Description")
                .createdDate("New CreatedDate")
                .isUserGenerated(1)
                .build();

        assertEquals("New Description", descriptionDTO.getDescription());
        assertEquals("New CreatedDate",descriptionDTO.getCreatedDate());
        assertEquals(1,descriptionDTO.getIsUserGenerated());
    }
}
