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
        when(handler.createOwner(any())).thenReturn(new UserResponseDto(1L, "Ana", "Admin", "ana@example.com", 2L, "OWNER"));
        mvc.perform(post("/users/owners")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.role").value("OWNER"));
    }

    @Test void invalidFieldsReturnBadRequest() throws Exception {
        mvc.perform(post("/users/owners")
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
        mvc.perform(post("/users/owners").contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isUnauthorized());

        mvc.perform(post("/users/owners")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OWNER")))
                        .contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void onlyOwnerCreatesEmployees() throws Exception {
        when(handler.createEmployee(any())).thenReturn(
                new UserResponseDto(9L, "Luis", "Pérez", "luis@example.com", 3L, "EMPLOYEE"));

        mvc.perform(post("/users/employees")
                        .with(jwt().jwt(token -> token.subject("7").claim("role", "OWNER"))
                                .authorities(new SimpleGrantedAuthority("ROLE_OWNER")))
                        .contentType(MediaType.APPLICATION_JSON).content(employeeBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("EMPLOYEE"));

        mvc.perform(post("/users/employees")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON).content(employeeBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void createsCustomerWithoutAuthentication() throws Exception {
        when(handler.createCustomer(any())).thenReturn(
                new UserResponseDto(10L, "Laura", "Gómez", "laura@example.com", 4L, "CUSTOMER"));

        mvc.perform(post("/users/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }

    @Test
    void validatesCustomerRequest() throws Exception {
        mvc.perform(post("/users/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.roleId").exists());
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor adminJwt() {
        return jwt().jwt(token -> token.subject("1").claim("role", "ADMIN"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private String validBody() {
        return """
                {"name":"Ana","lastName":"Admin","identityDocument":"123456","cellphone":"+573001234567",
                 "birthDate":"2000-01-01","email":"ana@example.com","password":"secret"}
                """;
    }

    private String employeeBody() {
        return """
                {"name":"Luis","lastName":"Pérez","identityDocument":"987654","cellphone":"3001234567",
                 "email":"luis@example.com","password":"secret","roleId":3,"restaurantId":5}
                """;
    }

    private String customerBody() {
        return """
                {"name":"Laura","lastName":"Gómez","identityDocument":"456789","cellphone":"3004567890",
                 "email":"laura@example.com","password":"secret","roleId":4}
                """;
    }
}
