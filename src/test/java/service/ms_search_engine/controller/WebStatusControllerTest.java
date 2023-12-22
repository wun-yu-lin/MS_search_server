package service.ms_search_engine.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
public class WebStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Testing get web status.")
    public void test1getWebStatus() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/webStatus")
                .contentType("application/json");

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        System.out.println("Response:" + body);
    }
}