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

package com.jirvan.util;

import com.jirvan.lang.NotFoundRuntimeException;
import com.jirvan.lang.SQLRuntimeException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public enum DatabaseType {

    sqlserver("Microsoft SQL Server"),
    sqlite("SQLite"),
    postgres("PostgreSQL");

    private String databaseProductName;

    private DatabaseType(String databaseProductName) {
        this.databaseProductName = databaseProductName;
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public static DatabaseType get(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            return get(connection);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static DatabaseType get(Connection connection) {
        try {
            return DatabaseType.get(connection.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static DatabaseType get(String name) {
        for (DatabaseType databaseType : DatabaseType.values()) {
            if (databaseType.databaseProductName.equals(name) || databaseType.name().equals(name)) {
                return databaseType;
            }
        }
        try {
            return DatabaseType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedDatabaseTypeException(name, DatabaseType.values());
        }
    }

    public static DatabaseType getIfSupported(Connection connection) {
        try {
            return DatabaseType.getIfSupported(connection.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static DatabaseType getIfSupported(DataSource dataSource, DatabaseType... supportedDatabaseTypes) {
        try (Connection connection = dataSource.getConnection()) {
            return DatabaseType.getIfSupported(connection.getMetaData().getDatabaseProductName(), supportedDatabaseTypes);
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static DatabaseType getIfSupported(String name, DatabaseType... supportedDatabaseTypes) {
        DatabaseType databaseType = get(name);
        if (databaseType.isOneOf(supportedDatabaseTypes)) {
            return databaseType;
        } else {
            throw new UnsupportedDatabaseTypeException(name, supportedDatabaseTypes);
        }
    }

    public boolean isOneOf(DatabaseType... values) {
        for (DatabaseType value : values) {
            if (this == value) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotOneOf(DatabaseType... values) {
        return !isOneOf(values);
    }

    public String getSql(Class anchorClass, String scriptRelativePath) {
        try {
            return Io.getResourceFileString(anchorClass, "sql/" + this.name() + "/" + scriptRelativePath);
        } catch (NotFoundRuntimeException e) {
            return Io.getResourceFileString(anchorClass, "sql/" + scriptRelativePath);
        }
    }

}
