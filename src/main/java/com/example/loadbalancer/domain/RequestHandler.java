package com.example.loadbalancer.domain;

import com.example.loadbalancer.domain.model.Request;
import com.example.loadbalancer.domain.model.Response;

public interface RequestHandler {

    Response handle(Request request) throws RequestHandlingException;
}
