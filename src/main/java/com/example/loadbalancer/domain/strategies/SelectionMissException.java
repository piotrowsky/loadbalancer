package com.example.loadbalancer.domain.strategies;

public class SelectionMissException extends Exception {
    public SelectionMissException(IndexOutOfBoundsException cause) {
        super(cause);
    }

    public SelectionMissException(String message) {
        super(message);
    }
}
