package com.punto_venta.web.controllers;

import com.punto_venta.services.IAuthService;
import com.punto_venta.web.dtos.request.AuthenticateRequest;
import com.punto_venta.web.dtos.request.RefreshTokenRequest;
import com.punto_venta.web.dtos.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IAuthService authService;

    public AuthController(IAuthService authService){
        this.authService = authService;
    }

    @PostMapping("authenticate")
    public ResponseEntity<BaseResponse> authenticate (@Valid @RequestBody AuthenticateRequest request) {
        BaseResponse baseResponse = authService.authenticate(request);
        return baseResponse.buildResponseEntity();
    }

    @PostMapping("refresh-token")
    public ResponseEntity<BaseResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        BaseResponse baseResponse = authService.refreshToken(request);

        return baseResponse.buildResponseEntity();
    }
}