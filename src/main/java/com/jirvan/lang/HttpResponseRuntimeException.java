/*

Copyright (c) 2016, Jirvan Pty Ltd
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

package com.jirvan.lang;

import com.jirvan.http.JiHttpUtils;
import com.jirvan.util.Utl;
import org.apache.http.StatusLine;

import java.util.List;

public class HttpResponseRuntimeException extends RuntimeException {

    private int statusCode;
    private String simpleErrorMessage;
    private String reasonPhrase;
    private String errorName;
    private String errorInfo;
    private List<JiHttpUtils.HttpErrorContentObject.FieldError> fieldErrors;
    private Long timestamp;
    private Integer status;
    private String error;
    private String exception;
    private String path;

    public HttpResponseRuntimeException(int statusCode, String simpleErrorMessage, String reasonPhrase) {
        super("HTTP " + statusCode + ": " + simpleErrorMessage);
        this.statusCode = statusCode;
        this.simpleErrorMessage = "HTTP " + statusCode + ": " + simpleErrorMessage;
        this.reasonPhrase = reasonPhrase;
    }

    public HttpResponseRuntimeException(StatusLine statusLine, JiHttpUtils.HttpErrorContentObject error) {
        super("HTTP " + statusLine.getStatusCode() + ": " + Utl.coalesce(error.errorMessage, error.description, error.detail, error.title, error.message, error.errorName, statusLine.getReasonPhrase()));
        this.statusCode = statusLine.getStatusCode();
        this.simpleErrorMessage = "HTTP " + statusCode + ": " + Utl.coalesce(error.errorMessage, error.description, error.detail, error.title, error.message, error.errorName, statusLine.getReasonPhrase());
        this.reasonPhrase = statusLine.getReasonPhrase();
        this.errorName = error.errorName;
        this.errorInfo = error.errorInfo;
        this.timestamp = error.timestamp;
        this.status = error.status;
        this.error = error.error;
        this.exception = error.exception;
        this.path = error.path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getSimpleErrorMessage() {
        return simpleErrorMessage;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getErrorName() {
        return errorName;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public List<JiHttpUtils.HttpErrorContentObject.FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getException() {
        return exception;
    }

    public String getPath() {
        return path;
    }

}

