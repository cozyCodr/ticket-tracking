package com.cozycodr.ticket_support.repository;

import com.cozycodr.ticket_support.model.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByTicket_IdOrderByCreatedDateDesc(UUID ticketId, Pageable pageable);
}
