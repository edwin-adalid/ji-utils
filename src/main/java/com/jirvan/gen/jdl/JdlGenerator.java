/*

Copyright (c) 2017 Jirvan Pty Ltd
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

package com.jirvan.gen.jdl;

import com.jirvan.dbreflect.Column;
import com.jirvan.dbreflect.DbReflect;
import com.jirvan.dbreflect.Table;
import com.jirvan.util.Io;
import com.jirvan.util.Jdbc;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Types;

import static com.jirvan.util.Assertions.*;

public class JdlGenerator {

    public static void main(String[] args) {
        boolean replaceExistingFile;
        String connectString;
        File outputFile = null;
        if (args.length == 0) {
            throw new RuntimeException("Usage: JdlGenerator [-r] connectString");
        } else if (args.length == 1) {
            replaceExistingFile = false;
            connectString = args[0];
        } else if (args.length == 2) {
            replaceExistingFile = "-r".equals(args[0]);
            connectString = args[1];
//        } else if (args.length == 3) {
//            replaceExistingFile = "-r".equals(args[0]);
//            connectString = args[1];
//            outputFile = new File(args[2]);
        } else {
            throw new RuntimeException("Usage: JdlGenerator [-r] connectString");
        }
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);
        generateJdlFile(Jdbc.getDataSource(connectString),
                        outputFile,
                        replaceExistingFile);
    }

    public static void generateJdlFile(DataSource dataSource,
                                       File outputFile,
                                       boolean replaceExistingFile) {
        if (outputFile == null) {
            outputFile = new File("jhipster-jdl.jh");
        } else {
            Io.ensureDirectoryExists(outputFile.getParentFile());
        }
        if (!replaceExistingFile) {
            assertFileDoesNotExist(outputFile);
        }
        try (PrintStream printStream = new PrintStream(outputFile)) {
            boolean firstTable = true;
            for (Table table : DbReflect.getTables(dataSource)) {
                if (!(table.tableName.toLowerCase().startsWith("jhi_") || table.tableName.toLowerCase().startsWith("databasechangelog"))) {
                    if (firstTable) {
                        firstTable = false;
                    } else {
                        printStream.printf("\n");
                    }
                    generateEntityJdl(printStream, table);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    //======================== Everything below here is private ========================//

    private static void generateEntityJdl(PrintStream printStream, Table table) {

        String entityName = DbReflect.underscoreSeperatedToCamelHumpName(table.tableName, true);

        // Generate main entity body
        String guessedIdColumn = guessIdColumn(table);
        printStream.printf("entity %s {", entityName);
        int longestFieldNameLength = getLongestFieldNameLength(table);
        int longestFieldTypeLength = getLongestFieldTypeLength(table);
        boolean firstColumn = true;
        for (Column column : table.columns) {
            if (!(column.columnName.equals(guessedIdColumn) || looksLikeAReferenceColumn(table, column))) {
                String fieldName = DbReflect.underscoreSeperatedToCamelHumpName(column.columnName, false);
                if (firstColumn) {
                    firstColumn = false;
                } else {
                    printStream.printf(",");
                }
                if (column.mandatory) {
                    printStream.printf("\n    %-" + longestFieldNameLength + "s   %-" + longestFieldTypeLength + "s  required",
                                       fieldName, getFieldType(column));
                } else {
                    printStream.printf("\n    %-" + longestFieldNameLength + "s   %s",
                                       fieldName, getFieldType(column));
                }
            }
        }
        printStream.printf("\n}");

        // Generate OneToMany relationships
        if (table.referencingForeignKeys.size() > 0) {
            printStream.printf(" relationship OneToMany {\n");
            for (int i = 0; i < table.referencingForeignKeys.size(); i++) {
                Table.ReferencingForeignKey referencingForeignKey = table.referencingForeignKeys.get(i);
                if (i > 0) printStream.printf(",\n");
                String lcEntityName = DbReflect.underscoreSeperatedToCamelHumpName(table.tableName, false);
                String referencedEntityName = DbReflect.underscoreSeperatedToCamelHumpName(referencingForeignKey.referencingTableName, true);
                String lcReferencedEntityName = DbReflect.underscoreSeperatedToCamelHumpName(referencingForeignKey.referencingTableName, false);
                String guessedNameField = guessNameField(table);
                if (guessedNameField != null) {
                    printStream.printf("    %s{%s} to %s{%s(%s)}",
                                       entityName,
                                       lcReferencedEntityName,
                                       referencedEntityName,
                                       lcEntityName,
                                       guessedNameField);
                } else {
                    printStream.printf("    %s{%s} to %s{%s}",
                                       entityName,
                                       lcReferencedEntityName,
                                       referencedEntityName,
                                       lcEntityName);
                }
            }
            printStream.printf("\n}");
        }


        printStream.printf("\n");

    }

    private static String guessIdColumn(Table table) {
        for (Column column : table.columns) {
            boolean isIdTypeColumnName = column.columnName.equalsIgnoreCase("id")
                                         || column.columnName.equalsIgnoreCase(table.tableName + "_id")
                                         || column.columnName.equalsIgnoreCase(table.tableName.replaceFirst("(?s)s$", "") + "_id");
            boolean isIdDataType = column.sqlType == Types.BIGINT
                                   || column.sqlType == Types.INTEGER;
            if (isIdTypeColumnName && isIdDataType) {
                return column.columnName;
            }
        }
        return null;
    }

    private static String guessNameField(Table table) {
        for (Column column : table.columns) {
            if (column.columnName.equalsIgnoreCase("name")
                || column.columnName.equalsIgnoreCase(table.tableName + "_name")
                || column.columnName.equalsIgnoreCase(table.tableName.replaceFirst("(?s)s$", "") + "_name")) {
                return DbReflect.underscoreSeperatedToCamelHumpName(column.columnName, false);
            }
        }
        return null;
    }

    private static boolean looksLikeAReferenceColumn(Table table, Column column) {
        for (Table.ForeignKey foreignKey : table.foreignKeys) {
            if (column.columnName.equalsIgnoreCase(foreignKey.referencedTableName + "_id")
                || column.columnName.equalsIgnoreCase(foreignKey.referencedTableName.replaceFirst("(?s)s$", "") + "_id")) {
                return true;
            }
        }
        return false;
    }

    private static int getLongestFieldNameLength(Table table) {
        int longestLength = 0;
        for (Column column : table.columns) {
            String fieldName = DbReflect.underscoreSeperatedToCamelHumpName(column.columnName, false);
            if (fieldName.length() > longestLength) longestLength = fieldName.length();
        }
        return longestLength;
    }

    private static int getLongestFieldTypeLength(Table table) {
        int longestLength = 0;
        for (Column column : table.columns) {
            String fieldType = getFieldType(column);
            if (fieldType.length() > longestLength) longestLength = fieldType.length();
        }
        return longestLength;
    }

    private static String getFieldType(Column column) {
        switch (column.sqlType) {
            case Types.VARCHAR:
                if (column.columnSize == 255) {
                    return "String";
                } else {
                    return "String maxlength(" + column.columnSize + ")";
                }
            case Types.BIGINT:
                return "Long";
            case Types.INTEGER:
                return "Integer";
            case Types.NUMERIC:
                return "BigDecimal";
            case Types.DATE:
                return "LocalDate";
            case Types.BIT:
                return "Boolean";
            case Types.BINARY:
                return "ZZZ";
            case Types.TIMESTAMP:
                return "ZZZ";
//                return "LocalDate";
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.DECIMAL:
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.TIME:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.NULL:
            case Types.OTHER:
            case Types.JAVA_OBJECT:
            case Types.DISTINCT:
            case Types.STRUCT:
            case Types.ARRAY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.REF:
            case Types.DATALINK:
            case Types.BOOLEAN:
            case Types.ROWID:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.NCLOB:
            case Types.SQLXML:
            default:
                throw new RuntimeException(String.format("JdlGenerator: Cannot handle sql type %d (%s)", column.sqlType, column.sqlTypeName));
        }
    }

}
