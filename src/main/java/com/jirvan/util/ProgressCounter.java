package com.jirvan.util;

import com.google.common.base.Strings;
import com.jirvan.io.OutputWriter;

public class ProgressCounter {

    public OutputWriter output;
    public Integer total;
    public int progress = 0;
    public int lineCounter = 0;
    public int maxCountsPerLine;
    public String indent;
    public String lineFormat;

    public ProgressCounter(OutputWriter output) {
        this(output, null, 3, 80);
    }

    public ProgressCounter(OutputWriter output,
                           Integer total) {
        this(output, total, 3, 80);
    }

    public ProgressCounter(OutputWriter output,
                           Integer total,
                           int indentLength,
                           int lineLength) {
        this.output = output;
        this.total = total;
        this.indent = Strings.padEnd("", indentLength, ' ');
        this.lineFormat = total == null ? "\n" + indent : " %d%%\n" + indent;
        this.maxCountsPerLine = lineLength - indentLength - 4;
    }

    public void incrementByOne(char displayChar) {
        if (progress == 0) {
            output.printf(indent);
        }
        progress++;
        if (++lineCounter > maxCountsPerLine) {
            if (total == null) {
                output.printf(lineFormat);
            } else {
                output.printf(lineFormat, (progress * 100) / total);
            }
            lineCounter = 1;
        }
        if (total == null || progress < total) {
            output.printf("%c", displayChar);
        }
    }

}
