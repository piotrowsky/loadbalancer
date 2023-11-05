package com.example.loadbalancer.domain.strategies;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.Request;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class DefaultConfigurableInstanceSelectionStrategy implements ConfigurableInstanceSelectionStrategy {

    private final Map<StrategyName, InstanceSelectionStrategy> map;
    private final AtomicReference<InstanceSelectionStrategy> current = new AtomicReference<>();

    public DefaultConfigurableInstanceSelectionStrategy(Set<InstanceSelectionStrategy> strategies) {
        this.map = strategies.stream()
                .collect(toMap(InstanceSelectionStrategy::name, identity()));
        final var defaultStrategy = strategies.stream()
                .min(comparing(s -> s.name().toString()))
                .orElseThrow(() -> new StrategySelectionException("At least one strategy is expected"));
        this.current.set(defaultStrategy);
    }

    @Override
    public void use(StrategyName strategyName) {
        final var newCurrent = ofNullable(map.get(strategyName))
                .orElseThrow(() -> new StrategySelectionException("Unsupported strategy: " + strategyName));
        current.set(newCurrent);
    }

    @Override
    public Instance select(Request request) throws SelectionMissException {
        return current.get().select(request);
    }

    @Override
    public StrategyName name() {
        throw new UnsupportedOperationException();
    }
}
