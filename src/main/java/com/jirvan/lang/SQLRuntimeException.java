/*

Copyright (c) 2010,2011,2012,2013 Jirvan Pty Ltd
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

import java.sql.*;

public class SQLRuntimeException extends RuntimeException {

    public static boolean includeSQLInMessage = false;

    private String sql;
    private Object[] parameters;

    public SQLRuntimeException(String message) {
        this(null, message, null, new Object[0]);
    }

    public SQLRuntimeException(String message, String sql) {
        this(null, message, sql, new Object[0]);
    }

    public SQLRuntimeException(String message, String sql, Object[] parameters) {
        this(null, message, sql, parameters);
    }

    public SQLRuntimeException() {
        this(null, null, null, new Object[0]);
    }

    public SQLRuntimeException(SQLException cause) {
        this(cause, null, null, new Object[0]);
    }

    public SQLRuntimeException(SQLException cause,
                               String sql) {
        this(cause, null, sql, new Object[0]);
    }

    public SQLRuntimeException(SQLException cause,
                               String sql,
                               Object[] parameters) {
        this(cause, null, sql, parameters);
    }

    public SQLRuntimeException(SQLException cause,
                               String message,
                               String sql,
                               Object[] parameters) {
        super(message != null
              ? includeSQLInMessage
                ? message + "\n" + format(sql, parameters)
                : message
              : cause == null
                ? includeSQLInMessage
                  ? "SQLRuntimeException\n" + format(sql, parameters)
                  : "SQLRuntimeException"
                : includeSQLInMessage
                  ? cause.getMessage() + "\n" + format(sql, parameters)
                  : cause.getMessage(),
              cause);
        this.sql = sql;
        this.parameters = parameters;
    }

    private static String format(String sql, Object[] parameters) {
        if (sql == null) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        buf.append("SQL: ");
        buf.append(sql.replaceAll("\\n", "\n     "));
        buf.append("\n");
        if (parameters != null) {
            buf.append("Parameter values: ");
            for (int i = 0; i < parameters.length; i++) {
                if (i != 0) buf.append(",\n                  ");
                buf.append(parameters[i] == null
                           ? "<null>"
                           : parameters[i].toString());

            }
            buf.append("\n");
        }
        return buf.toString();
    }


    public String getSql() {
        return sql;
    }

    public Object[] getParameters() {
        return parameters;
    }

}