package com.licenta.bustravel.controller;


import com.licenta.bustravel.DTO.AuthRequest;
import com.licenta.bustravel.DTO.UserSignUpDTO;
import com.licenta.bustravel.config.JwtService;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(authRequest.getUsername(), authentication.getAuthorities());
        }
        else throw new UsernameNotFoundException("Invalid user request");
    }

    @PostMapping("/signup")
    public @ResponseBody ResponseEntity<?> signUp(@RequestBody UserSignUpDTO userDTO){
        try{
            UserType userType = UserType.valueOf(userDTO.getUserType());
            UserEntity newUser = new UserEntity(0,userDTO.getUsername(), userDTO.getPassword(), userDTO.getName(), userDTO.getPhone(), userDTO.getEmail(), userType, null, null);
            userService.add(newUser);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sign up user does not work. " + e.getMessage());
        }
        return ResponseEntity.ok("Signed up successful!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader ){
        String token = authorizationHeader.substring(7);
        if(!jwtService.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token already invalidated!");
        jwtService.invalidateToken(token);
        return ResponseEntity.ok("Logout successful!");
    }


}
