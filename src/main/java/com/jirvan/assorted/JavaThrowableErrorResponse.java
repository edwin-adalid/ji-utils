/*

Copyright (c) 2013, Jirvan Pty Ltd
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

package com.jirvan.assorted;

import com.jirvan.util.*;

public class JavaThrowableErrorResponse extends ErrorResponse {

    public JavaThrowableErrorResponse(Throwable throwable) {
        super(new JavaThrowableError(throwable));
    }

    public JavaThrowableErrorResponse(String errorName, String errorMessage, Throwable throwable) {
        super(new JavaThrowableError(errorName, errorMessage, throwable));
    }

    public JavaThrowableErrorResponse(String errorMessage, Throwable throwable) {
        super(new JavaThrowableError(errorMessage, throwable));
    }

    public boolean getSuccess() {
        return false;
    }

    @Override public JavaThrowableError getError() {
        return (JavaThrowableError) error;
    }

    public static class JavaThrowableError extends Error {

        private String throwableClass;
        private String throwableStacktrace;

        public JavaThrowableError(Throwable throwable) {
            super(throwable.getClass().getSimpleName(), throwable.getMessage());
            this.throwableClass = throwable.getClass().getName();
            this.throwableStacktrace = Utl.getStackTrace(throwable);
        }

        public JavaThrowableError(String errorName, String errorMessage, Throwable throwable) {
            super(errorName, errorMessage);
            this.throwableClass = throwable.getClass().getName();
            this.throwableStacktrace = Utl.getStackTrace(throwable);
        }

        public JavaThrowableError(String errorMessage, Throwable throwable) {
            super(throwable.getClass().getSimpleName(), errorMessage);
            this.throwableClass = throwable.getClass().getName();
            this.throwableStacktrace = Utl.getStackTrace(throwable);
        }

        public String getThrowableClass() {
            return throwableClass;
        }

        public void setThrowableClass(String throwableClass) {
            this.throwableClass = throwableClass;
        }

        public String getThrowableStacktrace() {
            return throwableStacktrace;
        }

        public void setThrowableStacktrace(String throwableStacktrace) {
            this.throwableStacktrace = throwableStacktrace;
        }

    }
}
