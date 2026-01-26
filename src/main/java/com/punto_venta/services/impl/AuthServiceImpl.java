package com.punto_venta.services.impl;

import com.punto_venta.persistance.entities.Users;
import com.punto_venta.persistance.repositories.IUserRepository;
import com.punto_venta.persistance.repositories.IRoleRepository;
import com.punto_venta.services.IAuthService;
import com.punto_venta.services.IUserService;
import com.punto_venta.types.JWTType;
import com.punto_venta.utils.IJWTUtils;
import com.punto_venta.web.dtos.request.AuthenticateRequest;
import com.punto_venta.web.dtos.request.RefreshTokenRequest;
import com.punto_venta.web.dtos.response.BaseResponse;
import com.punto_venta.web.exeptions.InvalidCredentialsException;
import com.punto_venta.web.exeptions.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements IAuthService {
    private final IUserService userService;
    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final IJWTUtils ijwtUtils;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl (IUserService userService, IJWTUtils ijwtUtils, IUserRepository iUserRepository, IRoleRepository iRoleRepository){
        this.userService = userService;
        this.ijwtUtils = ijwtUtils;
        this.iUserRepository = iUserRepository;
        this.iRoleRepository = iRoleRepository;
    }


    @Override
    public BaseResponse authenticate(AuthenticateRequest request) {
        //Obtener usuario por email (forzar carga de roles con fetch)
        Users users = iUserRepository.findByEmailFetchRoles(request.getEmail())
            .orElseThrow(InvalidCredentialsException::new);

        //validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), users.getPassword())) {
            throw new InvalidCredentialsException();
        }

        //crear claims (incluye roles)
        // asegurar carga e inspección de roles
        var rolesSet = users.getRoles();
        log.info("Authenticating user id={} email={} rolesEntityCount={}", users.getId(), users.getEmail(), rolesSet == null ? 0 : rolesSet.size());

        java.util.List<String> roleNames = rolesSet == null ? new java.util.ArrayList<>() : rolesSet.stream().map(r -> r.getName()).collect(Collectors.toList());
        log.debug("Role names for user {}: {}", users.getEmail(), roleNames);

        // fallback: if entity collection is empty, try native query to read role_ids and load Roles
        if (roleNames.isEmpty()) {
            try {
                java.util.List<Long> roleIds = iUserRepository.findRoleIdsByUserEmail(users.getEmail());
                if (roleIds != null && !roleIds.isEmpty()) {
                    var rolesFromDb = iRoleRepository.findAllById(roleIds);
                    roleNames = rolesFromDb.stream().map(r -> r.getName()).collect(Collectors.toList());
                    log.info("Fallback: loaded {} roles via native query for user {}", roleNames.size(), users.getEmail());
                }
            } catch (Exception ex) {
                log.warn("Fallback native role-load failed for user {}: {}", users.getEmail(), ex.getMessage());
            }
        }

        if (roleNames.isEmpty()) {
            throw new AccessDeniedException();
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", users.getId());
        claims.put("email", users.getEmail());
        claims.put("roles", roleNames.stream().map(r -> r.toUpperCase()).collect(Collectors.toList()));

        // Generar tokens
        String accessToken = ijwtUtils.generateToken(users.getEmail(), claims, JWTType.ACCESS_TOKEN);
        String refreshToken = ijwtUtils.generateToken(users.getEmail(), null, JWTType.REFRESH_TOKEN);
        Long idUser = users.getId();

        // Armar respuesta
        Map<String, Object> tokens = Map.ofEntries(
                Map.entry("access_token", accessToken),
                Map.entry("refresh_token", refreshToken),
                Map.entry("id_user", idUser)
        );

        return BaseResponse.builder()
                .data(tokens)
                .message("Authenticated successfully")
                .success(true)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public BaseResponse refreshToken (RefreshTokenRequest request) {
            // Validar token
            Boolean isTokenValid = ijwtUtils.isTokenValid(request.getRefreshToken(), JWTType.REFRESH_TOKEN);

            if (!isTokenValid) {
                throw new InvalidCredentialsException();
            }

                // Obtener info del token
                String email = ijwtUtils.getSubjectFromToken(request.getRefreshToken(), JWTType.REFRESH_TOKEN);

                // Cargar usuario y roles desde BD para regenerar claims actuales
                Users users = iUserRepository.findByEmailFetchRoles(email)
                    .orElseThrow(InvalidCredentialsException::new);

                var rolesSet = users.getRoles();
                java.util.List<String> roleNames = rolesSet == null ? new java.util.ArrayList<>() : rolesSet.stream().map(r -> r.getName()).collect(Collectors.toList());

                if (roleNames.isEmpty()) {
                    try {
                        java.util.List<Long> roleIds = iUserRepository.findRoleIdsByUserEmail(users.getEmail());
                        if (roleIds != null && !roleIds.isEmpty()) {
                            var rolesFromDb = iRoleRepository.findAllById(roleIds);
                            roleNames = rolesFromDb.stream().map(r -> r.getName()).collect(Collectors.toList());
                            log.info("Fallback: loaded {} roles via native query for refresh for user {}", roleNames.size(), users.getEmail());
                        }
                    } catch (Exception ex) {
                        log.warn("Fallback native role-load failed during refresh for user {}: {}", users.getEmail(), ex.getMessage());
                    }
                }

                if (roleNames.isEmpty()) {
                    throw new AccessDeniedException();
                }
                Map<String, Object> newClaims = new HashMap<>();
                newClaims.put("id", users.getId());
                newClaims.put("email", users.getEmail());
                newClaims.put("roles", roleNames.stream().map(r -> r.toUpperCase()).collect(Collectors.toList()));

                // Generar nuevos tokens
                String accessToken = ijwtUtils.generateToken(email, newClaims, JWTType.ACCESS_TOKEN);
                String refreshToken = ijwtUtils.generateToken(email, null, JWTType.REFRESH_TOKEN);

            Map<String, Object> tokens = Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken
            );


            return BaseResponse.builder()
                    .data(tokens)
                    .message("Token refreshed")
                    .success(true)
                    .httpStatus(HttpStatus.CREATED)
                    .build();
        }
    }
