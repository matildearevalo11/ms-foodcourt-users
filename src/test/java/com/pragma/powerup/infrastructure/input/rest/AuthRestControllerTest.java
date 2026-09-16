package com.pragma.powerup.infrastructure.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pragma.powerup.application.dto.response.LoginResponseDto;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.infrastructure.configuration.SecurityConfiguration;
import com.pragma.powerup.infrastructure.exceptionhandler.ControllerAdvisor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthRestController.class)
@Import({ControllerAdvisor.class, SecurityConfiguration.class})
class AuthRestControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean IAuthHandler handler;

    @Test
    void loginIsPublicAndReturnsToken() throws Exception {
        when(handler.login(any())).thenReturn(new LoginResponseDto("jwt", "Bearer"));
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"owner@example.com\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt"));
    }

    @Test
    void rejectsInvalidLoginRequest() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
