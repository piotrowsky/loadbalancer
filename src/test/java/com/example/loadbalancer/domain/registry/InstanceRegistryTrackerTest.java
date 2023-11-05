package com.example.loadbalancer.domain.registry;

import com.example.loadbalancer.domain.model.*;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InstanceRegistryTrackerTest {

    InstanceRegistry registry = mock(InstanceRegistry.class);
    InstanceRegistryTracker tracker = new InstanceRegistryTracker(registry);

    @Test
    void should_increase_number_of_active_instances_on_registered_event() throws UnknownHostException {
        // given
        var instanceId = new InstanceId(randomUUID().toString());
        var instance = new Instance(
                instanceId,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId, "response"));
        var event = new InstanceRegisteredEvent(instance);
        assertThat(tracker.numberOfActiveInstances()).isEqualTo(0);

        // when
        tracker.consume(event);

        // then
        assertThat(tracker.numberOfActiveInstances()).isEqualTo(1);
    }

    @Test
    void should_decrease_number_of_active_instances_on_unregistered_event() throws UnknownHostException {
        // given
        var instanceId = new InstanceId(randomUUID().toString());
        var instance = new Instance(
                instanceId,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId, "response"));
        tracker.consume(new InstanceRegisteredEvent(instance));

        var event = new InstanceUnregisteredEvent(instance);

        // when
        tracker.consume(event);

        // then
        assertThat(tracker.numberOfActiveInstances()).isEqualTo(0);
    }
}