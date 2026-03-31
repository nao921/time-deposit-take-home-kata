package org.ikigaidigital.domain.withdrawal;

import java.time.LocalDate;

/**
 * Domain value object representing a withdrawal from a specific time deposit.
 */
public record Withdrawal(int id, int timeDepositId, Double amount, LocalDate date) {}
