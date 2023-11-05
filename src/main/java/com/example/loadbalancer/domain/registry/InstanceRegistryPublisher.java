package com.example.loadbalancer.domain.registry;

import com.example.loadbalancer.domain.model.InstanceRegisteredEvent;
import com.example.loadbalancer.domain.model.InstanceUnregisteredEvent;

public interface InstanceRegistryPublisher {

    void publish(InstanceRegisteredEvent event);

    void publish(InstanceUnregisteredEvent event);
}
