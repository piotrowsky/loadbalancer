package com.example.loadbalancer.app;

import com.example.loadbalancer.domain.LoadBalancer;
import com.example.loadbalancer.domain.metrics.InMemoryMetricsStore;
import com.example.loadbalancer.domain.registry.InMemoryInstanceRegistry;
import com.example.loadbalancer.domain.registry.InstanceRegistry;
import com.example.loadbalancer.domain.registry.InstanceRegistryConsumer;
import com.example.loadbalancer.domain.registry.InstanceRegistryTracker;
import com.example.loadbalancer.domain.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.Set;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

@Configuration
public class AppConfiguration {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AppConfiguration(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    @Scope("singleton")
    public LoadBalancer loadBalancer(InstanceRegistry instanceRegistry,
                                     ConfigurableInstanceSelectionStrategy configurableInstanceSelectionStrategy) {
        return new LoadBalancer(configurableInstanceSelectionStrategy, instanceRegistry);
    }

    @Bean
    @Scope("singleton")
    public ConfigurableInstanceSelectionStrategy configurableInstanceSelectionStrategy(InstanceRegistry instanceRegistry,
                                                                                       InstanceRegistryTracker instanceRegistryTracker) {
        return new DefaultConfigurableInstanceSelectionStrategy(Set.of(
                new RetriableStrategy(
                        new RandomStrategy(instanceRegistry, instanceRegistryTracker::numberOfActiveInstances)
                ),
                new RetriableStrategy(
                        new RoundRobinStrategy(instanceRegistry, instanceRegistryTracker::numberOfActiveInstances)
                ),
                new LoadMasterStrategy(
                        new InMemoryMetricsStore(newSingleThreadScheduledExecutor(), instanceRegistry)
                )
        ));
    }

    @Bean
    @Scope("singleton")
    public InstanceRegistry instanceRegistry() {
        return new InMemoryInstanceRegistry(new SpringEventPublisher(applicationEventPublisher));
    }

    @Bean
    @Scope("singleton")
    public InstanceRegistryTracker instanceRegistryTracker(InstanceRegistry instanceRegistry) {
        return new InstanceRegistryTracker(instanceRegistry);
    }

    @Bean
    @Scope("singleton")
    public InstanceRegistryConsumer instanceRegistryConsumer(InstanceRegistryTracker instanceRegistryTracker) {
        return new SpringEventConsumer(instanceRegistryTracker);
    }

    @Bean //async eventing
    @Scope("singleton")
    public ApplicationEventMulticaster applicationEventMulticaster() {
        final var multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return multicaster;
    }
}
