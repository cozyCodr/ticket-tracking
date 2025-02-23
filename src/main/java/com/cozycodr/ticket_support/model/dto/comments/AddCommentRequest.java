package com.cozycodr.ticket_support.model.dto.comments;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class AddCommentRequest {
    private String message;
}
