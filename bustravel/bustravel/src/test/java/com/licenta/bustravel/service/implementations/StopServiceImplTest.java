package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.model.StopEntity;
import com.licenta.bustravel.repositories.StopsRepository;
import com.licenta.bustravel.service.StopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StopServiceImplTest {

    @Mock
    private StopsRepository stopsRepository;

    @InjectMocks
    private StopServiceImpl stopService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStops() {
        // Arrange
        StopEntity stop1 = new StopEntity();
        stop1.setId(1);
        stop1.setLocation("Location1");
        stop1.setAddress("Address1");

        StopEntity stop2 = new StopEntity();
        stop2.setId(2);
        stop2.setLocation("Location2");
        stop2.setAddress("Address2");

        List<StopEntity> expectedStops = Arrays.asList(stop1, stop2);

        when(stopsRepository.findAll()).thenReturn(expectedStops);

        // Act
        List<StopEntity> actualStops = stopService.getAllStops();

        // Assert
        assertEquals(expectedStops, actualStops);
        verify(stopsRepository).findAll();
    }
}
