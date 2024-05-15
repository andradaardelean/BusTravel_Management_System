package com.licenta.bustravel.service.implementations;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.licenta.bustravel.email.EmailSender;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.UserRepository;
import com.licenta.bustravel.service.UserService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void add(UserEntity user) throws Exception {
        String id = saveToOAuth(user);
        if (user.isValid(user.getPhone(), user.getEmail())) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setOauthId(id);
            userRepository.save(user);
        } else
            throw new Exception("Invalid data");
    }

    public static String saveToOAuth(UserEntity user){
        HttpResponse<JSONObject> response = Unirest.post("https://travel-management-system.eu.auth0.com/oauth/token")
            .header("content-type", "application/json")
            .body(
                "{\"client_id\":\"OvxbQnJePNaihkjBLb0ythpweFosd2Is\"," +
                    "\"client_secret\":\"jCukuVX3FxABz6D1Z-jAFaJwKxotJQcKVVkIARKKANqKte0dxKlDRE9oHd-5JMIM\"," +
                    "\"audience\":\"https://travel-management-system.eu.auth0.com/api/v2/\"," +
                    "\"grant_type\":\"client_credentials\"}").asObject(JSONObject.class);
        String token = response.getBody()
            .get("access_token")
            .toString();
        HttpResponse<JSONObject> response2 = Unirest.post("https://travel-management-system.eu.auth0.com/api/v2/users")
            .header("content-type", "application/json")
            .header("Authorization", "Bearer " + token)
            .body(String.format("{\"email\": \"%s\", " +
                    "\"user_metadata\": {}, " +
                    "\"blocked\": false, " +
                    "\"email_verified\": false, " +
                    "\"app_metadata\": {}, " +
                    "\"given_name\": \"%s\", " +
                    "\"family_name\": \"%s\", " +
                    "\"name\": \"%s\", " +
                    "\"nickname\": \"%s\", " +
                    "\"user_id\": \"\", " +
                    "\"connection\": \"%s\", " +
                    "\"password\": \"%s\", " +
                    "\"verify_email\": false, " +
                    "\"username\": \"%s\"}",
                user.getEmail(),
                user.getName(),
                user.getName(),
                user.getName(),
                user.getName(),
                "Username-Password-Authentication",
                user.getPassword(),
                user.getUsername()))
            .asObject(JSONObject.class);

        return response2.getBody().get("user_id").toString();
    }

    @Override
    public Optional<UserEntity> getById(int id) throws Exception {
        return userRepository.findById(id);
    }

    @Override
    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow();
    }

    @Override
    public void modify(UserEntity user) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        String username = authentication.getName();
        UserEntity currentUser = userRepository.findByUsername(username)
            .orElseThrow();
        UserEntity userToModify = userRepository.findByUsername(user.getUsername())
            .orElse(null);
        if (currentUser.getUserType() == UserType.ADMIN || currentUser.getUsername()
            .equals(user.getUsername()) || userToModify.getCompanyEntity()
            .equals(currentUser.getCompanyEntity())) {
            user.setId(userToModify.getId());
            user.setPassword(userToModify.getPassword());
            if (user.getCompanyEntity() != null)
                user.setCompanyEntity(userToModify.getCompanyEntity());
            userRepository.save(user);
        } else
            throw new Exception("You are not allowed to modify this user!");
    }

    @Override
    public void delete(UserEntity user) throws Exception {
        userRepository.delete(userRepository.findByUsername(user.getUsername())
            .orElseThrow());

    }

    @Override
    public List<UserEntity> getAll() throws Exception {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getUsersByCompany(String company) {
        return userRepository.findByCompany(company);
    }

    @Override
    public void forgotPassword(String username, String email, String token) throws Exception {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow();
        if (user.getEmail()
            .equals(email)) {
            String to = email;
            String subject = "Reset your password";
            URL url = new URL("http://localhost:3000/change-password/" + token);
            String body =
                "Hi, \n thanks for choosing to work with us! \n To reset your password please access this " + "link: "
                    + url + "\n" + "If you did not request this, please ignore this email.";
            EmailSender.sendEmail(to, subject, body);
            user.setToken(token);
            userRepository.save(user);
        } else
            throw new Exception("Invalid email address!");
    }

    @Override
    public UserEntity checkToken(String username, String token) {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow();
        return user.getToken()
            .equals(token) ? user : null;
    }

    @Override
    public void changePassword(String username, String password, String token) throws Exception {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow();
        if (user.getToken()
            .equals(token)) {
            user.setPassword(passwordEncoder.encode(password));
            user.setToken(null);
            userRepository.save(user);
        } else
            throw new Exception("Invalid token!");
    }


}
