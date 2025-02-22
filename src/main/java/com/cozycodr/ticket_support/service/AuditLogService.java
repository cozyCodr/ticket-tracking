package com.cozycodr.ticket_support.service;

import com.cozycodr.ticket_support.model.entity.AuditLog;
import com.cozycodr.ticket_support.model.entity.Comment;
import com.cozycodr.ticket_support.model.entity.Ticket;
import com.cozycodr.ticket_support.model.entity.User;
import com.cozycodr.ticket_support.model.enums.TicketStatus;
import com.cozycodr.ticket_support.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class AuditLogService {

    private final AuditLogRepository logRepository;

    public void logNewTicketCreated(User user, Ticket ticket){
        String message = String.format("new ticket %s opened by %s", ticket.getId(), user.getUsername());
        AuditLog auditLog = AuditLog.builder()
                .logMessage(message)
                .build();

        logRepository.save(auditLog);
        log.info(message);
    }

    public void logCommentAddedToTicket(Comment comment, Ticket ticket, User user){
        String message = String.format("new comment %s added ticket %s opened by %s",
                comment.getId(), ticket.getId(), user.getUsername());
        AuditLog auditLog = AuditLog.builder()
                .logMessage(message)
                .build();

        logRepository.save(auditLog);
        log.info(message);
    }

    public void logTicketStatusChange(TicketStatus beforeStatus, TicketStatus afterStatus, Ticket ticket, User user){
        String message = String.format("status of ticket %s changed from %s to %s by %s",
                ticket.getId(), beforeStatus, afterStatus, user.getUsername()
        );
        AuditLog auditLog = AuditLog.builder()
                .logMessage(message)
                .build();

        logRepository.save(auditLog);
        log.info(message);
    }

    public void logNewUserCreated(User user){
        String message = String.format("New user %s registered", user.getUsername());
        AuditLog auditLog = AuditLog.builder()
                .logMessage(message)
                .build();

        logRepository.save(auditLog);
        log.info(message);
    }

    public void logNewUserLogin(User user){
        String message = String.format("User %s logged in at %x", user.getUsername(), new Date().getTime());
        AuditLog auditLog = AuditLog.builder()
                .logMessage(message)
                .build();

        logRepository.save(auditLog);
        log.info(message);
    }
}
