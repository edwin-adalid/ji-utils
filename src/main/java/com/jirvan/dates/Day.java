/*

Copyright (c) 2012,2013 Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.dates;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is primarily here to get around the standard java Date class's
 * entanglement with timezone along with providing an fixed
 * "granularity" of a day.  It is mainly for using with dates that have no
 * need for association with a time zone.  The association of a time zone can
 * actually cause real problems in these situations.  For example if a persons
 * birthday is the first of May, then it is always the 1st of May regardless of
 * what timezone they were born in or where they are now.  At the moment a
 * Gregorian calendar is assumed.
 */
@Deprecated // Now that LocalDate is available you really should use that
public class Day implements Cloneable, Serializable, Comparable<Day> {

    private static final DateFormat JAVASCRIPT_DAY_FORMAT = new SimpleDateFormat("MMM d, yyyy");

    private int year;
    private int monthInYear;
    private int dayInMonth;

    public Day() {
        this(new Date());
    }

    public Day(int year, int monthInYear, int dayInMonth) {
        this.year = year;
        this.monthInYear = monthInYear;
        this.dayInMonth = dayInMonth;
    }

    @Override public Day clone() {
        return new Day(year, monthInYear, dayInMonth);
    }

    private Day(LocalDate localDate) {
        this.year = localDate.getYear();
        this.monthInYear = localDate.getMonthValue();
        this.dayInMonth = localDate.getDayOfMonth();
    }

    private Day(GregorianCalendar calendar) {
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    private Day(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public Day(Date date, TimeZone timeZone) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
    }


    public static Day from(LocalDate localDate) {
        return localDate == null ? null : new Day(localDate);
    }

    public static Day from(GregorianCalendar calendar) {
        return calendar == null ? null : new Day(calendar);
    }

    public static Day from(Date date) {
        return date == null ? null : new Day(date);
    }

    public static Day from(Timestamp timestamp) {
        return timestamp == null ? null : new Day(timestamp);
    }

    public static Day from(Long date) {
        return date == null ? null : new Day(new Date(date));
    }

    public static Day from(Date date, TimeZone timeZone) {
        return date == null ? null : new Day(date, timeZone);
    }

    public static Day today() {
        return new Day();
    }

    public int getYear() {
        return year;
    }

    public Day setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMonthInYear() {
        return monthInYear;
    }

    public Day setMonthInYear(int monthInYear) {
        this.monthInYear = monthInYear;
        return this;
    }

    public int getDayInMonth() {
        return dayInMonth;
    }

    public Day setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
        return this;
    }

    public Date getDate() {
        return getCalendar().getTime();
    }

    public Timestamp getTimestamp() {
        return new Timestamp(getCalendar().getTimeInMillis());
    }

