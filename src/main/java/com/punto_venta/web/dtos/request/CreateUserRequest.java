package com.punto_venta.web.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 40, message = "El nombre no puede exceder 40 caracteres")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 50, message = "El email no puede exceder 50 caracteres")
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 40, message = "El nombre de usuario no puede exceder los 40 caracteres")
    private String userName;

    @NotEmpty(message = "Debe asignarse al menos un rol")
    private Set<Long> roleIds;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 40, message = "La contraseña debe tener entre 6 y 40 caracteres")
    private String password;
}
