package com.eduardo.geolocation_h3uber.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class H3IndexNotFoundException extends RuntimeException {
    public H3IndexNotFoundException(String message) {
        super(message);
    }
}
