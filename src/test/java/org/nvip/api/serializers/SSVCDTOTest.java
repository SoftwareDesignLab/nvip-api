package org.nvip.api.serializers;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class SSVCDTOTest {

    SSVCScoreDTO score = new SSVCScoreDTO("low","medium","high");
    SSVCScoreDTO newScore = new SSVCScoreDTO("new low","medium","high");
    @Test
    public void testGettersAndSetters() {
        SSVCDTO sSVCDTO = new  SSVCDTO("old cveid",score,false,"old exploitStatus", false);
        sSVCDTO.setCveId("new cveid");
        sSVCDTO.setScores(newScore);
        sSVCDTO.setExploitStatus("new exploitStatus");



        assertEquals("new cveid",  sSVCDTO.getCveId());
        assertEquals(newScore,  sSVCDTO.getScores());
        assertEquals("new exploitStatus",  sSVCDTO.getExploitStatus());
    }

    @Test
    public void testBuilder() {
        SSVCDTO sSVCDTO = SSVCDTO.builder()
                .cveId("new cveid")
                .scores(newScore)
                .exploitStatus("new exploitStatus").build();

        assertEquals("new cveid",  sSVCDTO.getCveId());
        assertEquals(newScore,  sSVCDTO.getScores());
        assertEquals("new exploitStatus",  sSVCDTO.getExploitStatus());
    }
}
