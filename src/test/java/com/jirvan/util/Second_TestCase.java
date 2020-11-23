package com.jirvan.util;

import com.jirvan.dates.*;
import junit.framework.*;

public class Second_TestCase extends TestCase {

    public void test_constructor() {
        Second second = new Second(1992, 1, 26, 10, 30, 12);
        Assert.assertEquals("Unexpected year", 1992, second.getYear());
        Assert.assertEquals("Unexpected month", 1, second.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, second.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 10, second.getHourInDay());
        Assert.assertEquals("Unexpected minute", 30, second.getMinuteInHour());
        Assert.assertEquals("Unexpected second", 12, second.getSecondInMinute());
    }

    public void test_toString() {
        Second second = new Second(1992, 1, 26, 10, 30, 12);
        Assert.assertEquals("Unexpected date string", "1992-01-26 10:30:12", second.toString());
        second = new Second(56, 7, 6, 9, 3, 34);
        Assert.assertEquals("Unexpected date string", "0056-07-06 09:03:34", second.toString());
    }

    public void test_toFilenameSafeString() {
        Second second = new Second(1992, 1, 26, 10, 30, 12);
        Assert.assertEquals("Unexpected second string", "19920126-1030-12", second.toFilenameSafeString());
        second = new Second(56, 7, 6, 9, 3, 34);
        Assert.assertEquals("Unexpected second string", "00560706-0903-34", second.toFilenameSafeString());
    }

    public void test_fromString() {

        Second second = Second.fromString((String) null);
        Assert.assertNull("Expected second to be null", second);

        second = Second.fromString("1992-01-26 23:34:56");
        Assert.assertEquals("Unexpected year", 1992, second.getYear());
        Assert.assertEquals("Unexpected month", 1, second.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, second.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 23, second.getHourInDay());
        Assert.assertEquals("Unexpected minute", 34, second.getMinuteInHour());
        Assert.assertEquals("Unexpected second", 56, second.getSecondInMinute());

        second = Second.fromString("0056-07-06 06:02:34");
        Assert.assertEquals("Unexpected year", 56, second.getYear());
        Assert.assertEquals("Unexpected month", 7, second.getMonthInYear());
        Assert.assertEquals("Unexpected day", 6, second.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 6, second.getHourInDay());
        Assert.assertEquals("Unexpected minute", 2, second.getMinuteInHour());
        Assert.assertEquals("Unexpected second", 34, second.getSecondInMinute());

        try {
            second = Second.fromString("0056-07-06 3:04:23");
            fail("Expected format error");
        } catch (Exception e) {
        }

    }

}
