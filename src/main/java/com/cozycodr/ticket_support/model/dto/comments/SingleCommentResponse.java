package com.cozycodr.ticket_support.model.dto.comments;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingleCommentResponse {
    private CommentResponse comment;
}
