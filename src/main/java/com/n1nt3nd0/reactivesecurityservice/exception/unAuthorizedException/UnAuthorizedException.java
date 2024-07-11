package com.n1nt3nd0.reactivesecurityservice.exception.unAuthorizedException;

import com.n1nt3nd0.reactivesecurityservice.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnAuthorizedException extends ApiException {
    public UnAuthorizedException(String message) {
        super(message, "N!NT3ND0_UNAUTHORIZED_EXCEPTION");
    }
}
