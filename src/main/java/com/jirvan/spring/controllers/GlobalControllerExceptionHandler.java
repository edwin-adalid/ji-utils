/*

Copyright (c) 2014, Jirvan Pty Ltd
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

package com.jirvan.spring.controllers;

import com.jirvan.lang.MessageException;
import com.jirvan.lang.NotFoundRuntimeException;
import com.jirvan.util.Utl;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody Error defaultErrorHandler(Throwable throwable) throws Throwable {

        // If the throwable is annotated with @ResponseStatus rethrow it and let the
        // framework handle it, otherwise return the error object with a 500 status
        if (AnnotationUtils.findAnnotation(throwable.getClass(), ResponseStatus.class) != null) {
            throw throwable;
        } else {
            throwable.printStackTrace();
            if (throwable instanceof MessageException) {
                return new Error("Error", throwable);
            } else {
                return new Error(throwable);
            }
        }

    }

    @ExceptionHandler(NotFoundRuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody Error handleError(NotFoundRuntimeException exception) {
        return new Error("Not found exception", exception);
    }

    public static class Error {

        private String errorName;
        private String errorMessage;
        private String throwableClass;
        private String throwableStacktrace;

        public Error(Throwable throwable) {
            this.errorName = throwable.getClass().getSimpleName();
            this.errorMessage = throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getSimpleName();
            this.throwableClass = throwable.getClass().getName();
            this.throwableStacktrace = Utl.getStackTrace(throwable);
        }

        public Error(String errorName, Throwable throwable) {
            this.errorName = errorName;
            this.errorMessage = throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getSimpleName();
            this.throwableClass = throwable.getClass().getName();
            this.throwableStacktrace = Utl.getStackTrace(throwable);
        }

        public String getErrorName() {
            return errorName;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getThrowableClass() {
            return throwableClass;
        }

        public String getThrowableStacktrace() {
            return throwableStacktrace;
        }

    }

}
