package com.example.loadbalancer.domain.strategies;

import com.example.loadbalancer.domain.metrics.MetricStore;
import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.Request;

public class LoadMasterStrategy implements InstanceSelectionStrategy {

    private final MetricStore metricStore;

    public LoadMasterStrategy(MetricStore metricStore) {
        this.metricStore = metricStore;
    }

    @Override
    public Instance select(Request request) throws SelectionMissException {
        return metricStore.lowestLoadIndex()
                .orElseThrow(() -> new SelectionMissException("No instance with lowestLoadIndex found"));
    }

    @Override
    public StrategyName name() {
        return StrategyName.LOAD_MASTER;
    }
}
