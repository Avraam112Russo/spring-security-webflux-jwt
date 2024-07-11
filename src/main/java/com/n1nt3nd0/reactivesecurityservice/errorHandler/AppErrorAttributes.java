package com.n1nt3nd0.reactivesecurityservice.errorHandler;

import com.n1nt3nd0.reactivesecurityservice.exception.ApiException;
import com.n1nt3nd0.reactivesecurityservice.exception.authException.AuthException;
import com.n1nt3nd0.reactivesecurityservice.exception.unAuthorizedException.UnAuthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

@Component
public class AppErrorAttributes extends DefaultErrorAttributes {
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public AppErrorAttributes() {
        super();
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        var error = getError(request);

        List<Map<String, Object>> errorList = new ArrayList<>();

        if (error instanceof AuthException || error instanceof UnAuthorizedException
                || error instanceof ExpiredJwtException || error instanceof SignatureException || error instanceof MalformedJwtException) {
            status = HttpStatus.UNAUTHORIZED;
            Map<String, Object> errorMap = new LinkedHashMap<>();
            errorMap.put("code", ((ApiException) error).getErrorCode());
            errorMap.put("message", error.getMessage());
            errorList.add(errorMap);
        } else if (error instanceof ApiException) {
            status = HttpStatus.BAD_REQUEST;
            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", ((ApiException) error).getErrorCode());
            errorMap.put("message", error.getMessage());
            errorList.add(errorMap);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            var message = error.getMessage();
            if (message == null)
                message = error.getClass().getName();

            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", "INTERNAL_ERROR");
            errorMap.put("message", message);
            errorList.add(errorMap);
        }

        Map<String, Object>errors = new HashMap<>();
        errors.put("errors", errorList);
        errorAttributes.put("status", status.value());
        errorAttributes.put("errors", errors);

        return errorAttributes;
    }
}
