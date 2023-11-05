package com.example.loadbalancer.domain.strategies;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.Request;
import com.example.loadbalancer.domain.registry.InstanceRegistry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntSupplier;

public class RandomStrategy implements InstanceSelectionStrategy {

    private final InstanceRegistry registry;
    private final IntSupplier numberOfActiveInstances;

    public RandomStrategy(InstanceRegistry registry, IntSupplier numberOfActiveInstances) {
        this.registry = registry;
        this.numberOfActiveInstances = numberOfActiveInstances;
    }

    @Override
    public Instance select(Request request) throws SelectionMissException {
        try {
            return registry.listActive().get(random());
        } catch (IndexOutOfBoundsException ex) {
            throw new SelectionMissException(ex);
        }
    }

    @Override
    public StrategyName name() {
        return StrategyName.RANDOM;
    }

    private int random() {
        return ThreadLocalRandom.current().nextInt(numberOfActiveInstances.getAsInt());
    }
}
