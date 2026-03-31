package org.ikigaidigital.application.port.in;

/**
 * Inbound port for updating time deposit balances.
 * Implemented by use cases, called by driving adapters.
 */
public interface UpdateBalancesUseCasePort {
    /**
     * Execute the use case: recalculate and update all balances.
     */
    void execute();
}
