package com.punto_venta.web.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class AuthenticateResponse {
    private String accessToken;
    private String refreshToken;
    private Long id;
}
