package com.punto_venta.web.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateResponse {
    private String accessToken;
    private String refreshToken;
    private Long id;
}
