package ru.orthodox.mbbg.services.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishEvent(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }
}
