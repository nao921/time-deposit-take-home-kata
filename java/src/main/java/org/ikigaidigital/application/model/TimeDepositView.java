package org.ikigaidigital.application.model;

import org.ikigaidigital.domain.timedeposit.TimeDeposit;
import org.ikigaidigital.domain.withdrawal.Withdrawal;

import java.util.List;

/**
 * Application model representing a time deposit with its associated withdrawals.
 */
public record TimeDepositView(TimeDeposit deposit, List<Withdrawal> withdrawals) {}
