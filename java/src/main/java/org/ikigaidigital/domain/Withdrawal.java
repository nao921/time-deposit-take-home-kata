package org.ikigaidigital.domain;

import java.time.LocalDate;

/**
 * Domain value object representing a withdrawal from a specific time deposit.
 */
public record Withdrawal(int id, int timeDepositId, Double amount, LocalDate date) {}

