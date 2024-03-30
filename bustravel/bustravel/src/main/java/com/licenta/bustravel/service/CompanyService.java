package com.licenta.bustravel.service;

import com.licenta.bustravel.model.CompanyEntity;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    void add(CompanyEntity companyEntity) throws Exception;
    Optional<CompanyEntity> getById(int id) throws Exception;
    CompanyEntity getByName(String name) throws Exception;
    void modify(CompanyEntity companyEntity) throws Exception;
    void delete(CompanyEntity companyEntity) throws Exception;
    List<CompanyEntity> getAll() throws  Exception;
}
