package com.punto_venta.web.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticateRequest {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "EL correo es invalido")
    private String email;

    @NotBlank(message = "La contraseña es requerido")
    private String password;

    public AuthenticateRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
