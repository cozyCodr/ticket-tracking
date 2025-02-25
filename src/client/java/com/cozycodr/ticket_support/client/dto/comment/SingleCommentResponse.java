package com.cozycodr.ticket_support.client.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingleCommentResponse {
    private CommentResponse comment;
}
