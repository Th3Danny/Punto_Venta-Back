package com.punto_venta.services;

import com.punto_venta.web.dtos.request.AuthenticateRequest;
import com.punto_venta.web.dtos.request.RefreshTokenRequest;
import com.punto_venta.web.dtos.response.BaseResponse;

public interface IAuthService {
    BaseResponse authenticate (AuthenticateRequest request);

    BaseResponse refreshToken (RefreshTokenRequest request);
}