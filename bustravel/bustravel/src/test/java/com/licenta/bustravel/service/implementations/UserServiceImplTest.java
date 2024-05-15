package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.email.EmailSender;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdd() throws Exception {
        UserEntity user = new UserEntity() {
            @Override
            public boolean isValid(String phone, String email) {
                return true;  // Stubbed isValid method to always return true
            }
        };
        user.setPhone("123456789");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setUsername("testuser");

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        // Mock the static method saveToOAuth
//        try (MockedStatic<UserServiceImpl> mockedStatic = mockStatic(UserServiceImpl.class)) {
//            mockedStatic.when(() -> UserServiceImpl.saveToOAuth(any(UserEntity.class))).thenReturn("oauthId");
//
//            userService.add(user);
//
//            verify(userRepository).save(user);
//            assertEquals("encodedPassword", user.getPassword());
//            assertEquals("oauthId", user.getOauthId());
//        }
    }

    @Test
    void testGetById() throws Exception {
        UserEntity user = new UserEntity();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.getById(1);

        verify(userRepository).findById(1);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testGetByUsername() {
        UserEntity user = new UserEntity();
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        UserEntity result = userService.getByUsername("username");

        verify(userRepository).findByUsername("username");
        assertEquals(user, result);
    }

    @Test
    void testModify() throws Exception {
        UserEntity currentUser = new UserEntity();
        currentUser.setUsername("currentUser");
        currentUser.setUserType(UserType.ADMIN);
        when(authentication.getName()).thenReturn("currentUser");
        when(userRepository.findByUsername("currentUser")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("userToModify")).thenReturn(Optional.of(new UserEntity()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = new UserEntity();
        user.setUsername("userToModify");

        userService.modify(user);

        verify(userRepository).save(user);
    }

    @Test
    void testDelete() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("username");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        userService.delete(user);

        verify(userRepository).delete(user);
    }

    @Test
    void testGetAll() throws Exception {
        List<UserEntity> users = List.of(new UserEntity());
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAll();

        verify(userRepository).findAll();
        assertEquals(users, result);
    }

    @Test
    void testGetUsersByCompany() {
        List<UserEntity> users = List.of(new UserEntity());
        when(userRepository.findByCompany("company")).thenReturn(users);

        List<UserEntity> result = userService.getUsersByCompany("company");

        verify(userRepository).findByCompany("company");
        assertEquals(users, result);
    }

    @Test
    void testForgotPassword() throws Exception {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        userService.forgotPassword("username", "test@example.com", "token");

        verify(userRepository).save(user);
        assertEquals("token", user.getToken());
    }

    @Test
    void testCheckToken() {
        UserEntity user = new UserEntity();
        user.setToken("token");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        UserEntity result = userService.checkToken("username", "token");

        assertNotNull(result);
    }

    @Test
    void testChangePassword() throws Exception {
        UserEntity user = new UserEntity();
        user.setToken("token");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        userService.changePassword("username", "newPassword", "token");

        verify(userRepository).save(user);
        assertEquals("encodedPassword", user.getPassword());
        assertNull(user.getToken());
    }
}
