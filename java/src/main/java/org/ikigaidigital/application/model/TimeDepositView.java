package org.ikigaidigital.application.model;

import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;

import java.util.List;

/**
 * Application model representing a time deposit with its associated withdrawals.
 */
public record TimeDepositView(TimeDeposit deposit, List<Withdrawal> withdrawals) {}
