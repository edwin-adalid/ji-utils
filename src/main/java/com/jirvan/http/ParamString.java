/*

Copyright (c) 2019 Jirvan Pty Ltd
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

package com.jirvan.http;

import com.jirvan.dates.Day;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ParamString {

    private String string = "";

    public ParamString add(String paramName, LocalDateTime paramValue) {
        return add(paramName, paramValue == null ? null : paramValue.toString());
    }

    public ParamString add(String paramName, LocalDate paramValue) {
        return add(paramName, paramValue == null ? null : paramValue.toString());
    }

    public ParamString add(String paramName, Day paramValue) {
        return add(paramName, paramValue == null ? null : paramValue.toString());
    }

    public ParamString add(String paramName, BigDecimal paramValue) {
        return add(paramName, paramValue == null ? null : paramValue.toString());
    }

    public ParamString add(String paramName, Integer paramValue) {
        return add(paramName, paramValue == null ? null : paramValue.toString());
    }

    public ParamString add(String paramName, Long paramValue) {
        return add(paramName, paramValue == null ? null : paramValue.toString());
    }

    public ParamString addOptional(String paramName, LocalDateTime paramValue) {
        return paramValue == null ? this : add(paramName, paramValue.toString());
    }

    public ParamString addOptional(String paramName, LocalDate paramValue) {
        return paramValue == null ? this : add(paramName, paramValue.toString());
    }

    public ParamString addOptional(String paramName, Day paramValue) {
        return paramValue == null ? this : add(paramName, paramValue.toString());
    }

    public ParamString addOptional(String paramName, BigDecimal paramValue) {
        return paramValue == null ? this : add(paramName, paramValue.toString());
    }

    public ParamString addOptional(String paramName, Integer paramValue) {
        return paramValue == null ? this : add(paramName, paramValue.toString());
    }

    public ParamString addOptional(String paramName, Long paramValue) {
        return paramValue == null ? this : add(paramName, paramValue.toString());
    }

    public ParamString add(String paramName, String paramValue) {
        if (paramValue == null) {
            throw new RuntimeException(paramName + " cannot be null");
        } else {
            string += string == null || string.trim().length() == 0 ? "?" : "&";
            try {
                string += paramName + "=" + URLEncoder.encode(paramValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    public String getString() {
        return string;
    }

}
