package org.ikigaidigital.domain;

class TimeDepositPlanFactory {
    private static final TimeDepositPlan BASIC = new BasicTimeDepositPlan();
    private static final TimeDepositPlan STUDENT = new StudentTimeDepositPlan();
    private static final TimeDepositPlan PREMIUM = new PremiumTimeDepositPlan();
    private static final TimeDepositPlan NO_INTEREST = new NoInterestTimeDepositPlan();

    public TimeDepositPlan from(String planType) {
        return switch (planType) {
            case "student" -> STUDENT;
            case "premium" -> PREMIUM;
            case "basic" -> BASIC;
            default -> NO_INTEREST;
        };
    }
}
