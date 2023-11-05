package com.example.loadbalancer.domain.strategies;

public class StrategySelectionException extends RuntimeException {
    public StrategySelectionException(String message) {
        super(message);
    }
}
