package com.example.loadbalancer.domain.model;

import com.example.loadbalancer.domain.RequestHandler;
import com.example.loadbalancer.domain.RequestHandlingException;
import com.example.loadbalancer.domain.metrics.Metrics;

import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.hash;

public class Instance implements RequestHandler {

    private final InstanceId instanceId;
    private final InetAddress address;
    private final RequestHandler delegate;
    private final AtomicLong callCount = new AtomicLong(0L);

    public Instance(InstanceId instanceId, InetAddress address, RequestHandler delegate) {
        this.instanceId = instanceId;
        this.address = address;
        this.delegate = delegate;
    }

    public InetAddress address() {
        return address;
    }

    public Metrics metrics() {
        return new Metrics(callCount.intValue());
    }

    public InstanceId instanceId() {
        return instanceId;
    }

    @Override
    public Response handle(Request request) throws RequestHandlingException {
        try {
            return delegate.handle(request);
        } finally {
            callCount.incrementAndGet();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instance instance = (Instance) o;
        return Objects.equals(instanceId, instance.instanceId) && Objects.equals(address, instance.address);
    }

    @Override
    public int hashCode() {
        return hash(instanceId, address);
    }
}
