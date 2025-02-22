package com.cozycodr.ticket_support.helpers;

import com.cozycodr.ticket_support.model.dto.AuthDataResponse;
import com.cozycodr.ticket_support.model.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseHelpers {

    public static ResponseEntity<ApiResponseBody> buildSuccessResponse(HttpStatus status, String message, Map<String, Object> data){
        return ResponseEntity.status(status)
                .body(ApiResponseBody.builder()
                        .statusCode(status.value())
                        .message(message)
                        .data(data)
                    .build()
                );
    }

    public static ResponseEntity<ApiResponseBody> buildSuccessResponse(HttpStatus status, String message, Object data){
        return ResponseEntity.status(status)
                .body(ApiResponseBody.builder()
                        .statusCode(status.value())
                        .message(message)
                        .data(data)
                        .build()
                );
    }

    public static ResponseEntity<ApiResponseBody> buildErrorResponse(HttpStatus status, Exception e){
        return ResponseEntity.status(status)
                .body(ApiResponseBody.builder()
                        .statusCode(status.value())
                        .message(e.getMessage())
                        .build()
                );
    }

    public static ResponseEntity<ApiResponseBody> buildErrorResponse(HttpStatus status, Exception e, ErrorResponse data){
        return ResponseEntity.status(status)
                .body(ApiResponseBody.builder()
                        .statusCode(status.value())
                        .message(e.getMessage())
                        .data(Map.of("error", data))
                        .build()
                );
    }
}
