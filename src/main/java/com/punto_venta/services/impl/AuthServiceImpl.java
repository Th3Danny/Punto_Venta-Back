package com.punto_venta.services.impl;

import com.punto_venta.persistance.entities.Users;
import com.punto_venta.services.IAuthService;
import com.punto_venta.services.IUserService;
import com.punto_venta.types.JWTType;
import com.punto_venta.utils.IJWTUtils;
import com.punto_venta.web.dtos.request.AuthenticateRequest;
import com.punto_venta.web.dtos.request.RefreshTokenRequest;
import com.punto_venta.web.dtos.response.BaseResponse;
import com.punto_venta.web.exeptions.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements IAuthService {
    private final IUserService userService;
    private final IJWTUtils ijwtUtils;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl (IUserService userService, IJWTUtils ijwtUtils){
        this.userService = userService;
        this.ijwtUtils = ijwtUtils;
    }


    @Override
    public BaseResponse authenticate(AuthenticateRequest request) {
        //Obtener usuario por email
        Users users = userService.getOptionalUserEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        //validar contraseña
        if (!passwordEncoder.matches(request.getPassword(), users.getPassword())) {
            throw new InvalidCredentialsException();
        }

        //crear claims
        Map<String, Object> claims = Map.of(
                "id", users.getId(),
                "email", users.getEmail()
        );

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
            Map<String, Object> claims = ijwtUtils.getClaimsFromToken(request.getRefreshToken(), JWTType.REFRESH_TOKEN);

            // Generar nuevos tokens
            String accessToken = ijwtUtils.generateToken(email, claims, JWTType.ACCESS_TOKEN);
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
