package com.cozycodr.ticket_support.client.event;

import lombok.Getter;

@Getter
public class ApplicationEvent {
    private final String type;
    private final Object data;

    public ApplicationEvent(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public static final String LOGOUT = "LOGOUT";
    public static final String LOGIN = "LOGIN";
    public static final String USER_UPDATE = "USER_UPDATE";
}
