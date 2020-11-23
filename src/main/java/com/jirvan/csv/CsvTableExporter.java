/*

Copyright (c) 2006,2007,2008,2009,2010,2011,2012,2013 Jirvan Pty Ltd
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

package com.jirvan.csv;

import com.jirvan.lang.SQLRuntimeException;
import com.jirvan.util.Jdbc;
import org.apache.commons.lang.SystemUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CsvTableExporter {

    public static String emptyStringIndicatorString;  // This is a quick and dirty

    public static void main(String[] args) {
        CsvTableExporter.exportToFile(Jdbc.getDataSource("sqlserver:cm/zippee@denver2/LifeCare"), "merchant_products", new File("L:\\Desktop\\test.csv"));
    }

    private static DateFormat timestampFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public static long exportToFile(DataSource dataSource, String tableName, File outFile) {
        return exportToFile(dataSource, tableName, null, null, outFile);
    }

    public static long exportToFile(DataSource dataSource, String tableName, String sql, String whereClauseCondition, File outFile) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                return exportToFile(connection, tableName, sql, whereClauseCondition, outFile);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static long exportToFile(Connection connection, String tableName, File outFile) {
        return exportToFile(connection, tableName, null, null, outFile);
    }

    public static long exportToFile(Connection connection, String tableName, String sql, String whereClauseCondition, File outFile) {
        try {
            OutputStream fileOutputStream = new FileOutputStream(outFile);
            try {
                return exportToOutputStream(connection, tableName, sql, whereClauseCondition, fileOutputStream);
            } finally {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long exportToOutputStream(DataSource dataSource, String tableName, String sql, String whereClauseCondition, OutputStream outputStream) throws IOException {
        try {
            Connection connection = dataSource.getConnection();
            try {
                return exportToOutputStream(connection, tableName, sql, whereClauseCondition, outputStream);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static long exportToOutputStream(Connection connection, String tableName, String sql, String whereClauseCondition, OutputStream outputStream) throws IOException {
        try {
            if (sql == null || sql.trim().length() == 0) {
                sql = whereClauseCondition == null || whereClauseCondition.trim().length() == 0
                      ? "select * from " + tableName
                      : "select * from " + tableName + " where " + whereClauseCondition;
            }
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                ResultSet rset = stmt.executeQuery();
                try {

                    // Write the header
                    int columnCount = rset.getMetaData().getColumnCount();
                    for (int i = 0; i < columnCount; i++) {
                        if (i != 0) outputStream.write(',');
                        outputStream.write(rset.getMetaData().getColumnName(i + 1).getBytes());
                    }
                    if (SystemUtils.IS_OS_WINDOWS) outputStream.write('\r');
                    outputStream.write('\n');

                    // Write the data lines
                    long rowsExported = 0;
                    while (rset.next()) {
                        for (int i = 0; i < columnCount; i++) {
                            if (i != 0) outputStream.write(',');
                            outputStream.write(formatValue(rset.getObject(i + 1)).getBytes());
                        }
                        if (SystemUtils.IS_OS_WINDOWS) outputStream.write('\r');
                        outputStream.write('\n');
                        rowsExported++;
                    }

                    return rowsExported;

                } finally {
                    stmt.close();
                }
            } finally {
                stmt.close();
            }

        } catch (SQLException e) {
            throw new SQLRuntimeException(e, sql);
        }
    }

    public static String formatValue(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof Timestamp) {
            return timestampFormat.format((Timestamp) value);
        } else if (value instanceof String) {
            if (emptyStringIndicatorString != null && "".equals(value)) {
                return emptyStringIndicatorString;
            } else if ("".equals(value)) {
                return "";
            } else if (((String) value).trim().length() == 0) {
                return "\"" + value + "\"";
            } else if (((String) value).indexOf('"') == -1 && ((String) value).indexOf(',') == -1 && ((String) value).indexOf('\n') == -1 && ((String) value).indexOf('\r') == -1) {
                return ((String) value).replaceAll("\"", "\"\"");
            } else {
                return "\"" + ((String) value).replaceAll("\"", "\"\"") + "\"";
            }
        } else if (value instanceof BigDecimal) {
            return value.toString().replaceFirst("\\.0+$", "").replaceFirst("\\.(\\d+)0+$", ".$1");
        } else {
            return value.toString();
        }
    }

}
