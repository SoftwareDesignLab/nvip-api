package org.nvip.api.serializers;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
public class CvssDTOTest {

    @Test
    public void testGettersAndSetters() {
        CvssDTO cvssDTO = new CvssDTO("CVE-2343",3.0);
        cvssDTO.setCveId("CVE-1234");
        cvssDTO.setBaseScore(5.0);

        assertEquals("CVE-1234", cvssDTO.getCveId());
        assertEquals(5.0, cvssDTO.getBaseScore(),0.0003f);
    }

    @Test
    public void testBuilder() {
        CvssDTO cvssDTO = CvssDTO.builder()
                .cveId("CVE-1234")
                .baseScore(5.0)
                .build();

        assertEquals("CVE-1234", cvssDTO.getCveId());
        assertEquals(5.0, cvssDTO.getBaseScore(),0.0003f);
    }
}
