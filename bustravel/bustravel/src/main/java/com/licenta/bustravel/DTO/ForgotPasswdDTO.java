package com.licenta.bustravel.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ForgotPasswdDTO {
    String username;
    String email;
}
