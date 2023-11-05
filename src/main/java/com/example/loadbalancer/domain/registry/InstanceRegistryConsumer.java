package com.example.loadbalancer.domain.registry;

import com.example.loadbalancer.domain.model.InstanceRegisteredEvent;
import com.example.loadbalancer.domain.model.InstanceUnregisteredEvent;

public interface InstanceRegistryConsumer {

    void consume(InstanceRegisteredEvent event);
    void consume(InstanceUnregisteredEvent event);
}
