package com.jirvan.dbreflect;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Table {

    public String tableName;
    public String schemaName;
    public String catalogName;
    public String remarks;                // explanatory comment on the table
    public List<Column> columns;
    public List<ForeignKey> foreignKeys;
    public List<ReferencingForeignKey> referencingForeignKeys;

    public static class ForeignKey {
        public List<String> columnNames;
        public String referencedTableName;
        public List<String> referencedColumnNames;
    }

    public static class ReferencingForeignKey {
        @JsonIgnore public Boolean isFromAnotherSchema;
        public String referencingTableName;
        public List<String> referencingColumnNames;
        public List<String> columnNames;
    }

}
