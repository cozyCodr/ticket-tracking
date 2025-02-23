package com.cozycodr.ticket_support.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHelpers {

    public static <T> ResponseEntity<ApiResponseBody<T>> buildSuccessResponse(HttpStatus status, String message, T data){
        return ResponseEntity.status(status)
                .body(ApiResponseBody.<T>builder()
                        .statusCode(status.value())
                        .message(message)
                        .data(data)
                    .build()
                );
    }

    public static <T> ResponseEntity<ApiResponseBody<T>> buildErrorResponse(HttpStatus status, Exception e){
        return ResponseEntity.status(status)
                .body(ApiResponseBody.<T>builder()
                        .statusCode(status.value())
                        .message(e.getMessage())
                        .build()
                );
    }

    public static <T> ResponseEntity<ApiResponseBody<T>> buildErrorResponse(HttpStatus status, Exception e, T data){
        return ResponseEntity.status(status)
                .body(ApiResponseBody.<T>builder()
                        .statusCode(status.value())
                        .message(e.getMessage())
                        .data(data)
                        .build()
                );
    }
}
