package com.punto_venta.web.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailAlreadyExistsException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
