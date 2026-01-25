package com.punto_venta.web.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "Refrescar el token es requerido")
    private String refreshToken;
}
