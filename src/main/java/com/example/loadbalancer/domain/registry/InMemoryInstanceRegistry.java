package com.example.loadbalancer.domain.registry;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.InstanceId;
import com.example.loadbalancer.domain.model.InstanceRegisteredEvent;
import com.example.loadbalancer.domain.model.InstanceUnregisteredEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Comparator.comparing;

public class InMemoryInstanceRegistry implements InstanceRegistry {

    private final Map<InstanceId, Instance> instances = new ConcurrentHashMap<>();
    private final InstanceRegistryPublisher publisher;

    public InMemoryInstanceRegistry(InstanceRegistryPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void register(Instance instance) {
        instances.put(instance.instanceId(), instance);
        publisher.publish(new InstanceRegisteredEvent(instance));
    }

    @Override
    public void unregister(InstanceId instanceId) {
        final var instance = instances.remove(instanceId);
        publisher.publish(new InstanceUnregisteredEvent(instance));
    }

    @Override
    public List<Instance> listActive() {
        return instances.values()
                .stream()
                .sorted(comparing(i -> i.instanceId().toString()))
                .toList();
    }

    @Override
    public void clear() {
        instances.clear();
    }
}
