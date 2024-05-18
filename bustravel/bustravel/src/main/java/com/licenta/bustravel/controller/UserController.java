package com.licenta.bustravel.controller;

import com.licenta.bustravel.DTO.ChangePasswordDTO;
import com.licenta.bustravel.DTO.ForgotPasswdDTO;
import com.licenta.bustravel.DTO.UserDTO;
import com.licenta.bustravel.DTO.UserSignUpDTO;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.config.OAuthService;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.service.CompanyService;
import com.licenta.bustravel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class UserController {

    private final UserService userService;
    private final CompanyService companyService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OAuthService oAuthService;

    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        if(oAuthService.isTokenValid(token)) {
            String userId = oAuthService.getOAuthId();
            if(userId.equals(""))
                return new ResponseEntity<>("Invalid user request", HttpStatus.BAD_REQUEST);
            UserEntity user = userService.getByOauthId(userId);
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            if (authentication.isAuthenticated()) {
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.badRequest().body("Invalid user request");
        }
        return new ResponseEntity<>("Token invalid!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/signup")
    public @ResponseBody ResponseEntity<?> signUp(@RequestBody UserSignUpDTO userDTO) {
        try {
            UserType userType = UserType.valueOf(userDTO.getUserType());
            UserEntity newUser = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .name(userDTO.getName())
                .phone(userDTO.getPhone())
                .email(userDTO.getEmail())
                .userType(userType)
                .build();
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
        if (!oAuthService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Token already invalidated!");
        return ResponseEntity.ok("Logout successful!");
    }

    @GetMapping("/byCompany/{company}")
    public ResponseEntity<?> getUsersByCompany(@PathVariable String company) {
        try {
            List<UserEntity> users = userService.getUsersByCompany(company);
            List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user.getUsername(), user.getName(), user.getPhone(), user.getEmail(),
                    user.getUserType()
                        .toString(), user.getCompanyEntity()
                    .getName()))
                .toList();
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
            if (!oAuthService.isTokenValid(token))
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
            if (!oAuthService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token is not valid!");
            LOGGER.info("Modifying user -1: " + userDTO);
            UserEntity user = UserEntity.builder()
                .username(userDTO.getUsername())
                .name(userDTO.getName())
                .phone(userDTO.getPhone())
                .email(userDTO.getEmail())
                .userType(UserType.valueOf(userDTO.getUserType()))
                .companyEntity(userDTO.getCompany() != null ? companyService.getByName(userDTO.getCompany()) : null)
                .build();
            LOGGER.info("Modifying user0: " + user);
            userService.modify(user);
            LOGGER.info("User modified successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Modify user does not work. " + e.getMessage());
        }
        return ResponseEntity.ok("User modified successfully!");
    }

    @GetMapping("/token")
    public ResponseEntity<?> getUserByToken(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.substring(7);
            if (!oAuthService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token is not valid!");
            String userId = oAuthService.getOAuthId();
            if(userId.equals(""))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token is not valid!");
            UserEntity user = userService.getByOauthId(userId);
            UserDTO userDTO = UserDTO.builder()
                .username(user.getUsername())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .userType(user.getUserType()
                    .toString())
                .build();
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Get user by token does not work. " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@RequestHeader("Authorization") String authorization,
                                               @PathVariable String username) {
        try {
            String token = authorization.substring(7);
            if (!oAuthService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token is not valid!");
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

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authorization,
                                        @PathVariable String username) {
        try {
            String token = authorization.substring(7);
            if (!oAuthService.isTokenValid(token))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token is not valid!");
            UserEntity user = userService.getByUsername(username);
            userService.delete(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Delete user does not work. " + e.getMessage());
        }
        return ResponseEntity.ok("User deleted successfully!");
    }

    @PutMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswdDTO userDTO) {
        try {
            String token = jwtService.generateToken(userDTO.getUsername(), null);
            userService.forgotPassword(userDTO.getUsername(), userDTO.getEmail(), token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Forgot password does not work. " + e.getMessage());
        }
        return ResponseEntity.ok("Email sent!");
    }

    @GetMapping("/validateToken/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {
        try {
            if (jwtService.isTokenValid(token)) {
                String username = jwtService.extractUsername(token);
                UserEntity user = userService.checkToken(username, token);
                UserDTO foundUser = UserDTO.builder()
                    .username(user.getUsername())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .email(user.getEmail())
                    .userType(user.getUserType()
                        .toString())
                    .build();
                if (foundUser != null) {
                    return new ResponseEntity<>(foundUser, HttpStatus.OK);
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Token is not valid!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Token is not valid!");
        }
    }


    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            String username = jwtService.extractUsername(changePasswordDTO.getToken());
            userService.changePassword(username, changePasswordDTO.getPassword(), changePasswordDTO.getToken());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Change password does not work. " + e.getMessage());
        }
        return ResponseEntity.ok("Password changed successfully!");
    }


}
