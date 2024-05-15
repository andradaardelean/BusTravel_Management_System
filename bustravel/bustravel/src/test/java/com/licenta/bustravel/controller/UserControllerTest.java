package com.licenta.bustravel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.licenta.bustravel.DTO.ForgotPasswdDTO;
import com.licenta.bustravel.DTO.UserDTO;
import com.licenta.bustravel.DTO.UserSignUpDTO;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.service.CompanyService;
import com.licenta.bustravel.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private CompanyService companyService;

    @Test
    void testSignUp() throws Exception {
        UserSignUpDTO userDTO = UserSignUpDTO.builder()
            .username("testUser")
            .password("password")
            .name("Test User")
            .phone("123456789")
            .email("test@example.com")
            .userType("CLIENT")
            .build();

        UserEntity newUser = UserEntity.builder()
            .username(userDTO.getUsername())
            .password(userDTO.getPassword())
            .name(userDTO.getName())
            .phone(userDTO.getPhone())
            .email(userDTO.getEmail())
            .userType(UserType.CLIENT)
            .build();

        doNothing().when(userService).add(any(UserEntity.class));

        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string("Signed up successful!"));

        verify(userService).add(any(UserEntity.class));
    }

    @Test
    void testSignUpException() throws Exception {
        UserSignUpDTO userDTO = UserSignUpDTO.builder()
            .username("testUser")
            .password("password")
            .name("Test User")
            .phone("123456789")
            .email("test@example.com")
            .userType("CLIENT")
            .build();

        doThrow(new Exception("Sign up failed")).when(userService).add(any(UserEntity.class));

        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Sign up user does not work. Sign up failed"));
    }

    @Test
    void testLogout() throws Exception {
        String token = "validToken";
        when(jwtService.isTokenValid(token)).thenReturn(true);
        doNothing().when(jwtService).invalidateToken(token);

        mockMvc.perform(post("/api/user/logout")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(content().string("Logout successful!"));

        verify(jwtService).invalidateToken(token);
    }

    @Test
    void testLogoutInvalidToken() throws Exception {
        String token = "invalidToken";
        when(jwtService.isTokenValid(token)).thenReturn(false);

        mockMvc.perform(post("/api/user/logout")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Token already invalidated!"));
    }

    @Test
    void testGetUsersByCompany() throws Exception {
        String company = "Test Company";
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setName("Test User");
        user.setPhone("123456789");
        user.setEmail("test@example.com");
        user.setUserType(UserType.CLIENT);
        user.setCompanyEntity(null);

        when(userService.getUsersByCompany(company)).thenReturn(List.of(user));

        mockMvc.perform(get("/api/user/byCompany/{company}", company))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    void testGetUsersByCompanyException() throws Exception {
        String company = "Test Company";
        when(userService.getUsersByCompany(company)).thenThrow(new Exception("Company not found"));

        mockMvc.perform(get("/api/user/byCompany/{company}", company))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Get users by company does not work. Company not found"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setName("Test User");
        user.setPhone("123456789");
        user.setEmail("test@example.com");
        user.setUserType(UserType.CLIENT);
        user.setCompanyEntity(null);

        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/user")
                .header("Authorization", "Bearer validToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    void testGetAllUsersInvalidToken() throws Exception {
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        mockMvc.perform(get("/api/user")
                .header("Authorization", "Bearer invalidToken"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Token is not valid!"));
    }

    @Test
    void testModifyUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
            .username("testUser")
            .name("Test User")
            .phone("123456789")
            .email("test@example.com")
            .userType("CLIENT")
            .build();

        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(companyService.getByName(anyString())).thenReturn(null);
        doNothing().when(userService).modify(any(UserEntity.class));

        mockMvc.perform(put("/api/user")
                .header("Authorization", "Bearer validToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string("User modified successfully!"));
    }

    @Test
    void testModifyUserInvalidToken() throws Exception {
        UserDTO userDTO = UserDTO.builder().build();
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        mockMvc.perform(put("/api/user")
                .header("Authorization", "Bearer invalidToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Token is not valid!"));
    }

    @Test
    void testGetUserByUsername() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setName("Test User");
        user.setPhone("123456789");
        user.setEmail("test@example.com");
        user.setUserType(UserType.CLIENT);
        user.setCompanyEntity(null);

        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(userService.getByUsername(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/user/{username}", "testUser")
                .header("Authorization", "Bearer validToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    void testGetUserByUsernameInvalidToken() throws Exception {
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        mockMvc.perform(get("/api/user/{username}", "testUser")
                .header("Authorization", "Bearer invalidToken"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Token is not valid!"));
    }

    @Test
    void testDeleteUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");

        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(userService.getByUsername(anyString())).thenReturn(user);
        doNothing().when(userService).delete(any(UserEntity.class));

        mockMvc.perform(delete("/api/user/{username}", "testUser")
                .header("Authorization", "Bearer validToken"))
            .andExpect(status().isOk())
            .andExpect(content().string("User deleted successfully!"));
    }

    @Test
    void testDeleteUserInvalidToken() throws Exception {
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        mockMvc.perform(delete("/api/user/{username}", "testUser")
                .header("Authorization", "Bearer invalidToken"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Token is not valid!"));
    }

    @Test
    void testValidateToken() throws Exception {
        String token = "validToken";
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setName("Test User");
        user.setPhone("123456789");
        user.setEmail("test@example.com");
        user.setUserType(UserType.CLIENT);
        user.setCompanyEntity(null);

        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("testUser");
        when(userService.checkToken(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(get("/api/user/validateToken/{token}", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    void testValidateTokenInvalid() throws Exception {
        String token = "invalidToken";
        when(jwtService.isTokenValid(token)).thenReturn(false);

        mockMvc.perform(get("/api/user/validateToken/{token}", token))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Token is not valid!"));
    }

}
