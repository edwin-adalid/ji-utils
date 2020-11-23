package com.jirvan.util;

import com.jirvan.dates.*;
import junit.framework.*;

public class Minute_TestCase extends TestCase {

    public void test_constructor() {
        Minute minute = new Minute(1992, 1, 26, 10, 30);
        Assert.assertEquals("Unexpected year", 1992, minute.getYear());
        Assert.assertEquals("Unexpected month", 1, minute.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, minute.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 10, minute.getHourInDay());
        Assert.assertEquals("Unexpected minute", 30, minute.getMinuteInHour());
    }

    public void test_toString() {
        Minute minute = new Minute(1992, 1, 26, 10, 30);
        Assert.assertEquals("Unexpected date string", "1992-01-26 10:30", minute.toString());
        minute = new Minute(56, 7, 6, 9, 3);
        Assert.assertEquals("Unexpected date string", "0056-07-06 09:03", minute.toString());
    }

    public void test_toFilenameSafeString() {
        Minute minute = new Minute(1992, 1, 26, 10, 35);
        Assert.assertEquals("Unexpected minute string", "19920126-1035", minute.toFilenameSafeString());
        minute = new Minute(56, 7, 6, 9, 3);
        Assert.assertEquals("Unexpected minute string", "00560706-0903", minute.toFilenameSafeString());
    }

    public void test_fromString() {

        Minute minute = Minute.fromString((String) null);
        Assert.assertNull("Expected minute to be null", minute);

        minute = Minute.fromString("1992-01-26 23:34");
        Assert.assertEquals("Unexpected year", 1992, minute.getYear());
        Assert.assertEquals("Unexpected month", 1, minute.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, minute.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 23, minute.getHourInDay());
        Assert.assertEquals("Unexpected minute", 34, minute.getMinuteInHour());

        minute = Minute.fromString("0056-07-06 06:02");
        Assert.assertEquals("Unexpected year", 56, minute.getYear());
        Assert.assertEquals("Unexpected month", 7, minute.getMonthInYear());
        Assert.assertEquals("Unexpected day", 6, minute.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 6, minute.getHourInDay());
        Assert.assertEquals("Unexpected minute", 2, minute.getMinuteInHour());

        try {
            minute = Minute.fromString("0056-07-06 3:04");
            fail("Expected format error");
        } catch (Exception e) {
        }

    }

    public void test_minuteInDay() {

        Assert.assertEquals("Unexpected minute in day", 1174, Minute.fromString("1992-01-26 19:34").getMinuteInDay());
        Assert.assertEquals("Unexpected minute in day", 15, Minute.fromString("1992-01-26 00:15").getMinuteInDay());
        Assert.assertEquals("Unexpected minute in day", 0, Minute.fromString("1996-01-26 00:00").getMinuteInDay());
        Assert.assertEquals("Unexpected minute in day", 1, Minute.fromString("1996-01-26 00:01").getMinuteInDay());
        Assert.assertEquals("Unexpected minute in day", 1439, Minute.fromString("1992-01-26 23:59").getMinuteInDay());

    }

}
