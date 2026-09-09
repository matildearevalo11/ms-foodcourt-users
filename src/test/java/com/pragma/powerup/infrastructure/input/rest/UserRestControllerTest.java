package com.pragma.powerup.infrastructure.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.dto.response.UserRoleResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.infrastructure.exceptionhandler.ControllerAdvisor;
import com.pragma.powerup.infrastructure.configuration.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@WebMvcTest(UserRestController.class)
@Import({ControllerAdvisor.class, SecurityConfiguration.class})
class UserRestControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean IUserHandler handler;
    @Test void createsOwner() throws Exception {
        when(handler.createUser(any())).thenReturn(new UserResponseDto(1L, "Ana", "Admin", "ana@example.com", 2L, "OWNER"));
        mvc.perform(post("/users")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.role").value("OWNER"));
    }

    @Test void invalidFieldsReturnBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").isMap());
    }

    @Test void returnsTheUsersRole() throws Exception {
        when(handler.getUserRole(7L)).thenReturn(new UserRoleResponseDto(7L, "OWNER"));

        mvc.perform(get("/users/7/role").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("OWNER"));
    }

    @Test
    void onlyAdministratorCreatesOwners() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isUnauthorized());

        mvc.perform(post("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OWNER")))
                        .contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isForbidden());
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor adminJwt() {
        return jwt().jwt(token -> token.subject("1").claim("role", "ADMIN"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private String validBody() {
        return """
                {"name":"Ana","lastName":"Admin","identityDocument":"123456","cellphone":"+573001234567",
                 "birthDate":"2000-01-01","email":"ana@example.com","password":"secret","roleId":2}
                """;
    }
}
