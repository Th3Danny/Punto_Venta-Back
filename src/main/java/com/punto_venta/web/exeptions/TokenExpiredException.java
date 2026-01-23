package com.punto_venta.web.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenExpiredException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public TokenExpiredException() {
        super("Token expired");
    }

}
