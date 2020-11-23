/*

Copyright (c) 2018, Jirvan Pty Ltd
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

package com.jirvan.thymeleaf;

import com.jirvan.dates.Day;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ThymeleafUtils {

    private static final NumberFormat formatCurrency = DecimalFormat.getCurrencyInstance(Locale.forLanguageTag("en-AU"));
    private static final NumberFormat format2Digits = new DecimalFormat("#,##0.00");
    private static final NumberFormat simpleFormat = new DecimalFormat("#,##0.##");

    public String formatCurrency(BigDecimal value) {
        return value == null ? null : formatCurrency.format(value);
    }

    public String format2DigitTime(BigDecimal time) {
        return time == null ? null : format2Digits.format(time);
    }

    public String formatSimpleTime(BigDecimal time) {
        return time == null ? null : simpleFormat.format(time);
    }

    public String formatDate(Day date) {
        return formatDate(date, "EEEE, d MMM yyyy");
    }

    public String formatShortDate(Day date) {
        return formatDate(date, "d-MMM-yy");
    }

    public String formatMidlengthDate(Day date) {
        return formatDate(date, "d MMMM yyyy");
    }

    public String formatDate(Day date, String pattern) {
        return date == null ? null : date.format(pattern);
    }

}
