package com.example.loadbalancer.app;

import com.example.loadbalancer.domain.model.InstanceRegisteredEvent;
import com.example.loadbalancer.domain.registry.InstanceRegistryConsumer;
import com.example.loadbalancer.domain.model.InstanceUnregisteredEvent;
import org.springframework.context.event.EventListener;

public class SpringEventConsumer implements InstanceRegistryConsumer {

    private final InstanceRegistryConsumer delegate;

    public SpringEventConsumer(InstanceRegistryConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    @EventListener
    public void consume(InstanceRegisteredEvent event) {
        delegate.consume(event);
    }

    @Override
    @EventListener
    public void consume(InstanceUnregisteredEvent event) {
        delegate.consume(event);
    }
}
