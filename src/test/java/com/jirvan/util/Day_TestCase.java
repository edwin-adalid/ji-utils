package com.jirvan.util;

import com.jirvan.dates.*;
import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

public class Day_TestCase {

    @Test
    public void test_constructor() {
        Day day = new Day(1992, 1, 26);
        assertEquals("Unexpected year", 1992, day.getYear());
        assertEquals("Unexpected month", 1, day.getMonthInYear());
        assertEquals("Unexpected day", 26, day.getDayInMonth());
    }

    @Test
    public void test_toString() {
        Day dateToDay = new Day(1992, 1, 26);
        assertEquals("Unexpected date string", "1992-01-26", dateToDay.toString());
        dateToDay = new Day(56, 7, 6);
        assertEquals("Unexpected date string", "0056-07-06", dateToDay.toString());
    }

    @Test
    public void test_toJavascriptString() {

        assertEquals("Unexpected date string", "Jan 26, 1992", new Day(1992, 1, 26).toJavascriptString());
        assertEquals("Unexpected date string", "Jul 6, 0056", new Day(56, 7, 6).toJavascriptString());

        assertEquals("Unexpected date string", "Jan 26, 1992", Day.toJavascriptString(new Day(1992, 1, 26)));
        assertEquals("Unexpected date string", "Jul 6, 0056", Day.toJavascriptString(new Day(56, 7, 6)));
        assertEquals("Unexpected date string", null, Day.toJavascriptString(null));
        assertEquals("Unexpected date string", "", Day.toJavascriptString(null,""));
        assertEquals("Unexpected date string", "Zac", Day.toJavascriptString(null,"Zac"));

    }

    @Test
    public void test_format() {

        assertEquals("Unexpected date string", "26-Jan-92", new Day(1992, 1, 26).format("d-MMM-yy"));

        assertEquals("Unexpected date string", "26-Jan-92", Day.format(new Day(1992, 1, 26), "d-MMM-yy"));
        assertEquals("Unexpected date string", null, Day.format(null, "d-MMM-yy"));
        assertEquals("Unexpected date string", "", Day.format(null, "d-MMM-yy", ""));
        assertEquals("Unexpected date string", "Zac", Day.format(null, "d-MMM-yy", "Zac"));

    }

    @Test
    public void test_toFilenameSafeString() {
        Day day = new Day(1992, 1, 26);
        assertEquals("Unexpected day string", "19920126", day.toFilenameSafeString());
        day = new Day(56, 7, 6);
        assertEquals("Unexpected day string", "00560706", day.toFilenameSafeString());
    }

    @Test
    public void test_fromString() {

        Day day = Day.fromString(null);
        assertNull("Expected Day to be null", day);

        day = Day.fromString("1992-01-26");
        assertEquals("Unexpected year", 1992, day.getYear());
        assertEquals("Unexpected month", 1, day.getMonthInYear());
        assertEquals("Unexpected day", 26, day.getDayInMonth());

        day = Day.fromString("0056-07-06");
        assertEquals("Unexpected year", 56, day.getYear());
        assertEquals("Unexpected month", 7, day.getMonthInYear());
        assertEquals("Unexpected day", 6, day.getDayInMonth());

        day = Day.fromString("Jan 26, 1992");
        assertEquals("Unexpected year", 1992, day.getYear());
        assertEquals("Unexpected month", 1, day.getMonthInYear());
        assertEquals("Unexpected day", 26, day.getDayInMonth());

        assertEquals("Unexpected month", 5, Day.fromString("may 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 8, Day.fromString("AUG 26, 1992").getMonthInYear());

        assertEquals("Unexpected month", 1, Day.fromString("jan 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 2, Day.fromString("feb 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 3, Day.fromString("mar 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 4, Day.fromString("apr 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 5, Day.fromString("may 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 6, Day.fromString("jun 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 7, Day.fromString("jul 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 8, Day.fromString("aug 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 9, Day.fromString("sep 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 10, Day.fromString("oct 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 11, Day.fromString("nov 26, 1992").getMonthInYear());
        assertEquals("Unexpected month", 12, Day.fromString("dec 26, 1992").getMonthInYear());

        try {
            day = Day.fromString("56-07-06");
            fail("Expected format error");
        } catch (Exception e) {
        }

    }

}
