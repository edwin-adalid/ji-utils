/*

Copyright (c) 2019, Jirvan Pty Ltd
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

import com.google.common.collect.PeekingIterator;
import com.jirvan.dates.Day;
import com.jirvan.dates.Hour;
import com.jirvan.dates.Millisecond;
import com.jirvan.dates.Minute;
import com.jirvan.dates.Month;
import com.jirvan.dates.Second;
import com.jirvan.util.Utl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CsvIterable<T> implements Iterable<T> {

    private PeekingIterator iterator;
    private Class<T> rowClass;

    public CsvIterable(Class rowClass, InputStream inputStream) {
        this(rowClass, inputStream, true, true);
    }

    public CsvIterable(Class rowClass, InputStream inputStream, boolean interpretEmptyStringsAsNulls, boolean validateRows) {
        this.rowClass = rowClass;
        this.iterator = new InternalIterator(inputStream, interpretEmptyStringsAsNulls, validateRows);
    }

    public PeekingIterator<T> iterator() {
        return this.iterator;
    }

    private void validateHeader(CSVRecord headerRecord) {

        int index = 0;
        for (Field f : rowClass.getDeclaredFields()) {
            if (headerRecord.size() < index + 1) {
                throw new RuntimeException(String.format("Column header \"%s\" is missing", f.getName()));
            }
            String headerRecordHeadername = headerRecord.get(index++);
            if (!headerRecordHeadername.equals(f.getName())) {
                throw new RuntimeException(String.format("Invalid header \"%s\" (expected %s)", headerRecordHeadername, f.getName()));
            }
        }

    }

    private class InternalIterator implements PeekingIterator {

        private Iterator<CSVRecord> csvParserIterator;
        private boolean interpretEmptyStringsAsNulls;
        private boolean validateRows;
        private T nextRow;

        public InternalIterator(InputStream inputStream, boolean interpretEmptyStringsAsNulls, boolean validateRows) {
            this.interpretEmptyStringsAsNulls = interpretEmptyStringsAsNulls;
            this.validateRows = validateRows;
            try {
                csvParserIterator = CSVFormat.EXCEL.withIgnoreEmptyLines()
                                                   .parse(new InputStreamReader(inputStream)).iterator();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (csvParserIterator.hasNext()) {
                CSVRecord header = csvParserIterator.next();
                validateHeader(header);
                nextRow = getNextRowFromCsvParser();
            } else {
                this.nextRow = null;
            }
        }

        public boolean hasNext() {
            return this.nextRow != null;
        }

        public T peek() {
            return nextRow;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public T next() {
            if (this.nextRow == null) {
                throw new NoSuchElementException();
            } else {
                T row = this.nextRow;
                nextRow = getNextRowFromCsvParser();
                return row;
            }
        }

        private T getNextRowFromCsvParser() {
            if (csvParserIterator.hasNext()) {
                try {
                    CSVRecord csvRecord = csvParserIterator.next();
                    T row = rowClass.newInstance();
                    int index = 0;
                    for (Field field : rowClass.getDeclaredFields()) {
                        if (csvRecord.size() < index + 1) {
                            throw new RuntimeException(String.format("Row %d: value for column \"%s\" is missing", csvRecord.getRecordNumber(), field.getName()));
                        }
                        String value = csvRecord.get(index++);
                        try {
                            setFieldValue(row, field, value, interpretEmptyStringsAsNulls);
                        } catch (Throwable t) {
                            throw new RuntimeException(String.format("Row %d, column \"%s\": %s", csvRecord.getRecordNumber(), field.getName(), Utl.coalesce(t.getMessage(), t.getClass().getSimpleName())), t);
                        }
                    }
                    if (validateRows) {
                        try {
                            Utl.validate(row);
                        } catch (Throwable t) {
                            throw new RuntimeException(String.format("Row %d: %s", csvRecord.getRecordNumber(), Utl.coalesce(t.getMessage(), t.getClass().getSimpleName())), t);
                        }
                    }
                    return row;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return null;
            }
        }

    }

    private void setFieldValue(T row, Field field, String stringValue, boolean interpretEmptyStringsAsNulls) throws IllegalAccessException {
        if (field.getType() == String.class) {
            if (interpretEmptyStringsAsNulls) {
                field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : stringValue);
            } else {
                field.set(row, stringValue);
            }
        } else if (field.getType() == Integer.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Integer.parseInt(stringValue.trim()));
        } else if (field.getType() == Long.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Long.parseLong(stringValue.trim()));
        } else if (field.getType() == BigDecimal.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : new BigDecimal(stringValue.trim()));
        } else if (field.getType() == Boolean.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Boolean.parseBoolean(stringValue.trim()));
        } else if (field.getType() == Month.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Month.fromString(stringValue.trim()));
        } else if (field.getType() == Day.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Day.fromString(stringValue.trim()));
        } else if (field.getType() == LocalDate.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : LocalDate.parse(stringValue.trim()));
        } else if (field.getType() == LocalDateTime.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : LocalDateTime.parse(stringValue.trim()));
        } else if (field.getType() == Hour.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Hour.fromString(stringValue.trim()));
        } else if (field.getType() == Minute.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Minute.fromString(stringValue.trim()));
        } else if (field.getType() == Second.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Second.fromString(stringValue.trim()));
        } else if (field.getType() == Millisecond.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Millisecond.fromString(stringValue.trim()));
        } else if (field.getType() == ZonedDateTime.class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : ZonedDateTime.parse(stringValue.trim()));
        } else if (Enum.class.isAssignableFrom(field.getType())) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : Enum.valueOf((Class<? extends Enum>) field.getType(), stringValue.trim()));
        } else if (field.getType() == byte[].class) {
            field.set(row, stringValue == null || stringValue.trim().length() == 0 ? null : stringValue.getBytes());
        } else {
            throw new UnsupportedOperationException(String.format("Cannot process values of type \"%s\"", field.getType().getName()));
        }
    }

}
