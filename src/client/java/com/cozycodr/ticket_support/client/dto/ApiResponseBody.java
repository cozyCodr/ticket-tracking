package com.cozycodr.ticket_support.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseBody<T> {
    private String message;
    private int statusCode;
    private T data;
}
