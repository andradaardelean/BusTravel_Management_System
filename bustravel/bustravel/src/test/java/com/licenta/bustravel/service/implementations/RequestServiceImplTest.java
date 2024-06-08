package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.RequestEntity;
import com.licenta.bustravel.model.enums.RequestStatus;
import com.licenta.bustravel.repositories.RequestRepository;
import com.licenta.bustravel.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestServiceImplTest {

//    @Mock
//    private CompanyService companyService;
//
//    @Mock
//    private RequestRepository requestRepository;
//
//    @InjectMocks
//    private RequestServiceImpl requestService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testMakeCompanyRequest() throws Exception {
//        RequestEntity request = new RequestEntity();
//        CompanyEntity companyEntity = new CompanyEntity();
//
//        when(requestRepository.save(any(RequestEntity.class))).thenReturn(request);
//
//        requestService.makeCompanyRequest(request, companyEntity);
//
//        verify(requestRepository).save(request);
//        assertEquals(RequestStatus.PENDING, request.getStatus());
//        assertNotNull(request.getCreatedAt());
//        assertNotNull(request.getRequestDetails());
//    }
//
//    @Test
//    void testSolveCompanyRequest() throws Exception {
//        RequestEntity request = new RequestEntity();
//        request.setId(1);
//        request.setRequestDetails("name=Test Company,description=Test Description,ownerName=Test Owner,ownerEmail=test@owner.com,phone=123456789");
//
//        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
//
//        // Simulate companyService.add call
//        doNothing().when(companyService).add(any(CompanyEntity.class));
//
//        requestService.solveCompanyRequest(request);
//
//        // Verify that the companyService.add method was called once
//        verify(companyService).add(any(CompanyEntity.class));
//
//        // Verify that requestRepository.save was called once after approval
//        verify(requestRepository).save(request);
//
//        assertEquals(RequestStatus.APPROVED, request.getStatus());
//    }
//
//    @Test
//    void testDeleteRequest() throws Exception {
//        RequestEntity request = new RequestEntity();
//
//        requestService.deleteRequest(request);
//
//        verify(requestRepository).delete(request);
//    }
//
//    @Test
//    void testGetRequestById() throws Exception {
//        RequestEntity request = new RequestEntity();
//        request.setId(1);
//
//        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
//
//        RequestEntity result = requestService.getRequestById(request.getId());
//
//        verify(requestRepository).findById(request.getId());
//        assertEquals(request, result);
//    }
//
//    @Test
//    void testGetAllRequests() {
//        List<RequestEntity> requests = List.of(new RequestEntity());
//
//        when(requestRepository.findAll()).thenReturn(requests);
//        when(requestRepository.findAllByStatus(RequestStatus.PENDING)).thenReturn(requests);
//
//        List<RequestEntity> resultAll = requestService.getAllRequests("ALL");
//        List<RequestEntity> resultPending = requestService.getAllRequests("PENDING");
//
//        verify(requestRepository).findAll();
//        verify(requestRepository).findAllByStatus(RequestStatus.PENDING);
//        assertEquals(requests, resultAll);
//        assertEquals(requests, resultPending);
//    }
}
