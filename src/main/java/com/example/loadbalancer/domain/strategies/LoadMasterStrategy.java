package com.example.loadbalancer.domain.strategies;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.Request;
import com.example.loadbalancer.domain.metrics.MetricStore;

public class LoadMasterStrategy implements InstanceSelectionStrategy {

    private final MetricStore metricStore;

    public LoadMasterStrategy(MetricStore metricStore) {
        this.metricStore = metricStore;
    }

    @Override
    public Instance select(Request request) {
        return metricStore.lowestLoadIndex();
    }

    @Override
    public StrategyName name() {
        return StrategyName.LOAD_MASTER;
    }
}
