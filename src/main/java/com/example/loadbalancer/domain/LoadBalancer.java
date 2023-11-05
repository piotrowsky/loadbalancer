package com.example.loadbalancer.domain;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.InstanceId;
import com.example.loadbalancer.domain.model.Request;
import com.example.loadbalancer.domain.model.Response;
import com.example.loadbalancer.domain.registry.InstanceRegistry;
import com.example.loadbalancer.domain.strategies.InstanceSelectionStrategy;
import io.vavr.control.Try;

public class LoadBalancer implements RequestHandler {

    private final InstanceSelectionStrategy strategy;
    private final InstanceRegistry registry;

    public LoadBalancer(InstanceSelectionStrategy strategy, InstanceRegistry registry) {
        this.strategy = strategy;
        this.registry = registry;
    }

    @Override
    public Response handle(Request request) throws RequestHandlingException {
        return Try.of(() -> strategy.select(request).handle(request))
                .getOrElseThrow(RequestHandlingException::new);
    }

    public void register(Instance instance) {
        registry.register(instance);
    }

    public void unregister(InstanceId instanceId) {
        registry.unregister(instanceId);
    }
}
