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

import au.com.bytecode.opencsv.CSVReader;
import com.jirvan.lang.ResourceNotFoundRuntimeException;
import com.jirvan.lang.SQLRuntimeException;
import com.jirvan.util.Strings;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Vector;

import static com.jirvan.util.Assertions.*;

public class CsvTableImporter {

    public static String emptyStringIndicatorString;  // This is a quick and dirty

    public static long importFromFile(DataSource dataSource, String tableName, File dataFile) {
        return importFromFile(dataSource, tableName, null, null, null, 0, dataFile, false);
    }

    public static long importFromFile(Connection connection, String tableName, File dataFile) {
        return importFromFile(connection, tableName, null, null, null, 0, dataFile, false);
    }

    public static long importFromFile(DataSource dataSource,
                                      String tableName,
                                      DateFormat timestampFormatOverride,
                                      File dataFile) {
        return importFromFile(dataSource, tableName, null, null, timestampFormatOverride, 0, dataFile, false);
    }

    public static long importFromFile(Connection connection,
                                      String tableName,
                                      DateFormat timestampFormatOverride,
                                      File dataFile) {
        return importFromFile(connection, tableName, null, null, timestampFormatOverride, 0, dataFile, false);
    }

    public static long importFromFile(DataSource dataSource, String tableName, Map columnMappings, String[] ignoreColumns, DateFormat timestampFormatOverride, int commitInterval, File dataFile) {
        return importFromFile(dataSource, tableName, columnMappings, ignoreColumns, timestampFormatOverride, commitInterval, dataFile, false);
    }

    public static long importFromFile(Connection connection, String tableName, Map columnMappings, String[] ignoreColumns, DateFormat timestampFormatOverride, int commitInterval, File dataFile) {
        return importFromFile(connection, tableName, columnMappings, ignoreColumns, timestampFormatOverride, commitInterval, dataFile, false);
    }

    public static long importFromResourceFile(DataSource dataSource, String tableName, Class anchorClass, String fileRelativePath) {
        return importFromResourceFile(dataSource, tableName, null, null, null, 0, anchorClass, fileRelativePath, false);
    }

    public static long importFromResourceFile(Connection connection, String tableName, Class anchorClass, String fileRelativePath) {
        return importFromResourceFile(connection, tableName, null, null, null, 0, anchorClass, fileRelativePath, false);
    }

    public static long importFromResourceFile(Connection connection, String tableName, Class anchorClass, String fileRelativePath, Map<String, String> columnValueOverrides) {
        return importFromResourceFile(connection, tableName, null, columnValueOverrides, null, null, 0, anchorClass, fileRelativePath, false);
    }

    public static long importFromResourceFile(DataSource dataSource,
                                              String tableName,
                                              DateFormat timestampFormatOverride,
                                              Class anchorClass,
                                              String fileRelativePath) {
        return importFromResourceFile(dataSource, tableName, null, null, timestampFormatOverride, 0, anchorClass, fileRelativePath, false);
    }

    public static long importFromResourceFile(Connection connection,
                                              String tableName,
                                              DateFormat timestampFormatOverride,
                                              Class anchorClass,
                                              String fileRelativePath) {
        return importFromResourceFile(connection, tableName, null, null, timestampFormatOverride, 0, anchorClass, fileRelativePath, false);
    }

    public static long importFromResourceFile(DataSource dataSource, String tableName, Map columnMappings, String[] ignoreColumns, DateFormat timestampFormatOverride, int commitInterval, Class anchorClass, String fileRelativePath) {
        return importFromResourceFile(dataSource, tableName, columnMappings, ignoreColumns, timestampFormatOverride, commitInterval, anchorClass, fileRelativePath, false);
    }

    public static long importFromResourceFile(Connection connection, String tableName, Map columnMappings, String[] ignoreColumns, DateFormat timestampFormatOverride, int commitInterval, Class anchorClass, String fileRelativePath) {
        return importFromResourceFile(connection, tableName, columnMappings, ignoreColumns, timestampFormatOverride, commitInterval, anchorClass, fileRelativePath, false);
    }

