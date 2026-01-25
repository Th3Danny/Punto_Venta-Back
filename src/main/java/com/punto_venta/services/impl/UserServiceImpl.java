package com.punto_venta.services.impl;

import com.punto_venta.persistance.entities.Users;
import com.punto_venta.persistance.repositories.IUserRepository;
import com.punto_venta.services.IUserService;
import com.punto_venta.web.dtos.request.CreateUserRequest;
import com.punto_venta.web.dtos.response.BaseResponse;
import com.punto_venta.web.exeptions.EmailAlreadyExistsException;
import com.punto_venta.web.exeptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(IUserRepository userRepository) {
    this.iUserRepository = userRepository;
}

    @Override
    public BaseResponse createUser(@Valid CreateUserRequest request) {
        // verifica si el email existe
        if (iUserRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException(("EL correo ya existe: " + request.getEmail()));
        }

        //Crear nuevo usuario
        Users users = new Users();
        users.setName(request.getName());
        users.setUsername(request.getUserName());
        users.setEmail(request.getEmail());
        users.setPassword(passwordEncoder.encode(request.getPassword()));

        //Guardar usuario
        Users savedUser = iUserRepository.save(users);

        return BaseResponse.builder()
                .data(savedUser)
                .message("Usuario creado correctamente")
                .success(true)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    @Override
    public BaseResponse getUserById(Long id) {
        Users users = iUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(("Usuario no encontrado con id: " + id).getClass()));
        return BaseResponse.builder()
                .data(users)
                .message("Usuario obtenido correctamente")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public BaseResponse getAllUser() {
        List<Users> users= iUserRepository.findAll();

        return BaseResponse.builder()
                .data(users)
                .message("Usuarios obtenidos correctamente")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public BaseResponse getUserByEmail(String email) {
        Users users = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(("Usuario no encontrado con el correo: " + email).getClass()));

        return BaseResponse.builder()
                .data(users)
                .message("Usuario obtenido correctamente")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    // Método unificado que puede usarse para ambos casos
    @Override
    public Optional<Users> getOptionalUserEmail(String email) {
        return iUserRepository.findByEmail(email);
    }
}