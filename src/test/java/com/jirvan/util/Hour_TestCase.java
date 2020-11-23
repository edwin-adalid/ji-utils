package com.jirvan.util;

import com.jirvan.dates.*;
import junit.framework.*;

public class Hour_TestCase extends TestCase {

    public void test_constructor() {
        Hour hour = new Hour(1992, 1, 26, 10);
        Assert.assertEquals("Unexpected year", 1992, hour.getYear());
        Assert.assertEquals("Unexpected month", 1, hour.getMonthInYear());
        Assert.assertEquals("Unexpected day", 26, hour.getDayInMonth());
        Assert.assertEquals("Unexpected hour", 10, hour.getHourInDay());
    }

    public void test_toString() {
        Hour hour = new Hour(1992, 1, 26, 11);
        Assert.assertEquals("Unexpected hour string", "1992-01-26 11", hour.toString());
        hour = new Hour(56, 7, 6, 9);
        Assert.assertEquals("Unexpected date string", "0056-07-06 09", hour.toString());
    }

    public void test_toFilenameSafeString() {
        Hour hour = new Hour(1992, 1, 26, 14);
        Assert.assertEquals("Unexpected hour string", "19920126-14", hour.toFilenameSafeString());
        hour = new Hour(56, 7, 6, 9);
        Assert.assertEquals("Unexpected hour string", "00560706-09", hour.toFilenameSafeString());
    }

}
