package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.model.*;
import com.licenta.bustravel.model.enums.RecurrenceType;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.*;
import com.licenta.bustravel.service.utils.DistanceMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private StopsRepository stopsRepository;

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdd() throws Exception {
        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setRecurrenceType(RecurrenceType.NONE);
        List<StopEntity> stops = new ArrayList<>();
        List<Integer> days = new ArrayList<>();

        UserEntity user = new UserEntity();
        user.setUserType(UserType.COMPANYEMPLOYEE);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(stopsRepository.findStop(anyString(), anyString())).thenReturn(null);

        routeService.add(routeEntity, stops, days);

        verify(routeRepository).saveAll(anyList());
    }

    @Test
    void testAddNotAllowed() throws Exception {
        RouteEntity routeEntity = new RouteEntity();
        List<StopEntity> stops = new ArrayList<>();
        List<Integer> days = new ArrayList<>();

        UserEntity user = new UserEntity();
        user.setUserType(UserType.CLIENT);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(Exception.class, () -> {
            routeService.add(routeEntity, stops, days);
        });

        assertTrue(exception.getMessage().contains("Not allowed."));
    }

    @Test
    void testGetById() throws Exception {
        RouteEntity routeEntity = new RouteEntity();
        when(routeRepository.findById(1)).thenReturn(Optional.of(routeEntity));

        Optional<RouteEntity> result = routeService.getById(1);

        verify(routeRepository).findById(1);
        assertTrue(result.isPresent());
        assertEquals(routeEntity, result.get());
    }

    @Test
    void testModify() throws Exception {
        RouteEntity routeEntity = new RouteEntity();
        List<StopEntity> stops = new ArrayList<>();

        UserEntity user = new UserEntity();
        user.setUserType(UserType.COMPANYEMPLOYEE);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        routeService.modify(routeEntity, stops);

        verify(routeRepository).save(routeEntity);
    }

    @Test
    void testModifyNotAllowed() throws Exception {
        RouteEntity routeEntity = new RouteEntity();
        List<StopEntity> stops = new ArrayList<>();

        UserEntity user = new UserEntity();
        user.setUserType(UserType.CLIENT);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(Exception.class, () -> {
            routeService.modify(routeEntity, stops);
        });

        assertTrue(exception.getMessage().contains("Not allowed."));
    }

    @Test
    void testDelete() throws Exception {
        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setRecurrenceType(RecurrenceType.NONE);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("username");
        UserEntity user = new UserEntity();
        user.setUserType(UserType.COMPANYEMPLOYEE);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(routeRepository.findRoute(any(), any(), anyString(), anyString())).thenReturn(routeEntity);

//        routeService.delete(routeEntity, false);

//        verify(routeRepository).delete(routeEntity);
    }

    @Test
    void testDeleteNotAllowed() throws Exception {
        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setRecurrenceType(RecurrenceType.NONE);

        UserEntity user = new UserEntity();
        user.setUserType(UserType.CLIENT);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        Exception exception = assertThrows(Exception.class, () -> {
            routeService.delete(routeEntity, false);
        });

        assertTrue(exception.getMessage().contains("Not allowed."));
    }

    @Test
    void testGetAll() throws Exception {
        List<RouteEntity> routes = List.of(new RouteEntity());
        when(routeRepository.findAll()).thenReturn(routes);

        List<RouteEntity> result = routeService.getAll();

        verify(routeRepository).findAll();
        assertEquals(routes, result);
    }

    @Test
    void testSearch() throws Exception {
        List<RouteEntity> routes = List.of(new RouteEntity());
        when(routeRepository.findAll()).thenReturn(routes);
        when(stopsRepository.findStopByLocation(anyString())).thenReturn(new StopEntity());
//        when(DistanceMatrix.getData(anyString(), anyString())).thenReturn("{}");
//        when(DistanceMatrix.parseData(anyString())).thenReturn(new HashMap<>());

//        Map<List<LinkEntity>, String> result = routeService.search("search", "2024-05-01", "2024-05-31", "startLocation", "endLocation", "1");

//        assertNotNull(result);
    }


}
