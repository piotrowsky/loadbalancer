package com.example.loadbalancer.domain.metrics;

import com.example.loadbalancer.domain.model.Instance;

public interface MetricStore {

    Instance lowestLoadIndex();
}
