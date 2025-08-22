package com.bookify.bookify_app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Basic Integration test for HelloControllerTest
 *
 * We use MockMvc to call the endpoint /Hej and to verify:
 * - HTTP 200 Ok
 * - Respond should be exactly "hej från backend!"
 */

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void hejEndpointShouldReturnCorrectMessage() throws Exception {
        mockMvc.perform(get("/hej"))
                .andExpect(status().isOk())
                .andExpect(content().string("hej från backend!"));
    }
}




