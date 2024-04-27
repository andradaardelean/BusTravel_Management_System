package com.licenta.bustravel.controller;


import com.licenta.bustravel.DTO.AuthRequest;
import com.licenta.bustravel.DTO.UserDTO;
import com.licenta.bustravel.DTO.UserSignUpDTO;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername(), authentication.getAuthorities());
        } else
            throw new UsernameNotFoundException("Invalid user request");
    }

    @PostMapping("/signup")
    public @ResponseBody ResponseEntity<?> signUp(@RequestBody UserSignUpDTO userDTO) {
        try {
            UserType userType = UserType.valueOf(userDTO.getUserType());
            UserEntity newUser = new UserEntity(0, userDTO.getUsername(), userDTO.getPassword(), userDTO.getName(),
                    userDTO.getPhone(), userDTO.getEmail(), userType, null, null);
            userService.add(newUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Sign up user does not work. " + e.getMessage());
        }
        return ResponseEntity.ok("Signed up successful!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        if (!jwtService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token already invalidated!");
        jwtService.invalidateToken(token);
        return ResponseEntity.ok("Logout successful!");
    }

    @GetMapping("/byCompany/{company}")
    public @ResponseBody ResponseEntity<?> getUsersByCompany(@PathVariable String company) {
        try {
            List<UserEntity> users = userService.getUsersByCompany(company);
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> new UserDTO(user.getUsername(), user.getName(), user.getPhone(), user.getEmail(),
                            user.getUserType()
                                    .toString(), user.getCompanyEntity()
                            .getName()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Get users by company does not work. " + e.getMessage());
        }
    }


    @GetMapping("")
    public @ResponseBody ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Token is not valid!");
            List<UserEntity> users = userService.getAll();
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> new UserDTO(user.getUsername(), user.getName(), user.getPhone(), user.getEmail(),
                            user.getUserType()
                                    .toString(), user.getCompanyEntity() != null ? user.getCompanyEntity()
                            .getName() : ""))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Get all users does not work. " + e.getMessage());
        }
    }

    @PutMapping("")
    public @ResponseBody ResponseEntity<?> modifyUser(@RequestHeader("Authorization") String authorization,
                                                      @RequestBody UserDTO userDTO) {
        try {
            String token = authorization.substring(7);
            LOGGER.info("token: " + token);
            if (!jwtService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Token is not valid!");
            LOGGER.info("Modifying user -1: " + userDTO);
            UserEntity user = new UserEntity(0, userDTO.getUsername(), null, userDTO.getName(), userDTO.getPhone(),
                    userDTO.getEmail(), UserType.valueOf(userDTO.getUserType()), null, null);
            LOGGER.info("Modifying user0: " + user);
            userService.modify(user);
            LOGGER.info("User modified successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Modify user does not work. " + e.getMessage());
        }
        return ResponseEntity.ok("User modified successfully!");
    }

    @GetMapping("{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            UserEntity user = userService.getByUsername(username);
            UserDTO userDTO = new UserDTO(user.getUsername(), user.getName(), user.getPhone(), user.getEmail(),
                    user.getUserType()
                            .toString(), user.getCompanyEntity() != null ? user.getCompanyEntity()
                    .getName() : "");
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Get user by username does not work. " + e.getMessage());
        }
    }
}
