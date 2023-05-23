package org.nvip.api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class TestReviewController {

    @Autowired
    MockMvc mockMvc;

    String testUri = "/reviews";

    @Test
    public void testSearchForReviewsReturnsSuccess(){
        try {
            mockMvc.perform(MockMvcRequestBuilders
                    .get(testUri)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()
            );
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSearchForReviewByCveIdReturnsSuccess(){
        String cveId = "CVE-1234-1234";
        try {
            mockMvc.perform(MockMvcRequestBuilders
                            .get(testUri+"?cveId="+cveId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()
                    );
        } catch (Exception e) {
            fail();
        }
    }
}
