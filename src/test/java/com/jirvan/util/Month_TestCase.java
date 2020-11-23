package com.jirvan.util;

import com.jirvan.dates.*;
import junit.framework.*;

public class Month_TestCase extends TestCase {

    public void test_constructor() {
        Month day = new Month(1992, 1);
        Assert.assertEquals("Unexpected year", 1992, day.getYear());
        Assert.assertEquals("Unexpected month", 1, day.getMonthInYear());
    }

    public void test_toString() {
        Month month = new Month(1992, 1);
        Assert.assertEquals("Unexpected date string", "1992-01", month.toString());
        month = new Month(56, 7);
        Assert.assertEquals("Unexpected date string", "0056-07", month.toString());
    }

    public void test_toFilenameSafeString() {
        Month month = new Month(1992, 1);
        Assert.assertEquals("Unexpected month string", "199201", month.toFilenameSafeString());
        month = new Month(56, 7);
        Assert.assertEquals("Unexpected month string", "005607", month.toFilenameSafeString());
    }

    public void test_fromString() {

        Month month = Month.fromString(null);
        Assert.assertNull("Expected Month to be null", month);

        month = Month.fromString("1992-01");
        Assert.assertEquals("Unexpected year", 1992, month.getYear());
        Assert.assertEquals("Unexpected month", 1, month.getMonthInYear());

        month = Month.fromString("0056-07");
        Assert.assertEquals("Unexpected year", 56, month.getYear());
        Assert.assertEquals("Unexpected month", 7, month.getMonthInYear());

        try {
            month = Month.fromString("2012-07-08");
            fail("Expected format error");
        } catch (Exception e) {
        }

    }

}
