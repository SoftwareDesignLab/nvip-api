package org.nvip.api.serializers;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class VdoCharacteristicDTOTest {
    @Test
    public void testGettersAndSetters() {
        VdoCharacteristicDTO  vdoCharacteristicDTO = new  VdoCharacteristicDTO("old cveId","old vdoLabel",0,"old vdoNounGroup", 0, 0);
        vdoCharacteristicDTO.setCveId("new cveId");
        vdoCharacteristicDTO.setVdoLabel("new vdoLabel");
        vdoCharacteristicDTO.setVdoConfidence(1);
        vdoCharacteristicDTO.setVdoNounGroup("new vdoNounGroup");
        vdoCharacteristicDTO.setUserId(1);
        vdoCharacteristicDTO.setIsActive(1);

        assertEquals("new cveId",  vdoCharacteristicDTO.getCveId());
        assertEquals("new vdoLabel",  vdoCharacteristicDTO.getVdoLabel());
        assertEquals(1, vdoCharacteristicDTO.getVdoConfidence(),0.0003f);
        assertEquals("new vdoNounGroup",  vdoCharacteristicDTO.getVdoNounGroup());
        assertEquals(Optional.of(1),  Optional.of(vdoCharacteristicDTO.getUserId()) );
        assertEquals(1,   vdoCharacteristicDTO.getIsActive());



    }

    @Test
    public void testBuilder() {
        VdoCharacteristicDTO  vdoCharacteristicDTO = VdoCharacteristicDTO.builder()
                .cveId("new cveId")
                .vdoLabel("new vdoLabel")
                .vdoConfidence(1)
                .vdoNounGroup("new vdoNounGroup")
                .userId(1)
                .isActive(1)
                .build();

        assertEquals("new cveId",  vdoCharacteristicDTO.getCveId());
        assertEquals("new vdoLabel",  vdoCharacteristicDTO.getVdoLabel());
        assertEquals(1, vdoCharacteristicDTO.getVdoConfidence(),0.0003f);
        assertEquals("new vdoNounGroup",  vdoCharacteristicDTO.getVdoNounGroup());
        assertEquals(Optional.of(1),  Optional.of(vdoCharacteristicDTO.getUserId()) );
        assertEquals(1,   vdoCharacteristicDTO.getIsActive());

    }
}
