package com.licenta.bustravel.service;


import com.licenta.bustravel.model.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void add(UserEntity user) throws Exception;
    Optional<UserEntity> getById(int id) throws Exception;
    UserEntity getByUsername(String username) throws Exception;
    void modify(UserEntity user) throws Exception;
    void delete(UserEntity user) throws Exception;
    List<UserEntity> getAll() throws  Exception;
}
