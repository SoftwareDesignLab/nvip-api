package org.nvip.api.serializers;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class PatchCommitDTOTest {
    @Test
    public void testGettersAndSetters() {
        PatchCommitDTO patchCommitDTO = new PatchCommitDTO("old cveid","old name","old commitSha", "old commitMessage","old uniDiff","old timeline","old timeToPatch","old commitDate",0);
        patchCommitDTO.setCveId("new cveid");
        patchCommitDTO.setSourceUrl("new SourceUrl");
        patchCommitDTO.setCommitSha("new commitSha");
        patchCommitDTO.setCommitMessage("new commitMessage");
        patchCommitDTO.setUniDiff("new UniDiff");
        patchCommitDTO.setTimeline("new Timeline");
        patchCommitDTO.setTimeToPatch("new timeToPatch");
        patchCommitDTO.setCommitDate("new CommitDate");
        patchCommitDTO.setLinesChanged(1);

        assertEquals("new cveid",patchCommitDTO.getCveId());
        assertEquals("new SourceUrl",patchCommitDTO.getSourceUrl());
        assertEquals("new commitSha",patchCommitDTO.getCommitSha());
        assertEquals("new commitMessage",patchCommitDTO.getCommitMessage());
        assertEquals("new UniDiff",patchCommitDTO.getUniDiff());
        assertEquals("new Timeline",patchCommitDTO.getTimeline());
        assertEquals("new timeToPatch",patchCommitDTO.getTimeToPatch());
        assertEquals("new CommitDate",patchCommitDTO.getCommitDate());
        assertEquals(1,patchCommitDTO.getLinesChanged());



    }

    @Test
    public void testBuilder() {
        PatchCommitDTO patchCommitDTO = PatchCommitDTO.builder()
                        .cveId("new cveid")
                        .sourceUrl("new SourceUrl")
                        .commitSha("new commitSha")
                        .commitMessage("new commitMessage")
                        .uniDiff("new UniDiff")
                        .timeline("new Timeline")
                        .timeToPatch("new timeToPatch")
                        .commitDate("new CommitDate")
                        .linesChanged(1)
                        .build();



        assertEquals("new cveid",patchCommitDTO.getCveId());
        assertEquals("new SourceUrl",patchCommitDTO.getSourceUrl());
        assertEquals("new commitSha",patchCommitDTO.getCommitSha());
        assertEquals("new commitMessage",patchCommitDTO.getCommitMessage());
        assertEquals("new UniDiff",patchCommitDTO.getUniDiff());
        assertEquals("new Timeline",patchCommitDTO.getTimeline());
        assertEquals("new timeToPatch",patchCommitDTO.getTimeToPatch());
        assertEquals("new CommitDate",patchCommitDTO.getCommitDate());
        assertEquals(1,patchCommitDTO.getLinesChanged());
    }
}
