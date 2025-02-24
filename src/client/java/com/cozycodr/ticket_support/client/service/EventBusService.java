package com.cozycodr.ticket_support.client.service;

import com.cozycodr.ticket_support.client.event.ApplicationEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class EventBusService {
    private final Map<String, List<Consumer<ApplicationEvent>>> listeners = new ConcurrentHashMap<>();

    public void subscribe(String eventType, Consumer<ApplicationEvent> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void publish(ApplicationEvent event) {
        if (listeners.containsKey(event.getType())) {
            listeners.get(event.getType()).forEach(listener -> listener.accept(event));
        }
    }

    public void unsubscribe(String eventType, Consumer<ApplicationEvent> listener) {
        if (listeners.containsKey(eventType)) {
            listeners.get(eventType).remove(listener);
        }
    }
}
