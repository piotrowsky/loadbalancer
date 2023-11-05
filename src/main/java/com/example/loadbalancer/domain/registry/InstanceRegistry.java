package com.example.loadbalancer.domain.registry;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.InstanceId;

import java.util.List;

public interface InstanceRegistry {
    void register(Instance instance);

    void unregister(InstanceId instanceId);

    List<Instance> listActive();

    void clear();
}
