package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSignUpDTO {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String userType;
}
