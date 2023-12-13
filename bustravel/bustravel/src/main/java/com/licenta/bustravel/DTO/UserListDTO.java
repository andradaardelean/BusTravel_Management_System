package com.licenta.bustravel.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class UserListDTO {
    String username;
    String email;
    String phone;
    String userType;
}
