package service.ms_search_engine.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
class SpectrumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get spectrum by ID, normal case")
    public void testGetSpectrumByID() throws Exception {

        //建立一個RequestBuilder, 可以發起的request與相關的設定
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/spectrum/1")
                .contentType("application/json");

        //發起request, 並驗證回傳的狀態碼是否為200(ok)
        MvcResult mvcResult =  mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        System.out.println("Response:" + body);
    }

    @Test
    @DisplayName("Get spectrum by parameter, normal case.")
    public void test1GetSpectrumByParameter() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/spectrum")
                .param("spectrumInit", "0")
                .param("spectrumOffSet", "10")
                .param("compoundName", "Caffeine")
                .contentType("application/json");
        MvcResult mvcResult = mockMvc.perform((requestBuilder))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        System.out.println("Response:" + body);
    }

    @Test
    @DisplayName("Get spectrum by fuzzy, normal case.")
    public void test1GetSpectrumByFuzzy() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/spectrum/fuzzy")
                .param("keyWord", "tetracycline")
                .contentType("application/json");
        MvcResult mvcResult = mockMvc.perform((requestBuilder))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        System.out.println("Response:" + body);
    }

    @Test
    @DisplayName("Get spectrum by fuzzy, error case.")
    public void test2GetSpectrumByFuzzy() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/spectrum/fuzzy")
                .contentType("application/json");
        MvcResult mvcResult = mockMvc.perform((requestBuilder))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        System.out.println("Response:" + body);
    }

}