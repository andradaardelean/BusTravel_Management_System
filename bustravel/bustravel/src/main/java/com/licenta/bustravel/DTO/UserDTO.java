package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    String username;
    String name;
    String phone;
    String email;
    String userType;
    String company;
}
