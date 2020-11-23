/*

Copyright (c) 2012, Jirvan Pty Ltd
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is primarily here to get around the standard java Date class's
 * entanglement with timezone along with providing an fixed
 * "granularity" of a month.  It is mainly for using with dates that have no
 * need for association with a time zone.  The association of a time zone can
 * actually cause real problems in these situations.  For example if a persons
 * birthday is the first of May, then it is always the 1st of May regardless of
 * what timezone they were born in or where they are now.  At the moment a
 * Gregorian calendar is assumed.
 */
public class Month {

    private int year;
    private int monthInYear;

    public Month() {
        this(new Date());
    }

    public Month(int year, int monthInYear) {
        this.year = year;
        this.monthInYear = monthInYear;
    }

    public Month(GregorianCalendar calendar) {
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
    }

    public Month(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
    }

    public Month(Date date, TimeZone timeZone) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
    }

    public static Month from(GregorianCalendar calendar) {
        return calendar == null ? null : new Month(calendar);
    }

    public static Month from(Date date) {
        return date == null ? null : new Month(date);
    }

    public static Month from(Timestamp timestamp) {
        return timestamp == null ? null : new Month(timestamp);
    }

    public static Month from(Date date, TimeZone timeZone) {
        return date == null ? null : new Month(date, timeZone);
    }

    public static Month current() {
        return new Month();
    }

    public int getYear() {
        return year;
    }

    public Month setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMonthInYear() {
        return monthInYear;
    }

    public Month setMonthInYear(int monthInYear) {
        this.monthInYear = monthInYear;
        return this;
    }

    public Date getDate() {
        return getCalendar().getTime();
    }

    public Calendar getCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, 1, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar;
    }

    public Month next() {
        return advanced(1);
    }

    public Day firstDay() {
        return new Day(year, monthInYear, 1);
    }

    public Day lastDay() {
        return this.next().firstDay().previous();
    }

    public Month previous() {
        return advanced(-1);
    }

    public Month advanced(int months) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, 1, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.MONTH, months);
        return new Month(calendar);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Month && ((Month) obj).getYear() == year && ((Month) obj).getMonthInYear() == monthInYear;
    }

    public boolean after(Month anotherMonth) {
        if (anotherMonth == null) {
            throw new NullPointerException("anotherMonth cannot be null");
        } else {
            if (year > anotherMonth.getYear()) {
                return true;
            } else if (year < anotherMonth.getYear()) {
                return false;
            } else {
                if (monthInYear > anotherMonth.getMonthInYear()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public boolean before(Month anotherMonth) {
        if (anotherMonth == null) {
            throw new NullPointerException("anotherMonth cannot be null");
        } else {
            if (year < anotherMonth.getYear()) {
                return true;
            } else if (year > anotherMonth.getYear()) {
                return false;
            } else {
                if (monthInYear < anotherMonth.getMonthInYear()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public String toString() {
        return String.format("%04d-%02d", year, monthInYear);
    }

    public String toFilenameSafeString() {
        return String.format("%04d%02d", year, monthInYear);
    }

    public String format(String pattern) {
        return new SimpleDateFormat(pattern).format(getDate());
    }

    public static String format(Month month, String pattern) {
        return format(month, pattern, null);
    }

    public static String format(Month month, String pattern, String valueIfNull) {
        return month == null ? valueIfNull : new SimpleDateFormat(pattern).format(month.getDate());
    }

    public static Date toDate(Month month) {
        return month == null ? null : month.getDate();
    }

    public static Month fromString(String monthString) {
        if (monthString == null) {
            return null;
        } else {
            Matcher matcherFull = Pattern.compile("^(\\d\\d\\d\\d)-(\\d\\d)$").matcher(monthString);
            Matcher matcherAbbreviated = Pattern.compile("^(\\d\\d) */ *(\\d\\d)$").matcher(monthString);
            if (matcherFull.matches()) {
                int year = Integer.parseInt(matcherFull.group(1));
                int month = Integer.parseInt(matcherFull.group(2));
                return new Month(year, month);
            } else if (matcherAbbreviated.matches()) {
                int year = Integer.parseInt(matcherAbbreviated.group(2)) + 2000;
                int month = Integer.parseInt(matcherAbbreviated.group(1));
                return new Month(year, month);
            } else {
                throw new MonthFormatException(monthString);
            }
        }
    }

    @JsonIgnore
    public Day getFirstDay() {
        return new Day(year, monthInYear, 1);
    }

    @JsonIgnore
    public Hour getFirstHour() {
        return new Hour(year, monthInYear, 1, 0);
    }

    @JsonIgnore
    public Minute getFirstMinute() {
        return new Minute(year, monthInYear, 1, 0, 0);
    }

    @JsonIgnore
    public Second getFirstSecond() {
        return new Second(year, monthInYear, 1, 0, 0, 0);
    }

    @JsonIgnore
    public Millisecond getFirstMillisecond() {
        return new Millisecond(year, monthInYear, 1, 0, 0, 0, 0);
    }

}
