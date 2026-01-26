package com.punto_venta.web.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateRequest {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "EL correo es invalido")
    private String email;

    @NotBlank(message = "La contraseña es requerido")
    private String password;

}
