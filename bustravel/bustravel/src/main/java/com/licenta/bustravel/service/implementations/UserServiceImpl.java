package com.licenta.bustravel.service.implementations;


import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.UserRepository;
import com.licenta.bustravel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class.getName());

    @Override
    public void add(UserEntity user) throws Exception {
        if (user.isValid(user.getPhone(), user.getEmail())) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            System.out.println("saved user:" + user);
        } else
            throw new Exception("Invalid data");
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
        LOGGER.info("Modifying2 user: " + user);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info("Current user9: " + authentication.getName());
        String username = authentication.getName();
        UserEntity currentUser = userRepository.findByUsername(username).orElseThrow();
        LOGGER.info("Current user3: " + currentUser);
        UserEntity userToModify = userRepository.getByUsername(
                user.getUsername()) == null ? userRepository.getByUsername(
                currentUser.getUsername()) : userRepository.getByUsername(user.getUsername());
        LOGGER.info("User to modify4: " + userToModify);
        LOGGER.info(currentUser.getUserType() == UserType.ADMIN ? "Admin" : "Not admin");
        LOGGER.info(currentUser.getUsername().equals(user.getUsername()) ? "Current user5" : "Not current user");
        if (currentUser.getUserType() == UserType.ADMIN || currentUser.getUsername()
                .equals(user.getUsername()) || userToModify.getCompanyEntity()
                .equals(currentUser.getCompanyEntity())) {
            user.setId(userToModify.getId());
            user.setPassword(userToModify.getPassword());
            LOGGER.info("User to modify6: " + user);
            if(user.getCompanyEntity() != null)
                user.setCompanyEntity(userToModify.getCompanyEntity());


            userRepository.save(user);
        } else
            throw new Exception("You are not allowed to modify this user!");
    }

    @Override
    public void delete(UserEntity user) throws Exception {
        if (userRepository.getByUsername(user.getUsername()) != null) {
            userRepository.delete(user);
        } else
            throw new Exception("The user you want to delete can not be found!");
    }

    @Override
    public List<UserEntity> getAll() throws Exception {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getUsersByCompany(String company) throws Exception {
        return userRepository.findByCompany(company);
    }
}
