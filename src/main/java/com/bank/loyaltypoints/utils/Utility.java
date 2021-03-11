package com.bank.loyaltypoints.utils;

import com.bank.loyaltypoints.model.CalcPointsContext;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class Utility {

    private static final String CURRENCY_DELIMITER = "\\.";

    private Utility() {
        //private constructor
    }

    public static String getAmountStrFromCents(int cents) {
        int euro = cents / 100;
        int remains = cents % 100;
        return String.format("%s.%s", euro, remains);
    }

    public static int getCentsFromAmountStr(String amount) {
        String[] amountParts = amount.split(CURRENCY_DELIMITER);
        int firstPartCents = Integer.parseInt(amountParts[0]) * 100;
        if (amountParts.length < 2) {
            return firstPartCents;
        } else if (amountParts[1].length() == 0) {
            return firstPartCents;
        } else if (amountParts[1].length() == 1) {
            return firstPartCents + Integer.parseInt(amountParts[1]) * 10;
        } else {
            return firstPartCents + Integer.parseInt(amountParts[1]);
        }
    }

    public static int calculatePoints(CalcPointsContext ctx) {
        int amount = ctx.getTransaction().getAmount();
        int points;
        if (amount > ctx.getSecondBound()) {
            points = 10000 + (amount - ctx.getSecondBound()) * ctx.getThirdFactor();
        } else if (amount > ctx.getFirstBound()) {
            points = 5000 + (amount - ctx.getFirstBound()) * ctx.getSecondFactor();
        } else {
            points = amount * ctx.getFirstFactor();
        }
        return points;
    }

    public static boolean isCurrentWeekDate(LocalDate date) {
        LocalDate now = LocalDate.now();
        LocalDate mondayBeforeNow = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sundayAfterNow = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return ((date.isEqual(mondayBeforeNow) || date.isAfter(mondayBeforeNow))
                && (date.isEqual(sundayAfterNow) || date.isBefore(sundayAfterNow)));
    }
}
