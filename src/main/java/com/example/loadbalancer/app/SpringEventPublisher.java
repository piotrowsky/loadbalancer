package com.example.loadbalancer.app;

import com.example.loadbalancer.domain.model.InstanceRegisteredEvent;
import com.example.loadbalancer.domain.registry.InstanceRegistryPublisher;
import com.example.loadbalancer.domain.model.InstanceUnregisteredEvent;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventPublisher implements InstanceRegistryPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(InstanceRegisteredEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publish(InstanceUnregisteredEvent event) {
        publisher.publishEvent(event);
    }
}