    public GregorianCalendar getCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar;
    }

    public Month getMonth() {
        return new Month(year, monthInYear);
    }

    public DayOfWeek getDayOfWeek() {
        int dayOfWeek = getCalendar().get(GregorianCalendar.DAY_OF_WEEK);
        if (dayOfWeek == GregorianCalendar.MONDAY) {
            return DayOfWeek.Monday;
        } else if (dayOfWeek == GregorianCalendar.TUESDAY) {
            return DayOfWeek.Tuesday;
        } else if (dayOfWeek == GregorianCalendar.WEDNESDAY) {
            return DayOfWeek.Wednesday;
        } else if (dayOfWeek == GregorianCalendar.THURSDAY) {
            return DayOfWeek.Thursday;
        } else if (dayOfWeek == GregorianCalendar.FRIDAY) {
            return DayOfWeek.Friday;
        } else if (dayOfWeek == GregorianCalendar.SATURDAY) {
            return DayOfWeek.Saturday;
        } else if (dayOfWeek == GregorianCalendar.SUNDAY) {
            return DayOfWeek.Sunday;
        } else {
            throw new RuntimeException(String.format("Unexpected error: Unrecognized GregorianCalendar.DAY_OF_WEEK %d", dayOfWeek));
        }
    }

    public boolean isOnAWeekend() {
        DayOfWeek dayOfWeek = getDayOfWeek();
        return dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday;
    }

    public boolean isAWeekday() {
        DayOfWeek dayOfWeek = getDayOfWeek();
        return dayOfWeek != DayOfWeek.Saturday && dayOfWeek != DayOfWeek.Sunday;
    }

    public Day next() {
        return advanced(1);
    }

    public static Day firstSunday() {
        return new Day().first(DayOfWeek.Sunday);
    }

    public static Day firstMonday() {
        return new Day().first(DayOfWeek.Monday);
    }

    public static Day firstTuesday() {
        return new Day().first(DayOfWeek.Tuesday);
    }

    public static Day firstWednesday() {
        return new Day().first(DayOfWeek.Wednesday);
    }

    public static Day firstThursday() {
        return new Day().first(DayOfWeek.Thursday);
    }

    public static Day firstFriday() {
        return new Day().first(DayOfWeek.Friday);
    }

    public static Day firstSaturday() {
        return new Day().first(DayOfWeek.Saturday);
    }

    public Day first(DayOfWeek dayOfWeek) {
        int thisDayOfWeek = this.getDayOfWeek().calendarConstant;
        int referenceDayOfWeek = dayOfWeek.calendarConstant;
        if (referenceDayOfWeek < thisDayOfWeek) {
            return this.advanced(7 - (thisDayOfWeek - referenceDayOfWeek));
        } else if (referenceDayOfWeek == thisDayOfWeek) {
            return this.clone();
        } else {  // referenceDayOfWeek > thisDayOfWeek
            return advanced(referenceDayOfWeek - thisDayOfWeek);
        }
    }

    public Day previous() {
        return advanced(-1);
    }

    public Day advanced(int days) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.DATE, days);
        return new Day(calendar);
    }

    public Day receded(int days) {
        return advanced(-days);
    }

    public Day advancedYears(int years) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.YEAR, years);
        return new Day(calendar);
    }

    public Day recededYears(int years) {
        return advancedYears(-years);
    }

    public Day advancedMonths(int months) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.MONTH, months);
        return new Day(calendar);
    }

    public Day recededMonths(int months) {
        return advancedMonths(-months);
    }

    public Day advancedWeeks(int weeks) {
        return advanced(weeks * 7);
    }

    public Day recededWeeks(int weeks) {
        return advancedWeeks(-weeks);
    }

    public Day advanceToNextWeekday() {
        Day day = new Day(year, monthInYear, dayInMonth);
        while (day.isOnAWeekend()) {
            day = day.advanced(1);
        }
        return day;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
               && obj instanceof Day
               && ((Day) obj).getYear() == year
               && ((Day) obj).getMonthInYear() == monthInYear
               && ((Day) obj).getDayInMonth() == dayInMonth;
    }

    public boolean after(Day anotherDay) {
        if (anotherDay == null) {
            throw new NullPointerException("anotherDay cannot be null");
        } else {
            if (year > anotherDay.getYear()) {
                return true;
            } else if (year < anotherDay.getYear()) {
                return false;
            } else {
                if (monthInYear > anotherDay.getMonthInYear()) {
                    return true;
                } else if (monthInYear < anotherDay.getMonthInYear()) {
                    return false;
                } else {
                    if (dayInMonth > anotherDay.getDayInMonth()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    public boolean before(Day anotherDay) {
        if (anotherDay == null) {
            throw new NullPointerException("anotherDay cannot be null");
        } else {
            if (year < anotherDay.getYear()) {
                return true;
            } else if (year > anotherDay.getYear()) {
                return false;
            } else {
                if (monthInYear < anotherDay.getMonthInYear()) {
                    return true;
                } else if (monthInYear > anotherDay.getMonthInYear()) {
                    return false;
                } else {
                    if (dayInMonth < anotherDay.getDayInMonth()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    public int daysSince(Day anotherDay) {
        return -daysUntil(anotherDay);
    }

    public int daysUntil(Day anotherDay) {
        GregorianCalendar thisCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        thisCalendar.set(year, monthInYear - 1, dayInMonth, 0, 0, 0);
        thisCalendar.set(GregorianCalendar.MILLISECOND, 0);
        GregorianCalendar theOtherCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        theOtherCalendar.set(anotherDay.year, anotherDay.monthInYear - 1, anotherDay.dayInMonth, 0, 0, 0);
        theOtherCalendar.set(GregorianCalendar.MILLISECOND, 0);
        long milliseconds = theOtherCalendar.getTimeInMillis() - thisCalendar.getTimeInMillis();
        double fractionalDays = (double) milliseconds / ((double) (24 * 60 * 60 * 1000));
        long longDays = Math.round(fractionalDays);
        if (longDays > Integer.MAX_VALUE) {
            throw new RuntimeException("Number of days too big to represent as an int");
        } else {
            return (int) longDays;
        }

    }

    public LocalDate toLocalDate() {
        return LocalDate.of(this.year, this.monthInYear, this.dayInMonth);
    }

    public static LocalDate toLocalDate(Day day) {
        if (day == null) {
            return null;
        } else {
            return day.toLocalDate();
        }
    }

    public static Date toDate(Day day) {
        return day == null ? null : day.getDate();
    }

    public String toString() {
        return String.format("%04d-%02d-%02d", year, monthInYear, dayInMonth);
    }

    public String toJavascriptString() {
        return JAVASCRIPT_DAY_FORMAT.format(getDate());
    }

    public static String toJavascriptString(Day day) {
        return toJavascriptString(day, null);
    }

    public static String toJavascriptString(Day day, String valueIfNull) {
        return day == null ? valueIfNull : JAVASCRIPT_DAY_FORMAT.format(day.getDate());
    }

    public String format(String pattern) {
        return new SimpleDateFormat(pattern).format(getDate());
    }

    public static String format(Day day, String pattern) {
        return format(day, pattern, null);
    }

    public static String format(Day day, String pattern, String valueIfNull) {
        return day == null ? valueIfNull : new SimpleDateFormat(pattern).format(day.getDate());
    }

    public String toFilenameSafeString() {
        return String.format("%04d%02d%02d", year, monthInYear, dayInMonth);
    }

    public static Day fromString(String dateString) {
        if (dateString == null) {
            return null;
        } else {

            Matcher m = Pattern.compile("^(\\d\\d\\d\\d)[-\\.](\\d\\d)[-\\.](\\d\\d).*$").matcher(dateString);
            if (m.matches()) {
                int year = Integer.parseInt(m.group(1));
                int month = Integer.parseInt(m.group(2));
                int day = Integer.parseInt(m.group(3));
                return new Day(year, month, day);
            }

            m = Pattern.compile("^([a-z]{3}) (\\d\\d?), (\\d{4})$").matcher(dateString.toLowerCase());
            if (m.matches()) {
                int year = Integer.parseInt(m.group(3));
                int day = Integer.parseInt(m.group(2));
                String monthString = m.group(1);
                int month;
                if ("jan".equals(monthString)) {
                    month = 1;
                } else if ("feb".equals(monthString)) {
                    month = 2;
                } else if ("mar".equals(monthString)) {
                    month = 3;
                } else if ("apr".equals(monthString)) {
                    month = 4;
                } else if ("may".equals(monthString)) {
                    month = 5;
                } else if ("jun".equals(monthString)) {
                    month = 6;
                } else if ("jul".equals(monthString)) {
                    month = 7;
                } else if ("aug".equals(monthString)) {
                    month = 8;
                } else if ("sep".equals(monthString)) {
                    month = 9;
                } else if ("oct".equals(monthString)) {
                    month = 10;
                } else if ("nov".equals(monthString)) {
                    month = 11;
                } else if ("dec".equals(monthString)) {
                    month = 12;
                } else {
                    throw new DateFormatException(dateString);
                }
                return new Day(year, month, day);
            }

            m = Pattern.compile("^(\\d\\d?)-([a-z]{3})-(\\d{4})$").matcher(dateString.toLowerCase());
            if (m.matches()) {
                int year = Integer.parseInt(m.group(3));
                int day = Integer.parseInt(m.group(1));
                String monthString = m.group(2);
                int month;
                if ("jan".equals(monthString)) {
                    month = 1;
                } else if ("feb".equals(monthString)) {
                    month = 2;
                } else if ("mar".equals(monthString)) {
                    month = 3;
                } else if ("apr".equals(monthString)) {
                    month = 4;
                } else if ("may".equals(monthString)) {
                    month = 5;
                } else if ("jun".equals(monthString)) {
                    month = 6;
                } else if ("jul".equals(monthString)) {
                    month = 7;
                } else if ("aug".equals(monthString)) {
                    month = 8;
                } else if ("sep".equals(monthString)) {
                    month = 9;
                } else if ("oct".equals(monthString)) {
                    month = 10;
                } else if ("nov".equals(monthString)) {
                    month = 11;
                } else if ("dec".equals(monthString)) {
                    month = 12;
                } else {
                    throw new DateFormatException(dateString);
                }
                return new Day(year, month, day);
            }

            throw new DateFormatException(dateString);

        }
    }

    public int compareTo(Day anotherDay) {
        if (this.equals(anotherDay)) {
            return 0;
        } else if (this.before(anotherDay)) {
            return -1;
        } else {
            return 1;
        }
    }

    @JsonIgnore
    public Hour getFirstHour() {
        return new Hour(year, monthInYear, dayInMonth, 0);
    }

    @JsonIgnore
    public Minute getFirstMinute() {
        return new Minute(year, monthInYear, dayInMonth, 0, 0);
    }

    @JsonIgnore
    public Second getFirstSecond() {
        return new Second(year, monthInYear, dayInMonth, 0, 0, 0);
    }

    @JsonIgnore
    public Millisecond getFirstMillisecond() {
        return new Millisecond(year, monthInYear, dayInMonth, 0, 0, 0, 0);
    }

}
