package com.punto_venta.web.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends RuntimeException{
    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(Class classType) {
        super(String.format("%s not found", classType.toString()));
    }
}
