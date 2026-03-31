package org.ikigaidigital.domain;

import org.springframework.stereotype.Component;

@Component
public class TimeDepositPlanFactory {
    private final BasicTimeDepositPlan basic;
    private final StudentTimeDepositPlan student;
    private final PremiumTimeDepositPlan premium;
    private final NoInterestTimeDepositPlan noInterest;

    public TimeDepositPlanFactory(BasicTimeDepositPlan basic,
                                  StudentTimeDepositPlan student,
                                  PremiumTimeDepositPlan premium) {
        this.basic = basic;
        this.student = student;
        this.premium = premium;
        this.noInterest = new NoInterestTimeDepositPlan();
    }

    public TimeDepositPlan from(String planType) {
        return switch (planType) {
            case "student" -> student;
            case "premium" -> premium;
            case "basic" -> basic;
            default -> noInterest;
        };
    }
}
