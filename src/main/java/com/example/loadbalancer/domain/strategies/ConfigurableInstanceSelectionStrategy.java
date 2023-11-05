package com.example.loadbalancer.domain.strategies;

public interface ConfigurableInstanceSelectionStrategy extends InstanceSelectionStrategy {

    void use(StrategyName strategyName) throws StrategySelectionException;
}
