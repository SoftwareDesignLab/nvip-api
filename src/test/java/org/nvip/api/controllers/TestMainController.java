package org.nvip.api.controllers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainController.class)
@Disabled("Disabled until data can be inserted/mocked")
public class TestMainController {

    @Autowired
    MockMvc mockMvc;

    String testUri = "/";

    @Test
    public void testGetCrawlReportsReturnsSuccess(){
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
}
