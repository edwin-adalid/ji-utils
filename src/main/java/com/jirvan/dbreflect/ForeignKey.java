package com.jirvan.dbreflect;

import java.util.List;

public class ForeignKey {

    public String tableName;
    public String schemaName;
    public String catalogName;
    public String remarks;                // explanatory comment on the table
    public List<Column> columns;
    public Reference referencingTable;
    public Reference referencedTable;

    public static class Reference {
        public String tableName;
        public List<String> columnNames;
    }

}
