package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.email.EmailSender;
import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.CompanyRepository;
import com.licenta.bustravel.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyServiceImplTest {

//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private CompanyRepository companyRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private CompanyServiceImpl companyService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testAdd() throws Exception {
//        CompanyEntity companyEntity = mock(CompanyEntity.class);
//        when(companyEntity.getOwnerEmail()).thenReturn("owner@test.com");
//        when(companyEntity.getOwnerName()).thenReturn("Owner Name");
//        when(companyEntity.getPhone()).thenReturn("123456789");
//        when(companyEntity.isValid(anyString(), anyString())).thenReturn(true);
//
//        UserEntity adminUser = new UserEntity();
//        adminUser.setUserType(UserType.ADMIN);
//
//        Authentication authentication = mock(Authentication.class);
//        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        when(authentication.getName()).thenReturn("admin");
//        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//        when(companyRepository.save(any(CompanyEntity.class))).thenReturn(companyEntity);
//
//        companyService.add(companyEntity);
//
//        verify(companyRepository).save(companyEntity);
//        verify(userRepository).save(any(UserEntity.class));
//    }
//
//    @Test
//    void testAddNotAdmin() throws Exception {
//        CompanyEntity companyEntity = mock(CompanyEntity.class);
//        when(companyEntity.getOwnerEmail()).thenReturn("owner@test.com");
//        when(companyEntity.getOwnerName()).thenReturn("Owner Name");
//        when(companyEntity.getPhone()).thenReturn("123456789");
//        when(companyEntity.isValid(anyString(), anyString())).thenReturn(true);
//
//        UserEntity nonAdminUser = new UserEntity();
//        nonAdminUser.setUserType(UserType.COMPANYEMPLOYEE);
//
//        Authentication authentication = mock(Authentication.class);
//        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        when(authentication.getName()).thenReturn("user");
//        when(userRepository.findByUsername("user")).thenReturn(Optional.of(nonAdminUser));
//
//        Exception exception = assertThrows(Exception.class, () -> {
//            companyService.add(companyEntity);
//        });
//
//        assertEquals("Not allowed.", exception.getMessage());
//        verify(companyRepository, never()).save(any(CompanyEntity.class));
//    }
//
//    @Test
//    void testGetById() throws Exception {
//        CompanyEntity companyEntity = new CompanyEntity();
//        when(companyRepository.findById(1)).thenReturn(Optional.of(companyEntity));
//
//        Optional<CompanyEntity> result = companyService.getById(1);
//
//        verify(companyRepository).findById(1);
//        assertTrue(result.isPresent());
//        assertEquals(companyEntity, result.get());
//    }
//
//    @Test
//    void testGetByName() throws Exception {
//        CompanyEntity companyEntity = new CompanyEntity();
//        when(companyRepository.getByName("Test Company")).thenReturn(companyEntity);
//
//        CompanyEntity result = companyService.getByName("Test Company");
//
//        verify(companyRepository).getByName("Test Company");
//        assertEquals(companyEntity, result);
//    }
//
//    @Test
//    void testModify() throws Exception {
//        CompanyEntity companyEntity = new CompanyEntity();
//
//        companyService.modify(companyEntity);
//
//        verify(companyRepository).save(companyEntity);
//    }
//
//    @Test
//    void testDelete() throws Exception {
//        CompanyEntity companyEntity = new CompanyEntity();
//
//        companyService.delete(companyEntity);
//
//        verify(companyRepository).delete(companyEntity);
//    }
//
//    @Test
//    void testGetAll() throws Exception {
//        List<CompanyEntity> companies = List.of(new CompanyEntity());
//        when(companyRepository.findAll()).thenReturn(companies);
//
//        List<CompanyEntity> result = companyService.getAll();
//
//        verify(companyRepository).findAll();
//        assertEquals(companies, result);
//    }
}
