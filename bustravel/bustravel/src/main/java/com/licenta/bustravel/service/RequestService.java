package com.licenta.bustravel.service;

import com.licenta.bustravel.DTO.CompanyDTO;
import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.RequestEntity;

import java.util.List;

public interface RequestService {
    void makeCompanyRequest(RequestEntity request, CompanyEntity companyEntity) throws Exception;
    void solveCompanyRequest(RequestEntity request) throws Exception;
    void modifyRequest(RequestEntity request) throws Exception;
    void deleteRequest(RequestEntity request) throws Exception;
    RequestEntity getRequestById(int id) throws Exception;
    List<RequestEntity> getAllRequests();
}
