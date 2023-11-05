package com.example.loadbalancer.domain;

public class RequestHandlingException extends Exception {

    public RequestHandlingException(Throwable cause) {
        super(cause);
    }
}
