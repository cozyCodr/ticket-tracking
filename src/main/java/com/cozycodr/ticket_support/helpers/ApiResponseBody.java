package com.cozycodr.ticket_support.helpers;

import lombok.*;

import java.util.Map;



@Builder
public record ApiResponseBody(String message, int statusCode, Object data) {

}
