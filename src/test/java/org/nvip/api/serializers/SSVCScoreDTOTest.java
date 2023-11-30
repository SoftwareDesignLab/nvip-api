package org.nvip.api.serializers;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class SSVCScoreDTOTest {

    @Test
    public void testGettersAndSetters() {
        SSVCScoreDTO score = new SSVCScoreDTO("old low","old medium","old high");
        score.setScoreLow("new low");
        score.setScoreHigh("new high");
        score.setScoreMedium("new Medium");



        assertEquals("new low",  score.getScoreLow());
        assertEquals("new high",   score.getScoreHigh());
        assertEquals("new Medium",   score.getScoreMedium());
    }

    @Test
    public void testBuilder() {
        SSVCScoreDTO score = SSVCScoreDTO .builder()
                .scoreHigh("new high")
                .scoreMedium("new medium")
                .scoreLow("new Low").build();

        assertEquals("new Low",  score.getScoreLow());
        assertEquals("new high",   score.getScoreHigh());
        assertEquals("new medium",   score.getScoreMedium());
    }

    @Test
    public void testGet() {
        SSVCScoreDTO score = SSVCScoreDTO .builder()
                .scoreHigh("new high")
                .scoreMedium("new medium")
                .scoreLow("new Low").build();

        assertEquals("new Low",  score.get(0));
        assertEquals("new high",   score.get(2));
        assertEquals("new medium",   score.get(1));

    }
}
