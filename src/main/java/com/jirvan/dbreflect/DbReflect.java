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

package com.jirvan.dbreflect;

import com.jirvan.lang.SQLRuntimeException;
import com.jirvan.util.Jdbc;
import com.jirvan.util.Json;
import com.jirvan.util.Strings;
import com.jirvan.util.Utl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.jirvan.util.Assertions.*;
import static java.sql.DatabaseMetaData.*;

public class DbReflect {

    private static final Logger log = LoggerFactory.getLogger(DbReflect.class);

    public static void main(String[] args) {
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);
        List<Table> tables = getTables(Jdbc.getPostgresDataSource("cmsbdev/x@localhost/cmsbdev"));
//        List<Table> tables = getTables(Jdbc.getPostgresDataSource("jitimedev/x@localhost/jitimedev"));
        System.out.printf("\n%s\n", Json.toJsonString(tables));
    }

    public static String underscoreSeperatedToCamelHumpName(String name, boolean leadingCharacterUppercase) {
        if (name.matches(".*[a-z].*") && name.matches(".*[A-Z].*")) {
            return name;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            char[] chars = name.toLowerCase().toCharArray();
            boolean firstWordLetter = leadingCharacterUppercase;
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (c == '_') {
                    firstWordLetter = true;
                } else if (firstWordLetter) {
                    if ('a' <= c && c <= 'z') {
                        stringBuilder.append((char) (c + ('A' - 'a')));
                    } else {
                        stringBuilder.append(c);
                    }
                    firstWordLetter = false;
                } else {
                    stringBuilder.append(c);
                }
            }
            return stringBuilder.toString();
        }
    }

    public static List<Table> getTables(DataSource dataSource) {
        return getTables(dataSource, null, null);
    }

    public static List<Table> getTables(DataSource dataSource, String schemaName) {
        return getTables(dataSource, null, schemaName);
    }

    public static List<Table> getTables(DataSource dataSource, String catalogName, String schemaName) {
        try {
            Connection connection = dataSource.getConnection();
            try {
                ResultSet resultSet = connection.getMetaData().getTables(catalogName, schemaName, null, new String[]{"TABLE"});
                try {

                    List<Table> list = new ArrayList<>();
                    String lastCatalogName = "noneYet", lastSchemaName = "noneYet";
                    while (resultSet.next()) {

                        Table table = new Table();
                        table.catalogName = resultSet.getString("TABLE_CAT");
                        table.schemaName = resultSet.getString("TABLE_SCHEM");
                        table.tableName = resultSet.getString("TABLE_NAME");
                        table.remarks = resultSet.getString("REMARKS");

                        if (!Strings.isIn(lastCatalogName, "noneYet", table.catalogName)) {
                            throw new RuntimeException(String.format("Catalog must be specified if database has more than one (found %s and %s, there could be others)",
                                                                     lastCatalogName, table.catalogName));
                        }

                        if (!Strings.isIn(lastSchemaName, "noneYet", table.schemaName)) {
                            throw new RuntimeException(String.format("Schema must be specified if database has more than one (found %s and %s, there could be others)",
                                                                     lastSchemaName, table.schemaName));
                        }

                        table.columns = getColumns(connection, table.catalogName, table.schemaName, table.tableName);

                        table.referencingForeignKeys = getReferencingForeignKeys(connection, table.catalogName, table.schemaName, table.tableName);

                        list.add(table);

                    }
                    addForeignKeysForAllTables(list);
                    Collections.sort(list, new Comparator<Table>() {
                        @Override public int compare(Table o1, Table o2) {
                            return o1.tableName.compareTo(o2.tableName);
                        }
                    });
                    return list;

                } finally {
                    resultSet.close();
                }
            } finally {
                connection.close();
            }

        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }


    //======================== Everything below here is private ========================//

    private static String getSqlTypeName(int type) {
        switch (type) {
            case Types.BIT:
                return "BIT";
            case Types.TINYINT:
                return "TINYINT";
            case Types.SMALLINT:
                return "SMALLINT";
            case Types.INTEGER:
                return "INTEGER";
            case Types.BIGINT:
                return "BIGINT";
            case Types.FLOAT:
                return "FLOAT";
            case Types.REAL:
                return "REAL";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.NUMERIC:
                return "NUMERIC";
            case Types.DECIMAL:
                return "DECIMAL";
            case Types.CHAR:
                return "CHAR";
            case Types.VARCHAR:
                return "VARCHAR";
            case Types.LONGVARCHAR:
                return "LONGVARCHAR";
            case Types.DATE:
                return "DATE";
            case Types.TIME:
                return "TIME";
            case Types.TIMESTAMP:
                return "TIMESTAMP";
            case Types.BINARY:
                return "BINARY";
            case Types.VARBINARY:
                return "VARBINARY";
            case Types.LONGVARBINARY:
                return "LONGVARBINARY";
            case Types.NULL:
                return "NULL";
            case Types.OTHER:
                return "OTHER";
            case Types.JAVA_OBJECT:
                return "JAVA_OBJECT";
            case Types.DISTINCT:
                return "DISTINCT";
            case Types.STRUCT:
                return "STRUCT";
            case Types.ARRAY:
                return "ARRAY";
            case Types.BLOB:
                return "BLOB";
            case Types.CLOB:
                return "CLOB";
            case Types.REF:
                return "REF";
            case Types.DATALINK:
                return "DATALINK";
            case Types.BOOLEAN:
                return "BOOLEAN";
            case Types.ROWID:
                return "ROWID";
            case Types.NCHAR:
                return "NCHAR";
            case Types.NVARCHAR:
                return "NVARCHAR";
            case Types.LONGNVARCHAR:
                return "LONGNVARCHAR";
            case Types.NCLOB:
                return "NCLOB";
            case Types.SQLXML:
                return "SQLXML";
        }
        throw new RuntimeException("Unrecognized jdbc data type \"" + type + "\"");
    }

    private static List<String> getPkColumnNames(Connection connection, String catalogName, String schemaName, String tableName) {
        List<String> list = new ArrayList<String>();
        try {
            ResultSet resultSet = connection.getMetaData().getPrimaryKeys(catalogName, schemaName, tableName);
            try {

                while (resultSet.next()) {
                    list.add(resultSet.getString("COLUMN_NAME"));
                }

            } finally {
                resultSet.close();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
        return list;
    }

    private static List<Column> getColumns(Connection connection, String catalogName, String schemaName, String tableName) {
        List<String> pkColumnNames = getPkColumnNames(connection, catalogName, schemaName, tableName);
        List<Column> list = new ArrayList<>();
        try {
            try (ResultSet resultSet = connection.getMetaData().getColumns(catalogName, schemaName, tableName, null)) {

                while (resultSet.next()) {
                    Column column = new Column();
                    column.columnName = resultSet.getString("COLUMN_NAME");
                    column.sqlType = resultSet.getInt("DATA_TYPE");
                    column.sqlTypeName = getSqlTypeName(column.sqlType);
                    column.columnSize = resultSet.getInt("COLUMN_SIZE");
                    column.decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
                    column.isInPrimaryKey = Strings.isIn(column.columnName, pkColumnNames);
                    column.mandatory = resultSet.getShort("NULLABLE") == typeNoNulls;
                    list.add(column);
                }

            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
//        Collections.sort(list, new Comparator<Column>() {
//            @Override public int compare(Column o1, Column o2) {
//                return o1.columnName.compareTo(o2.columnName);
//            }
//        });
        return list;
    }

    private static List<Table.ReferencingForeignKey> getReferencingForeignKeys(Connection connection, String catalogName, String schemaName, String tableName) throws SQLException {

        try (ResultSet resultSet = connection.getMetaData().getExportedKeys(catalogName, schemaName, tableName)) {
            List<Table.ReferencingForeignKey> list = new ArrayList<>();
            Table.ReferencingForeignKey foreignKey = null;
            ExportedKeyRow lastExportedKeyRow = null;
            while (resultSet.next()) {

                ExportedKeyRow exportedKeyRow = new ExportedKeyRow(resultSet);

                if (lastExportedKeyRow == null) {
                    foreignKey = startKey(exportedKeyRow);
                } else {
                    if (exportedKeyRow.hasSameFkTableAs(lastExportedKeyRow)) {
                        if (exportedKeyRow.keySeq == 1) {
                            if (foreignKey != null) endKey(list, catalogName, schemaName, lastExportedKeyRow, foreignKey);
                            foreignKey = startKey(exportedKeyRow);
                        } else {
                            addToKey(foreignKey, lastExportedKeyRow, exportedKeyRow);
                        }
                    } else {
                        if (foreignKey != null) endKey(list, catalogName, schemaName, lastExportedKeyRow, foreignKey);
                        foreignKey = startKey(exportedKeyRow);
                    }
                }

                lastExportedKeyRow = exportedKeyRow;

            }
            // End current key if any and return list
            if (foreignKey != null) endKey(list, catalogName, schemaName, lastExportedKeyRow, foreignKey);
            Collections.sort(list, new Comparator<Table.ReferencingForeignKey>() {
                @Override public int compare(Table.ReferencingForeignKey o1, Table.ReferencingForeignKey o2) {
                    return o1.referencingTableName.compareTo(o2.referencingTableName);
                }
            });
            return list;

        }

    }

    private static void addForeignKeysForAllTables(List<Table> tables) throws SQLException {
        for (Table table : tables) {
            table.foreignKeys = new ArrayList<>();
            for (Table otherTable : tables) {
                for (Table.ReferencingForeignKey referencingForeignKey : otherTable.referencingForeignKeys) {
                    if (referencingForeignKey.referencingTableName.equals(table.tableName)) {
                        Table.ForeignKey foreignKey = new Table.ForeignKey();
                        foreignKey.columnNames = new ArrayList<>();
                        foreignKey.referencedColumnNames = new ArrayList<>();
                        for (String referencingColumnName : referencingForeignKey.referencingColumnNames) {
                            foreignKey.columnNames.add(referencingColumnName);
                        }
                        foreignKey.referencedTableName = otherTable.tableName;
                        for (String columName : referencingForeignKey.columnNames) {
                            foreignKey.referencedColumnNames.add(columName);
                        }
                        table.foreignKeys.add(foreignKey);
                    }
                }
            }
            Collections.sort(table.foreignKeys, new Comparator<Table.ForeignKey>() {
                @Override public int compare(Table.ForeignKey o1, Table.ForeignKey o2) {
                    return o1.referencedTableName.compareTo(o2.referencedTableName);
                }
            });

        }
    }

    private static void endKey(List<Table.ReferencingForeignKey> list, String catalogName, String schemaName, ExportedKeyRow keyRow, Table.ReferencingForeignKey foreignKey) {
        if (Utl.areEqual(keyRow.fkCatalogName, catalogName) && Utl.areEqual(keyRow.fkSchemaName, schemaName)) {
            list.add(foreignKey);
        } else {
            log.warn("Ignoring exported key for table {} (foreign key table is {}.{}.{}", keyRow.pkTableName, keyRow.fkCatalogName, keyRow.fkSchemaName, keyRow.fkTableName);
        }
    }

    private static void addToKey(Table.ReferencingForeignKey foreignKey, ExportedKeyRow lastExportedKeyRow, ExportedKeyRow exportedKeyRow) {

        // Check key sequence and foreign key name are consistent
        assertTrue(exportedKeyRow.keySeq == lastExportedKeyRow.keySeq + 1, String.format("Expected column keySeq (%d) for exported key to be the next one (%d)", exportedKeyRow.keySeq, lastExportedKeyRow.keySeq + 1));
        assertEquals(exportedKeyRow.fkName, lastExportedKeyRow.fkName, String.format("Expected foreign key name for column (%s) to be the same as the last one (%s)", exportedKeyRow.fkName, lastExportedKeyRow.fkName));

        // Add columns
        foreignKey.referencingColumnNames.add(exportedKeyRow.fkColumnName);
        foreignKey.columnNames.add(exportedKeyRow.pkColumnName);

    }

    private static Table.ReferencingForeignKey startKey(ExportedKeyRow exportedKeyRow) {
        Table.ReferencingForeignKey foreignKey;
        assertTrue(exportedKeyRow.keySeq == 1, "Expected first column for exported key to have a keySeq of 1");
        foreignKey = new Table.ReferencingForeignKey();
        foreignKey.referencingTableName = exportedKeyRow.fkTableName;
        foreignKey.referencingColumnNames = new ArrayList<>();
        foreignKey.referencingColumnNames.add(exportedKeyRow.fkColumnName);
        foreignKey.columnNames = new ArrayList<>();
        foreignKey.columnNames.add(exportedKeyRow.pkColumnName);
        return foreignKey;
    }

    private static class ExportedKeyRow {

        public String fkName;
        public int keySeq;
        public String fkCatalogName;
        public String fkSchemaName;
        public String fkTableName;
        public String fkColumnName;
        public String pkCatalogName;
        public String pkSchemaName;
        public String pkTableName;
        public String pkColumnName;

        public ExportedKeyRow(ResultSet resultSet) throws SQLException {
            this.fkName = resultSet.getString("FK_NAME");
            this.keySeq = resultSet.getInt("KEY_SEQ");
            this.fkCatalogName = resultSet.getString("FKTABLE_CAT");
            this.fkSchemaName = resultSet.getString("FKTABLE_SCHEM");
            this.fkTableName = resultSet.getString("FKTABLE_NAME");
            this.fkColumnName = resultSet.getString("FKCOLUMN_NAME");
            this.pkCatalogName = resultSet.getString("PKTABLE_CAT");
            this.pkSchemaName = resultSet.getString("PKTABLE_SCHEM");
            this.pkTableName = resultSet.getString("PKTABLE_NAME");
            this.pkColumnName = resultSet.getString("PKCOLUMN_NAME");
        }

        public boolean hasSameFkTableAs(ExportedKeyRow anotherKeyRow) {
            return Utl.areEqual(anotherKeyRow.fkCatalogName, fkCatalogName)
                   && Utl.areEqual(anotherKeyRow.fkSchemaName, fkSchemaName)
                   && Utl.areEqual(anotherKeyRow.fkTableName, fkTableName);
        }
    }

}

/*

                List<String> columNames = new ArrayList<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    columNames.add(resultSet.getMetaData().getColumnName(i));
                }
                System.out.printf("\n%s\n", Json.toJsonString(columNames));

 */