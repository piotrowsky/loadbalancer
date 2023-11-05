package com.example.loadbalancer.domain.registry;

import com.example.loadbalancer.domain.model.InstanceRegisteredEvent;
import com.example.loadbalancer.domain.model.InstanceUnregisteredEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class InstanceRegistryTracker implements InstanceRegistryConsumer {

    private final AtomicInteger numberOfActiveInstances;

    public InstanceRegistryTracker(InstanceRegistry registry) {
        this.numberOfActiveInstances = new AtomicInteger(registry.listActive().size());
    }

    public int numberOfActiveInstances() {
        return numberOfActiveInstances.get();
    }

    @Override
    public void consume(InstanceRegisteredEvent event) {
        numberOfActiveInstances.incrementAndGet();
    }

    @Override
    public void consume(InstanceUnregisteredEvent event) {
        numberOfActiveInstances.decrementAndGet();
    }
}
