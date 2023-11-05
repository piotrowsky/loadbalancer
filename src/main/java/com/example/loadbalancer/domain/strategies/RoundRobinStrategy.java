package com.example.loadbalancer.domain.strategies;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.Request;
import com.example.loadbalancer.domain.registry.InstanceRegistry;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;

public class RoundRobinStrategy implements InstanceSelectionStrategy {

    private final InstanceRegistry registry;
    private final AtomicInteger robinCounter;
    private final IntSupplier numberOfActiveInstances;

    public RoundRobinStrategy(InstanceRegistry registry, IntSupplier numberOfActiveInstances) {
        this.registry = registry;
        this.robinCounter = new AtomicInteger(0);
        this.numberOfActiveInstances = numberOfActiveInstances;
    }

    @Override
    public Instance select(Request request) throws SelectionMissException {
        try {
            return registry.listActive().get(currentRobinCounter());
        } catch (IndexOutOfBoundsException ex) {
            throw new SelectionMissException(ex);
        }
    }

    @Override
    public StrategyName name() {
        return StrategyName.ROUND_ROBIN;
    }

    private int currentRobinCounter() {
        return robinCounter.getAndUpdate(current -> current > numberOfActiveInstances.getAsInt() - 1 ? 0 : ++current);
    }
}
