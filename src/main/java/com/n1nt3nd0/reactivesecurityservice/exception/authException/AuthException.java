package com.n1nt3nd0.reactivesecurityservice.exception.authException;

import com.n1nt3nd0.reactivesecurityservice.exception.ApiException;

public class AuthException extends ApiException {
    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
}
