package com.jirvan.util;

import com.jirvan.dates.*;
import junit.framework.*;

public class Millisecond_TestCase extends TestCase {

    public void test_constructor() {
        Millisecond millisecond = new Millisecond(1992, 1, 26, 10, 30, 12, 23);
        Assert.assertEquals("Unexpected year", 1992, millisecond.getYear());
        Assert.assertEquals("Unexpected month", 1, millisecond.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, millisecond.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 10, millisecond.getHourInDay());
        Assert.assertEquals("Unexpected minute", 30, millisecond.getMinuteInHour());
        Assert.assertEquals("Unexpected second", 12, millisecond.getSecondInMinute());
        Assert.assertEquals("Unexpected millisecond", 23, millisecond.getMillisecondInSecond());
    }

    public void test_toString() {
        Millisecond millisecond = new Millisecond(1992, 1, 26, 10, 30, 12, 45);
        Assert.assertEquals("Unexpected milisecond string", "1992-01-26 10:30:12.045", millisecond.toString());
        millisecond = new Millisecond(56, 7, 6, 9, 3, 34, 8);
        Assert.assertEquals("Unexpected millisecond string", "0056-07-06 09:03:34.008", millisecond.toString());
    }

    public void test_toFilenameSafeString() {
        Millisecond millisecond = new Millisecond(1992, 1, 26, 10, 30, 12, 45);
        Assert.assertEquals("Unexpected milisecond string", "19920126-1030-12.045", millisecond.toFilenameSafeString());
        millisecond = new Millisecond(56, 7, 6, 9, 3, 34, 8);
        Assert.assertEquals("Unexpected millisecond string", "00560706-0903-34.008", millisecond.toFilenameSafeString());
    }

    public void test_fromString() {

        Millisecond millisecond = Millisecond.fromString((String) null);
        Assert.assertNull("Expected millisecond to be null", millisecond);

        millisecond = Millisecond.fromString("1992-01-26 23:34:56.003");
        Assert.assertEquals("Unexpected year", 1992, millisecond.getYear());
        Assert.assertEquals("Unexpected month", 1, millisecond.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, millisecond.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 23, millisecond.getHourInDay());
        Assert.assertEquals("Unexpected minute", 34, millisecond.getMinuteInHour());
        Assert.assertEquals("Unexpected second", 56, millisecond.getSecondInMinute());
        Assert.assertEquals("Unexpected millisecond", 3, millisecond.getMillisecondInSecond());

        millisecond = Millisecond.fromString("0056-07-06 06:02:34.387");
        Assert.assertEquals("Unexpected year", 56, millisecond.getYear());
        Assert.assertEquals("Unexpected month", 7, millisecond.getMonthInYear());
        Assert.assertEquals("Unexpected day", 6, millisecond.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 6, millisecond.getHourInDay());
        Assert.assertEquals("Unexpected minute", 2, millisecond.getMinuteInHour());
        Assert.assertEquals("Unexpected second", 34, millisecond.getSecondInMinute());
        Assert.assertEquals("Unexpected millisecond", 387, millisecond.getMillisecondInSecond());

        try {
            millisecond = Millisecond.fromString("0056-07-06 03:04:23.");
            fail("Expected format error");
        } catch (Exception e) {
        }

    }

}