    public static long importFromFile(DataSource dataSource,
                                      String tableName,
                                      Map columnMappings,
                                      String[] ignoreColumns,
                                      DateFormat timestampFormatOverride,
                                      int commitInterval,
                                      File dataFile,
                                      boolean resetAutonumberedPrimaryKey) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                return importFromFile(connection, tableName, columnMappings, ignoreColumns, timestampFormatOverride, commitInterval, dataFile, resetAutonumberedPrimaryKey);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }

    }

    public static long importFromFile(Connection connection,
                                      String tableName,
                                      Map columnMappings,
                                      String[] ignoreColumns,
                                      DateFormat timestampFormatOverride,
                                      int commitInterval,
                                      File dataFile,
                                      boolean resetAutonumberedPrimaryKey) {
        try {
            return importFromReader(connection,
                                    tableName,
                                    columnMappings,
                                    ignoreColumns,
                                    timestampFormatOverride,
                                    commitInterval,
                                    new FileReader(dataFile),
                                    resetAutonumberedPrimaryKey);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static long importFromResourceFile(Connection connection,
                                              String tableName,
                                              Map columnMappings,
                                              String[] ignoreColumns,
                                              DateFormat timestampFormatOverride,
                                              int commitInterval,
                                              Class anchorClass,
                                              String fileRelativePath,
                                              boolean resetAutonumberedPrimaryKey) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(fileRelativePath);
            if (inputStream == null) {
                throw new ResourceNotFoundRuntimeException(anchorClass, fileRelativePath);
            }
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                try {
                    return importFromReader(connection,
                                            tableName,
                                            columnMappings,
                                            ignoreColumns,
                                            timestampFormatOverride,
                                            commitInterval,
                                            inputStreamReader,
                                            resetAutonumberedPrimaryKey);
                } finally {
                    inputStreamReader.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long importFromResourceFile(Connection connection,
                                              String tableName,
                                              Map columnMappings,
                                              Map<String, String> columnValueOverrides,
                                              String[] ignoreColumns,
                                              DateFormat timestampFormatOverride,
                                              int commitInterval,
                                              Class anchorClass,
                                              String fileRelativePath,
                                              boolean resetAutonumberedPrimaryKey) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(fileRelativePath);
            if (inputStream == null) {
                throw new ResourceNotFoundRuntimeException(anchorClass, fileRelativePath);
            }
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                try {
                    return importFromReader(connection,
                                            tableName,
                                            columnMappings,
                                            columnValueOverrides,
                                            ignoreColumns,
                                            timestampFormatOverride,
                                            commitInterval,
                                            inputStreamReader,
                                            resetAutonumberedPrimaryKey);
                } finally {
                    inputStreamReader.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long importFromResourceFile(DataSource dataSource,
                                              String tableName,
                                              Map columnMappings,
                                              String[] ignoreColumns,
                                              DateFormat timestampFormatOverride,
                                              int commitInterval,
                                              Class anchorClass,
                                              String fileRelativePath,
                                              boolean resetAutonumberedPrimaryKey) {
        try {
            InputStream inputStream = anchorClass.getResourceAsStream(fileRelativePath);
            assertNotNull(inputStream, String.format("Couldn't access resource \"%s\" anchored by \"%s\"", fileRelativePath, anchorClass.getName()));
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                try {
                    return importFromReader(dataSource,
                                            tableName,
                                            columnMappings,
                                            ignoreColumns,
                                            timestampFormatOverride,
                                            commitInterval,
                                            inputStreamReader,
                                            resetAutonumberedPrimaryKey);
                } finally {
                    inputStreamReader.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long importFromReader(DataSource dataSource,
                                        String tableName,
                                        Map columnMappings,
                                        String[] ignoreColumns,
                                        DateFormat timestampFormatOverride,
                                        int commitInterval,
                                        Reader reader,
                                        boolean resetAutonumberedPrimaryKey) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                return importFromReader(connection,
                                        tableName,
                                        columnMappings,
                                        ignoreColumns,
                                        timestampFormatOverride,
                                        commitInterval,
                                        reader,
                                        resetAutonumberedPrimaryKey);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static long importFromReader(Connection connection,
                                        String tableName,
                                        Map columnMappings,
                                        String[] ignoreColumns,
                                        DateFormat timestampFormatOverride,
                                        int commitInterval,
                                        Reader reader,
                                        boolean resetAutonumberedPrimaryKey) {
        return importFromReader(connection,
                                tableName,
                                columnMappings,
                                null,
                                ignoreColumns,
                                timestampFormatOverride,
                                commitInterval,
                                reader,
                                resetAutonumberedPrimaryKey);
    }

    public static long importFromReader(Connection connection,
                                        String tableName,
                                        Map columnMappings,
                                        Map<String, String> columnValueOverrides,
                                        String[] ignoreColumns,
                                        DateFormat timestampFormatOverride,
                                        int commitInterval,
                                        Reader reader,
                                        boolean resetAutonumberedPrimaryKey) {
        assertNotNull(connection, "connection is null");
        assertNotNull(tableName, "tableName is null");
        try {

            boolean databaseIsOracle;
            try {
                databaseIsOracle = connection.getMetaData().getDatabaseProductName().toLowerCase().indexOf("oracle") != -1;
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }

            CSVReader csvReader = new CSVReader(reader);
            int lineNumber = 1;
            try {

                // Get the column names
                String[] columnNames = csvReader.readNext();
                if (columnNames == null || columnNames.length == 0) {
                    throw new RuntimeException("First row is empty (expected column names");
                }

                // Apply column mappings (if any)
                if (columnMappings != null) {
                    for (int i = 0; i < columnNames.length; i++) {
                        String mappedColumn = (String) columnMappings.get(columnNames[i]);
                        if (mappedColumn != null) columnNames[i] = mappedColumn;
                    }
                }

                // Build the sql and get the JDBC data types
                StringBuffer sqlBuffer = new StringBuffer();
                StringBuffer sqlParameterBuffer = new StringBuffer();
                int[] columnDataTypes = new int[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {

                    // Add to the sql buffer and the parameter string buffer
                    if (ignoreColumns == null || !Strings.isIn(columnNames[i], ignoreColumns)) {
                        if (sqlBuffer.length() == 0) {
                            sqlBuffer.append("insert into " + tableName + " (\n   ");
                            sqlParameterBuffer.append('?');
                        } else {
                            sqlBuffer.append(",\n   ");
                            sqlParameterBuffer.append(",?");
                        }
                        sqlBuffer.append(columnNames[i]);
                    }

                    // Set the JDBC data type
                    try {
                        String schemaPattern = databaseIsOracle
                                               ? connection.getMetaData().getUserName()
                                               : null;
                        ResultSet rset = connection.getMetaData().getColumns(null,
                                                                             schemaPattern,
                                                                             databaseIsOracle ? tableName.toUpperCase() : tableName,
                                                                             databaseIsOracle ? columnNames[i].toUpperCase() : columnNames[i]);
                        try {
                            if (rset.next()) {
                                columnDataTypes[i] = rset.getInt(5);
                            } else {
                                throw new RuntimeException("Table \"" + tableName + "\" or column \"" + tableName + "." + columnNames[i] + "\" does not exist");
                            }
                        } finally {
                            rset.close();
                        }
                    } catch (SQLException e) {
                        throw new SQLRuntimeException(e);
                    }

                }

                // Join the buffered "main" sql and parameter sql
                String sql = sqlBuffer.toString() + "\n" + ") values (" + sqlParameterBuffer.toString() + ")";

                // Prepare the insert statement and execute it for each row in the csv file
                DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    try {
                        String[] nextLine;
                        int rowsWithoutCommit = 0;
                        while ((nextLine = csvReader.readNext()) != null) {
                            lineNumber++;

                            if (nextLine.length == 1 && (nextLine[0] == null || nextLine[0].trim().length() == 0)) {
                                continue;
                            }

                            try {

                                // Check the number of fields is correct
                                if (nextLine.length != columnNames.length) {
                                    throw new RuntimeException("Exception processing line " + lineNumber + ": This line has "
                                                               + nextLine.length + " fields, but " + columnNames.length
                                                               + " (the number of headings in the first line) were expected.");
                                }

                                // If appropriate apply column value overrides
                                if (columnValueOverrides != null && columnValueOverrides.size() > 0) {
                                    for (int i = 0; i < columnNames.length; i++) {
                                        String overrideValue;
                                        if ((overrideValue = columnValueOverrides.get(columnNames[i])) != null) {
                                            nextLine[i] = overrideValue;
                                        }
                                    }
                                }

                                // Set the column value parameters
                                Vector parameters = new Vector();
                                int parameterNumber = 0;
                                for (int i = 0; i < nextLine.length; i++) {
                                    if (ignoreColumns == null || !Strings.isIn(columnNames[i], ignoreColumns)) {
                                        parameterNumber++;
                                        try {
                                            if (nextLine[i] == null || nextLine[i].length() == 0) {
                                                parameters.add(null);
                                                stmt.setNull(parameterNumber, columnDataTypes[i]);
                                            } else {
                                                if (columnDataTypes[i] == Types.VARCHAR) {
//                                                if (columnDataTypes[i] == Types.VARCHAR
//                                                        ||columnDataTypes[i] == Types.CLOB) {
                                                    if (emptyStringIndicatorString != null && emptyStringIndicatorString.equals(nextLine[i])) {
                                                        parameters.add("");
                                                        stmt.setString(parameterNumber, "");
                                                    } else {
                                                        parameters.add(nextLine[i]);
                                                        stmt.setString(parameterNumber, nextLine[i]);
                                                    }
                                                } else if (columnDataTypes[i] == Types.CHAR) {
                                                    parameters.add(nextLine[i]);
                                                    stmt.setString(parameterNumber, nextLine[i]);
                                                } else if (columnDataTypes[i] == Types.DATE
                                                           || columnDataTypes[i] == Types.TIMESTAMP) {
                                                    if (timestampFormatOverride != null) {
                                                        parameters.add(nextLine[i]);
                                                        stmt.setTimestamp(parameterNumber, new Timestamp(timestampFormatOverride.parse(nextLine[i]).getTime()));
                                                    } else {
                                                        String timestampString = nextLine[i].trim().replaceFirst("(\\d{4})\\.(\\d{2})\\.(\\d{2})", "$1-$2-$3");
                                                        if (timestampString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                                                            timestampString += " 00:00:00";
                                                        }
                                                        parameters.add(timestampString);
                                                        stmt.setTimestamp(parameterNumber, new Timestamp(timestampFormat.parse(timestampString).getTime()));
                                                    }
                                                } else if (columnDataTypes[i] == Types.BIT
                                                           || columnDataTypes[i] == Types.BOOLEAN) {
                                                    parameters.add(nextLine[i]);
                                                    if ("true".equalsIgnoreCase(nextLine[i])
                                                        || "Y".equalsIgnoreCase(nextLine[i])
                                                        || "1".equalsIgnoreCase(nextLine[i])) {
                                                        stmt.setBoolean(parameterNumber, true);
                                                    } else if ("false".equalsIgnoreCase(nextLine[i])
                                                               || "N".equalsIgnoreCase(nextLine[i])
                                                               || "0".equalsIgnoreCase(nextLine[i])) {
                                                        stmt.setBoolean(parameterNumber, false);
                                                    } else {
                                                        throw new RuntimeException("\"%s\" is an invalid value for Column \"" + columnNames[i] + "\" (must be one of true, false, Y, N, 1, 0)");
                                                    }
                                                } else if (columnDataTypes[i] == Types.INTEGER) {
                                                    parameters.add(nextLine[i].trim());
                                                    String stringValue = nextLine[i].trim().replaceAll(",", "").replaceAll("\\$", "");
                                                    if (stringValue.charAt(0) == '(' && stringValue.charAt(stringValue.length() - 1) == ')') {
                                                        stringValue = "-" + stringValue.substring(1, stringValue.length() - 1);
                                                    }
                                                    stmt.setInt(parameterNumber, new Integer(stringValue));
                                                } else if (columnDataTypes[i] == Types.TINYINT
                                                           || columnDataTypes[i] == Types.SMALLINT
                                                           || columnDataTypes[i] == Types.BIGINT
                                                           || columnDataTypes[i] == Types.FLOAT
                                                           || columnDataTypes[i] == Types.REAL
                                                           || columnDataTypes[i] == Types.NUMERIC
                                                           || columnDataTypes[i] == Types.DECIMAL
                                                           || columnDataTypes[i] == Types.DOUBLE) {
                                                    parameters.add(nextLine[i].trim());
                                                    String stringValue = nextLine[i].trim().replaceAll(",", "").replaceAll("\\$", "");
                                                    if (stringValue.charAt(0) == '(' && stringValue.charAt(stringValue.length() - 1) == ')') {
                                                        stringValue = "-" + stringValue.substring(1, stringValue.length() - 1);
                                                    }
                                                    stmt.setBigDecimal(parameterNumber, new BigDecimal(stringValue));
                                                } else {
                                                    throw new RuntimeException("Column \"" + columnNames[i] + "\" is of a JDBC data type that CsvTableImporter can not currently handle (type = " + columnDataTypes[i] + ")");
                                                }
                                            }
                                        } catch (Throwable t) {
                                            throw new RuntimeException("Exception processing \"" + columnNames[i] + "\" in line " + lineNumber + ": " + (t.getMessage() == null ? t.getClass().getName() : t.getMessage()) + "\n(value was \"" + nextLine[i] + "\")", t);
                                        }
                                    }
                                }

                                // Execute the statement
                                try {
                                    stmt.executeUpdate();
                                    rowsWithoutCommit++;
                                    if (commitInterval != 0 && rowsWithoutCommit >= commitInterval) {
                                        connection.commit();
                                        rowsWithoutCommit = 0;
                                    }
                                } catch (SQLException e) {
                                    throw new SQLRuntimeException(e, sql, parameters.toArray());
                                }

                            } catch (Throwable t) {
                                throw CsvLineRuntimeException.wrapIfAppropriate(lineNumber, t);
                            }

                        }
                    } finally {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    throw new SQLRuntimeException(e, sql);
                }

            } finally {
                csvReader.close();
            }

            if (resetAutonumberedPrimaryKey) {
                throw new UnsupportedOperationException("resetAutonumberedPrimaryKey not supported");
                //Sql.resetAutonumberedPK(connection, tableName);
            }

            return lineNumber - 1;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
