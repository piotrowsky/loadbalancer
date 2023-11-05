package com.example.loadbalancer.domain.strategies;

import com.example.loadbalancer.domain.model.Instance;
import com.example.loadbalancer.domain.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetriableStrategy implements InstanceSelectionStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(RetriableStrategy.class);

    private final InstanceSelectionStrategy delegate;

    public RetriableStrategy(InstanceSelectionStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public Instance select(Request request) throws SelectionMissException {
        for(int tryNumber = 0; true; ++tryNumber) {
            try {
                return delegate.select(request);
            } catch (SelectionMissException ex) {
                if (tryNumber == 2) {
                    throw ex;
                }
                LOG.warn("Failed to select strategy, retrying[{}]", tryNumber, ex);
            }
        }
    }

    @Override
    public StrategyName name() {
        return delegate.name();
    }
}
