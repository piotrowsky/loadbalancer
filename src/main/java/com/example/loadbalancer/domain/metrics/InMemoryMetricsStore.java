package com.example.loadbalancer.domain.metrics;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.registry.InstanceRegistry;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Comparator.comparing;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class InMemoryMetricsStore implements MetricStore {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryMetricsStore.class);

    private final AtomicReference<Instance> lowestLoadIndex = new AtomicReference<>();

    public InMemoryMetricsStore(InstanceRegistry registry) {
        newSingleThreadScheduledExecutor().scheduleAtFixedRate(() ->
            Try.run(() -> {
                final var instanceWithLowestLoadIndex = registry.listActive().stream()
                        .min(comparing(i -> i.metrics().loadIndex()));
                if (instanceWithLowestLoadIndex.isEmpty()) {
                    LOG.debug("No instance registered yet. Will try again later");
                    return;
                }
                lowestLoadIndex.set(instanceWithLowestLoadIndex.orElseThrow());
            }).onFailure(ex -> LOG.warn("Failed to calculate instance with lower load, will try again in about 1000 ms...", ex)),
                0, 1, TimeUnit.SECONDS);
    }

    @Override
    public Instance lowestLoadIndex() {
        return lowestLoadIndex.get();
    }
}
