package com.licenta.bustravel.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChangePasswordDTO {
    String token;
    String password;
}
