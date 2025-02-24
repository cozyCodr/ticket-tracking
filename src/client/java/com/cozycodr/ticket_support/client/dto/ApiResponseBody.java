package com.cozycodr.ticket_support.client.dto;

import lombok.Builder;


@Builder
public record ApiResponseBody<T>(String message, int statusCode, T data) {

}
