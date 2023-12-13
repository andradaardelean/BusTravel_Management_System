package com.licenta.bustravel.service.implementations;


import com.licenta.bustravel.entities.UserEntity;
import com.licenta.bustravel.entities.enums.UserType;
import com.licenta.bustravel.repositories.UserRepository;
import com.licenta.bustravel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void add(UserEntity user) throws Exception {
        if(user.isValid(user.getPhone(), user.getEmail())){
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            System.out.println("saved user:" + user);
        }
        else throw new Exception("Invalid data");
    }

    @Override
    public Optional<UserEntity> getById(int id) throws Exception {
        return userRepository.findById(id);
    }

    @Override
    public UserEntity getByUsername(String username) throws Exception {
        return userRepository.getByUsername(username);
    }

    @Override
    public void modify(UserEntity user) throws Exception {
        userRepository.save(user);
    }

    @Override
    public void delete(UserEntity user) throws Exception {
        if(userRepository.getByUsername(user.getUsername()) != null){
            userRepository.delete(user);
        }
        else throw new Exception("The user you want to delete can not be found!");
    }

    @Override
    public List<UserEntity> getAll() throws Exception {
        return userRepository.findAll();
    }
}
