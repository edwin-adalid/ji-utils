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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is primarily here to get around the standard java Date class's
 * entanglement with timezone along with providing a fixed
 * "granularity" of a second.  It is mainly for using with dates that have no
 * need for association with a time zone.  The association of a time zone can
 * actually cause real problems in these situations.  For example if a persons
 * birthday is the first of May, then it is always the 1st of May regardless of
 * what timezone they were born in or where they are now.  At the moment a
 * Gregorian calendar is assumed.
 */
public class Second {

    private int year;
    private int monthInYear;
    private int dayInMonth;
    private int hourInDay;
    private int minuteInHour;
    private int secondInMinute;

    public Second() {
        this(new Date());
    }

    public Second(int year, int monthInYear, int dayInMonth, int hourInDay, int minuteInHour, int secondInMinute) {
        this.year = year;
        this.monthInYear = monthInYear;
        this.dayInMonth = dayInMonth;
        this.hourInDay = hourInDay;
        this.minuteInHour = minuteInHour;
        this.secondInMinute = secondInMinute;
    }

    private Second(LocalDateTime localDateTime) {
        this.year = localDateTime.getYear();
        this.monthInYear = localDateTime.getMonthValue();
        this.dayInMonth = localDateTime.getDayOfMonth();
        this.hourInDay = localDateTime.getHour();
        this.minuteInHour = localDateTime.getMinute();
        this.secondInMinute = localDateTime.getSecond();
    }

    private Second(GregorianCalendar calendar) {
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        this.minuteInHour = calendar.get(GregorianCalendar.MINUTE);
        this.secondInMinute = calendar.get(GregorianCalendar.SECOND);
    }

    private Second(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        this.minuteInHour = calendar.get(GregorianCalendar.MINUTE);
        this.secondInMinute = calendar.get(GregorianCalendar.SECOND);
    }

    public Second(Date date, TimeZone timeZone) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.setTime(date);
        this.year = calendar.get(GregorianCalendar.YEAR);
        this.monthInYear = calendar.get(GregorianCalendar.MONTH) + 1;
        this.dayInMonth = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        this.hourInDay = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        this.minuteInHour = calendar.get(GregorianCalendar.MINUTE);
        this.secondInMinute = calendar.get(GregorianCalendar.SECOND);
    }

    public static Second now() {
        return new Second();
    }

    public int getYear() {
        return year;
    }

    public Second setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMonthInYear() {
        return monthInYear;
    }

    public Second setMonthInYear(int monthInYear) {
        this.monthInYear = monthInYear;
        return this;
    }

    public int getDayInMonth() {
        return dayInMonth;
    }

    public Second setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
        return this;
    }

    public int getHourInDay() {
        return hourInDay;
    }

    public Second setHourInDay(int hourInDay) {
        this.hourInDay = hourInDay;
        return this;
    }

    public int getMinuteInHour() {
        return minuteInHour;
    }

    public Second setMinuteInHour(int minuteInHour) {
        this.minuteInHour = minuteInHour;
        return this;
    }

    public int getSecondInMinute() {
        return secondInMinute;
    }

    public Second setSecondInMinute(int secondInMinute) {
        this.secondInMinute = secondInMinute;
        return this;
    }

    public Date getDate() {
        return getCalendar().getTime();
    }

    public Calendar getCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, hourInDay, minuteInHour, secondInMinute);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar;
    }

    public Month getMonth() {
        return new Month(year, monthInYear);
    }

    public Day getDay() {
        return new Day(year, monthInYear, dayInMonth);
    }

    public Hour getHour() {
        return new Hour(year, monthInYear, dayInMonth, hourInDay);
    }

    public Minute getMinute() {
        return new Minute(year, monthInYear, dayInMonth, hourInDay, minuteInHour);
    }

    public Second next() {
        return advanced(1);
    }

    public Second previous() {
        return advanced(-1);
    }

    public Second advanced(int seconds) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, monthInYear - 1, dayInMonth, hourInDay, minuteInHour, secondInMinute);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        calendar.add(GregorianCalendar.SECOND, seconds);
        return new Second(calendar);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
               && obj instanceof Second
               && ((Second) obj).getYear() == year
               && ((Second) obj).getMonthInYear() == monthInYear
               && ((Second) obj).getDayInMonth() == dayInMonth
               && ((Second) obj).getHourInDay() == hourInDay
               && ((Second) obj).getMinuteInHour() == minuteInHour
               && ((Second) obj).getSecondInMinute() == secondInMinute;
    }

    public String toString() {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, monthInYear, dayInMonth, hourInDay, minuteInHour, secondInMinute);
    }

    public String toFilenameSafeString() {
        return String.format("%04d%02d%02d-%02d%02d-%02d", year, monthInYear, dayInMonth, hourInDay, minuteInHour, secondInMinute);
    }

    public String toISO8601String() {
        return String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, monthInYear, dayInMonth, hourInDay, minuteInHour, secondInMinute);
    }

    public String format(String pattern) {
        return new SimpleDateFormat(pattern).format(getDate());
    }

    public static String format(Second second, String pattern) {
        return format(second, pattern, null);
    }

    public static String format(Second second, String pattern, String valueIfNull) {
        return second == null ? valueIfNull : new SimpleDateFormat(pattern).format(second.getDate());
    }

    public static Second fromString(String string) {
        if (string == null) {
            return null;
        } else {
            Matcher m = Pattern.compile("^(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)[ T](\\d\\d):(\\d\\d):(\\d\\d)$").matcher(string);
            if (!m.matches()) {
                throw new SecondTimestampFormatException(string);
            }
            int year = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            int hour = Integer.parseInt(m.group(4));
            int minute = Integer.parseInt(m.group(5));
            int second = Integer.parseInt(m.group(6));
            return new Second(year, month, day, hour, minute, second);
        }
    }

    public static Second from(Date date) {
        if (date == null) {
            return null;
        } else {
            return new Second(date);
        }
    }

    public static Date toDate(Second second) {
        if (second == null) {
            return null;
        } else {
            return second.getDate();
        }
    }

    public static Second from(GregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        } else {
            return new Second(calendar);
        }
    }

    public static Second from(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            return new Second(localDateTime);
        }
    }

    public static Calendar toCalendar(Second second) {
        if (second == null) {
            return null;
        } else {
            return second.getCalendar();
        }
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(this.year, this.monthInYear, this.dayInMonth, this.hourInDay, this.minuteInHour, this.secondInMinute);
    }

    public static LocalDateTime toLocalDateTime(Second second) {
        if (second == null) {
            return null;
        } else {
            return second.toLocalDateTime();
        }
    }

    public static String formatDuration(Second from, Second to) {
        if (from == null || to == null) {
            return "";
        } else {
            return formatDuration((to.getDate().getTime() - from.getDate().getTime()) / 1000);
        }
    }

    public static String formatDuration(Long seconds) {
        if (seconds == null) {
            return "";
        } else {
            long totalSeconds = Math.abs(seconds);
            long totalMinutes = totalSeconds / 60;
            long hoursComponent = totalSeconds / (60 * 60);
            long minutesComponent = totalMinutes - (hoursComponent * 60);
            long secondsComponent = totalSeconds - (totalMinutes * 60);
            return String.format("%s%02d:%02d:%02d", seconds < 0 ? "-" : "", hoursComponent, minutesComponent, secondsComponent);
        }
    }

    @JsonIgnore
    public Millisecond getFirstMillisecond() {
        return new Millisecond(year, monthInYear, dayInMonth, hourInDay, minuteInHour, secondInMinute, 0);
    }

}
