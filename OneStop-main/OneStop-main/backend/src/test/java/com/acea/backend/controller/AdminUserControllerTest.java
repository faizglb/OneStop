package com.acea.backend.controller;

import com.acea.backend.domain.enums.Role;
import com.acea.backend.dto.admin.AdminUserResponseDto;
import com.acea.backend.service.AdminUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcStatusResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserService adminUserService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/admin/users should return 200 OK for ADMIN role")
    void getUsers_asAdmin_shouldReturnOk() throws Exception {
        AdminUserResponseDto dto = AdminUserResponseDto.builder()
            .id(UUID.randomUUID())
            .email("student@acea.com")
            .firstName("John")
            .lastName("Doe")
            .role(Role.STUDENT)
            .accountTier("FREE")
            .active(true)
            .suspended(false)
            .createdAt(Instant.now())
            .build();

        given(adminUserService.getUsers(any(), any(), any(), any()))
            .willReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/v1/admin/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].email").value("student@acea.com"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("GET /api/v1/admin/users should return 403 Forbidden for STUDENT role")
    void getUsers_asStudent_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
            .andExpect(status().isForbidden());
    }
}
