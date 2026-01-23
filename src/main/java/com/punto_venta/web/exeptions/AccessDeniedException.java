package com.punto_venta.web.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccessDeniedException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    public AccessDeniedException() {
        super("Access denied");
    }
}
