package com.bookify.bookify_app;

// ********************************************************************************************
// * GlobalExceptionHandlerTest verifies the standardized error response (RFC 7807).          *
// * WHAT is tested:                                                                          *
// *  - GET /hej?crash=true returns 500 Internal Server Error                                 *
// *  - Content-Type is "application/problem+json"                                            *
// *  - $.title = "Internal Server Error"                                                     *
// *  - $.correlationId is present                                                            *
// * WHY: Ensures clients always receive a consistent error contract and traceability         *
// * through correlationId, allowing errors to be linked to specific requests in logs.        *
// ********************************************************************************************

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnProblemJsonOnException() throws Exception {
        mockMvc.perform(get("/hej?crash=true"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.correlationId").exists());
    }
}

