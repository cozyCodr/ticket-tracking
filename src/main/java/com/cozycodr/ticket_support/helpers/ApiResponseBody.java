package com.cozycodr.ticket_support.helpers;

import lombok.*;

import java.util.Map;



@Builder
public record ApiResponseBody<T>(String message, int statusCode, T data) {

}
