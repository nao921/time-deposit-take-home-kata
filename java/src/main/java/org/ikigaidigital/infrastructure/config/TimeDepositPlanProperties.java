package org.ikigaidigital.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "time-deposit")
public class TimeDepositPlanProperties {
    private int gracePeriodDays;
    private int monthsInYear;
    private Plans plans;

    public int getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(int gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public int getMonthsInYear() {
        return monthsInYear;
    }

    public void setMonthsInYear(int monthsInYear) {
        this.monthsInYear = monthsInYear;
    }

    public Plans getPlans() {
        return plans;
    }

    public void setPlans(Plans plans) {
        this.plans = plans;
    }

    public static class Plans {
        private Basic basic;
        private Student student;
        private Premium premium;

        public Basic getBasic() {
            return basic;
        }

        public void setBasic(Basic basic) {
            this.basic = basic;
        }

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public Premium getPremium() {
            return premium;
        }

        public void setPremium(Premium premium) {
            this.premium = premium;
        }
    }

    public static class Basic {
        private double interestRate;

        public double getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(double interestRate) {
            this.interestRate = interestRate;
        }
    }

    public static class Student {
        private double interestRate;
        private int maxInterestDays;

        public double getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(double interestRate) {
            this.interestRate = interestRate;
        }

        public int getMaxInterestDays() {
            return maxInterestDays;
        }

        public void setMaxInterestDays(int maxInterestDays) {
            this.maxInterestDays = maxInterestDays;
        }
    }

    public static class Premium {
        private double interestRate;
        private int interestStartDays;

        public double getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(double interestRate) {
            this.interestRate = interestRate;
        }

        public int getInterestStartDays() {
            return interestStartDays;
        }

        public void setInterestStartDays(int interestStartDays) {
            this.interestStartDays = interestStartDays;
        }
    }
}
