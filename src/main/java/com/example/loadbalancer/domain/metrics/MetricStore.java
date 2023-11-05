package com.example.loadbalancer.domain.metrics;

import com.example.loadbalancer.domain.model.Instance;

import java.util.Optional;

public interface MetricStore {

    Optional<Instance> lowestLoadIndex();
}
