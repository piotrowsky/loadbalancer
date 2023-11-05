package com.example.loadbalancer.domain.strategies;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.Request;

public interface InstanceSelectionStrategy {

    Instance select(Request request) throws SelectionMissException;

    StrategyName name();
}
