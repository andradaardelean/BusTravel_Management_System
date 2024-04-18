package com.licenta.bustravel.service.implementations;


import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.UserRepository;
import com.licenta.bustravel.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void add(UserEntity user) throws Exception {
        if (user.isValid(user.getPhone(), user.getEmail())) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity currentUser = userRepository.findByUsername(username).orElseThrow();
        UserEntity userToModify = userRepository.getByUsername(
                user.getUsername()) == null ? userRepository.getByUsername(
                currentUser.getUsername()) : userRepository.getByUsername(user.getUsername());
        if (currentUser.getUserType() == UserType.ADMIN || currentUser.getUsername()
                .equals(user.getUsername()) || userToModify.getCompanyEntity()
                .equals(currentUser.getCompanyEntity())) {
            user.setId(userToModify.getId());
            user.setPassword(userToModify.getPassword());
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
    public List<UserEntity> getUsersByCompany(String company) {
        return userRepository.findByCompany(company);
    }
}
