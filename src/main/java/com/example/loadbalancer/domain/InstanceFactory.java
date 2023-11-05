package com.example.loadbalancer.domain;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.InstanceId;

import java.net.InetAddress;
import java.net.URI;

public class InstanceFactory {

    Instance remote(InstanceId instanceId, InetAddress address, URI uri) {
        //TODO: handler can use WebClient
        return null;
    }
}
