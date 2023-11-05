package com.example.loadbalancer.domain.registry;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.InstanceId;
import com.example.loadbalancer.domain.model.Response;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InMemoryInstanceRegistryTest {
    InstanceRegistryPublisher publisher = mock(InstanceRegistryPublisher.class);
    InMemoryInstanceRegistry registry = new InMemoryInstanceRegistry(publisher);

    @Test
    void should_register_instance() throws UnknownHostException {
        // given
        var instanceId = new InstanceId(randomUUID().toString());
        var instance = new Instance(
                instanceId,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId, "response"));

        // when
        registry.register(instance);

        // then
        assertThat(registry.listActive()).containsOnly(instance);
    }

    @Test
    void should_unregister_instance() throws UnknownHostException {
        // given
        var instanceId = new InstanceId(randomUUID().toString());
        var instance = new Instance(
                instanceId,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId, "response"));
        registry.register(instance);

        // when
        registry.unregister(instanceId);

        // then
        assertThat(registry.listActive()).isEmpty();
    }

    @Test
    void should_list_active_instances() throws UnknownHostException {
        // given
        var instanceId1 = new InstanceId(randomUUID().toString());
        var instance1 = new Instance(
                instanceId1,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId1, "response1"));
        registry.register(instance1);

        // and
        var instanceId2 = new InstanceId(randomUUID().toString());
        var instance2 = new Instance(
                instanceId2,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId2, "response2"));
        registry.register(instance2);
        registry.unregister(instanceId2);

        // when
        var result = registry.listActive();

        // then
        assertThat(result).containsOnly(instance1);
    }

    @Test
    void should_clear_instances() throws UnknownHostException {
        // given
        var instanceId = new InstanceId(randomUUID().toString());
        var instance = new Instance(
                instanceId,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId, "response"));
        registry.register(instance);

        // when
        registry.clear();

        // then
        assertThat(registry.listActive()).isEmpty();
    }
}