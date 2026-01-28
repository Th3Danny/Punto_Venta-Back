package com.punto_venta.services.impl;

import com.punto_venta.persistance.entities.Roles;
import com.punto_venta.persistance.entities.Users;
import com.punto_venta.persistance.repositories.IRoleRepository;
import com.punto_venta.persistance.repositories.IUserRepository;
import com.punto_venta.services.IUserService;
import com.punto_venta.web.dtos.request.CreateUserRequest;
import com.punto_venta.web.dtos.response.BaseResponse;
import com.punto_venta.web.dtos.response.CreateUserResponse;
import com.punto_venta.web.dtos.response.RoleResponse;
import com.punto_venta.web.exeptions.EmailAlreadyExistsException;
import com.punto_venta.web.exeptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(IUserRepository userRepository, IRoleRepository iRoleRepository) {
    this.iUserRepository = userRepository;
    this.iRoleRepository = iRoleRepository;
}

    @Override
    public BaseResponse createUser(@Valid CreateUserRequest request) {
        // verifica si el email existe
        if (iUserRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException(("EL correo ya existe: " + request.getEmail()));
        }

        // Obtener entidades Roles a partir de los IDs recibidos
        Set<Roles> roles = new HashSet<>();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Iterable<Roles> found = iRoleRepository.findAllById(request.getRoleIds());
            found.forEach(roles::add);
        }

        if (roles.isEmpty()) {
            throw new RuntimeException("Los roles enviados no existen");
        }

        //Crear nuevo usuario
        Users users = new Users();
        users.setName(request.getName());
        users.setUsername(request.getUserName());
        users.setEmail(request.getEmail());
        users.setRoles(roles);
        users.setPassword(passwordEncoder.encode(request.getPassword()));

        //Guardar usuario
        Users savedUser = iUserRepository.save(users);

        // Mapear a DTO de respuesta profesional
        Set<RoleResponse> createdRoleResponses = savedUser.getRoles().stream()
            .map(r -> new RoleResponse(r.getId(), r.getName()))
            .collect(Collectors.toSet());

        CreateUserResponse createdResponse = new CreateUserResponse(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getName(),
            savedUser.getUsername(),
            createdRoleResponses
        );

        return BaseResponse.builder()
            .data(createdResponse)
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