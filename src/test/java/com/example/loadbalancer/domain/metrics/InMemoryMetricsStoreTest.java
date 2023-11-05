package com.example.loadbalancer.domain.metrics;

import com.example.loadbalancer.domain.RequestHandlingException;
import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.InstanceId;
import com.example.loadbalancer.domain.model.Request;
import com.example.loadbalancer.domain.model.Response;
import com.example.loadbalancer.domain.registry.InstanceRegistry;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class InMemoryMetricsStoreTest {

    InstanceRegistry registry = mock(InstanceRegistry.class);
    ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);

    @Test
    void should_return_empty_result_when_lookup_is_not_initialized() {
        // given
        given(executorService.scheduleAtFixedRate(any(Runnable.class), eq(0L), eq(1L), eq(SECONDS)))
                .willAnswer(inv -> null);
        var store = new InMemoryMetricsStore(executorService, registry);

        // when
        var result = store.lowestLoadIndex();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_empty_result_when_no_instance_with_lowest_load_found() {
        // given
        given(executorService.scheduleAtFixedRate(any(Runnable.class), eq(0L), eq(1L), eq(SECONDS)))
                .willAnswer(inv -> {
                    inv.getArgument(0, Runnable.class).run();
                    return null;
                });
        var store = new InMemoryMetricsStore(executorService, registry);

        // when
        var result = store.lowestLoadIndex();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_empty_result_when_background_calculation_failed() {
        // given
        given(executorService.scheduleAtFixedRate(any(Runnable.class), eq(0L), eq(1L), eq(SECONDS)))
                .willAnswer(inv -> {
                    inv.getArgument(0, Runnable.class).run();
                    return null;
                });
        given(registry.listActive()).willThrow(RuntimeException.class);

        var store = new InMemoryMetricsStore(executorService, registry);

        // when
        var result = store.lowestLoadIndex();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_not_empty_result_when_instance_with_lowest_load_found() throws UnknownHostException, RequestHandlingException {
        // given
        given(executorService.scheduleAtFixedRate(any(), anyLong(), anyLong(), any()))
                .willAnswer(inv -> {
                    inv.getArgument(0, Runnable.class).run();
                    return null;
                });

        var instanceId1 = new InstanceId(randomUUID().toString());
        var instanceNotCalledAtAll = new Instance(
                instanceId1,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId1, "response1"));

        var instanceId2 = new InstanceId(randomUUID().toString());
        var instanceCalledOnce = new Instance(
                instanceId2,
                InetAddress.getLocalHost(),
                request -> new Response(instanceId2, "response2"));
        instanceCalledOnce.handle(new Request("request"));

        given(registry.listActive()).willReturn(List.of(instanceNotCalledAtAll, instanceCalledOnce));

        var store = new InMemoryMetricsStore(executorService, registry);

        // when
        var result = store.lowestLoadIndex();

        // then
        assertThat(result).hasValue(instanceNotCalledAtAll);
    }
}