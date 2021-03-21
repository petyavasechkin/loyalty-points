package com.bank.loyaltypoints.utils;

import com.bank.loyaltypoints.model.CalcPointsContext;
import com.bank.loyaltypoints.model.Transaction;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UtilityTest {

    @Test
    void getAmountStrFromCentsTest1() {
        String result = Utility.getAmountStrFromCents(100525);

        assertThat(result).isEqualTo("1005.25");
    }

    @Test
    void getAmountStrFromCentsTest2() {
        String result = Utility.getAmountStrFromCents(34576);

        assertThat(result).isEqualTo("345.76");
    }

    @Test
    void getCentsFromAmountStrTest1() {
        int cents = Utility.getCentsFromAmountStr("244.74");

        assertThat(cents).isEqualTo(24474);
    }

    @Test
    void getCentsFromAmountStrTest2() {
        int cents = Utility.getCentsFromAmountStr("244.0");

        assertThat(cents).isEqualTo(24400);
    }

    @Test
    void getCentsFromAmountStrTest3() {
        int cents = Utility.getCentsFromAmountStr("244");

        assertThat(cents).isEqualTo(24400);
    }

    @Test
    void getCentsFromAmountStrTest4() {
        int cents = Utility.getCentsFromAmountStr("244.");

        assertThat(cents).isEqualTo(24400);
    }

    @Test
    void getCentsFromAmountStrTest5() {
        int cents = Utility.getCentsFromAmountStr("244.1");

        assertThat(cents).isEqualTo(24410);
    }

    @Test
    void calculatePointsTest() {
        CalcPointsContext ctx = new CalcPointsContext(createDummyTransaction(),
                5000, 7500, 1, 2, 3);

        int points = Utility.calculatePoints(ctx);

        assertThat(points).isEqualTo(10900);
    }

    @Test
    void isCurrentWeekDateTest1() {
        LocalDate date = LocalDate.now().minusDays(8);

        boolean result = Utility.isCurrentWeekDate(date);

        assertThat(result).isFalse();
    }

    @Test
    void isCurrentWeekDateTest2() {
        LocalDate date = LocalDate.now().plusDays(8);

        boolean result = Utility.isCurrentWeekDate(date);

        assertThat(result).isFalse();
    }

    @Test
    void isCurrentWeekDateTest3() {
        LocalDate date = LocalDate.now().minusDays(1);
        if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            date = date.plusDays(2);
        }

        boolean result = Utility.isCurrentWeekDate(date);

        assertThat(result).isTrue();
    }

    @Test
    void isCurrentWeekDateTest4() {
        LocalDate date = LocalDate.now().plusDays(1);
        if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            date = date.minusDays(2);
        }

        boolean result = Utility.isCurrentWeekDate(date);

        assertThat(result).isTrue();
    }

    private Transaction createDummyTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(7800);
        return transaction;
    }
}