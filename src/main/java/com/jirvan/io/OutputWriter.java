/*

Copyright (c) 2014,2015,2016,2017,2018 Jirvan Pty Ltd
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

package com.jirvan.io;

import com.jirvan.util.Strings;
import com.jirvan.util.Utl;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OutputWriter {

    private List<Logger> loggers = new ArrayList<>();
    private List<org.apache.log4j.Logger> log4jLoggers = new ArrayList<>();
    private List<OutputStream> outputStreams = new ArrayList<>();
    private List<Writer> writers = new ArrayList<>();
    private List<OutputWriter> outputWriters = new ArrayList<>();
    private boolean waitingForLineEnd = false;
    private boolean atStartOfLine = true;
    private List<String> linePrefixSections = new ArrayList<>();
    private String linePrefix;

    public OutputWriter(OutputStream... outputStreams) {
        this.outputStreams.addAll(Arrays.asList(outputStreams));
    }

    public OutputWriter(Logger logger1, Logger... loggers2ToN) {
        add(logger1, loggers2ToN);
    }

    public OutputWriter(org.apache.log4j.Logger logger1, org.apache.log4j.Logger... loggers2ToN) {
        add(logger1, loggers2ToN);
    }

    public OutputWriter(Writer outputWriter1, Writer... outputWriters2ToN) {
        add(outputWriter1, outputWriters2ToN);
    }

    public OutputWriter(OutputWriter outputWriter1, OutputWriter... outputWriters2ToN) {
        add(outputWriter1, outputWriters2ToN);
    }

    public String getLinePrefix() {
        return linePrefix;
    }

    /**
     * Resets the line prefix (normally from a saved list of sections returned by
     * an earlier pushLinePrefix.
     *
     * @param linePrefixSections The new line prefix sections
     * @return The original line prefix sections
     */
    public List<String> resetLinePrefix(List<String> linePrefixSections) {
        List<String> originalLinePrefixSections = this.linePrefixSections;
        this.linePrefixSections = linePrefixSections;
        this.linePrefix = Strings.join(linePrefixSections, "");
        return originalLinePrefixSections;
    }

    /**
     * Pushes a section onto the end of the line prefix
     *
     * @param linePrefix The line prefix sections to add
     * @return The original line prefix sections
     */
    public List<String> pushLinePrefix(String linePrefix) {
        List<String> originalLinePrefixSections = this.linePrefixSections;
        linePrefixSections.add(Utl.coalesce(linePrefix, ""));
        this.linePrefix = Strings.join(this.linePrefixSections, "");
        return originalLinePrefixSections;
    }

    /**
     * Pops the most recently added section of the line prefix
     *
     * @return the "popped" section
     */
    public String popLinePrefix() {
        String poppedSection;
        if (linePrefixSections.size() > 0) {
            poppedSection = linePrefixSections.remove(linePrefixSections.size() - 1);
        } else {
            poppedSection = "";
        }
        this.linePrefix = Strings.join(linePrefixSections, "");
        return poppedSection;
    }

    public boolean isAtStartOfLine() {
        return atStartOfLine;
    }

    public OutputWriter add(OutputWriter outputWriter1, OutputWriter... outputWriters2ToN) {
        this.outputWriters.add(outputWriter1);
        this.outputWriters.addAll(Arrays.asList(outputWriters2ToN));
        return this;
    }

    public OutputWriter add(Logger logger1, Logger... loggers2ToN) {
        this.loggers.add(logger1);
        this.loggers.addAll(Arrays.asList(loggers2ToN));
        return this;
    }

    public OutputWriter add(org.apache.log4j.Logger logger1, org.apache.log4j.Logger... loggers2ToN) {
        this.log4jLoggers.add(logger1);
        this.log4jLoggers.addAll(Arrays.asList(loggers2ToN));
        return this;
    }

    public OutputWriter add(OutputStream outputStream1, OutputStream... outputStreams2ToN) {
        this.outputStreams.add(outputStream1);
        this.outputStreams.addAll(Arrays.asList(outputStreams2ToN));
        return this;
    }

    public OutputWriter add(Writer writer1, Writer... writers2ToN) {
        this.writers.add(writer1);
        this.writers.addAll(Arrays.asList(writers2ToN));
        return this;
    }

    public OutputWriter printf(String format, Object... args) {
        printToAllOutputs(String.format(format, args), false, false);
        return this;
    }

    public OutputWriter printfAndEndLine(String format, Object... args) {
        printToAllOutputs(String.format(format, args), true, false);
        return this;
    }

    public OutputWriter printfAndWaitForLineEnd(String format, Object... args) {
        printToAllOutputs(String.format(format, args), false, true);
        return this;
    }

    public OutputWriter printf(Logger additionalLoggerToPrintTo, String format, Object... args) {
        String formattedString = String.format(format, args);
        additionalLoggerToPrintTo.info(formattedString.replaceFirst("\\n$", ""));
//        additionalLoggerToPrintTo.info(formattedString.replaceFirst("^\\n", "").replaceFirst("\\n$", ""));
        printToAllOutputs(formattedString, false, false);
        return this;
    }

    public OutputWriter printf(org.apache.log4j.Logger additionalLoggerToPrintTo, String format, Object... args) {
        String formattedString = String.format(format, args);
        additionalLoggerToPrintTo.info(formattedString.replaceFirst("\\n$", ""));
//        additionalLoggerToPrintTo.info(formattedString.replaceFirst("^\\n", "").replaceFirst("\\n$", ""));
        printToAllOutputs(formattedString, false, false);
        return this;
    }

    public WriterProxy getWriterProxy() {
        return new WriterProxy();
    }

    private void printToAllOutputs(String formattedString, boolean endLine, boolean thenWaitForEndLine) {
        try {
            String stringToPrint;
            if (linePrefix == null) {
                stringToPrint = formattedString;
            } else {
                stringToPrint = formattedString.replaceAll("\\n([^$])", "\n" + linePrefix + "$1");
                if (atStartOfLine) {
                    stringToPrint = linePrefix + stringToPrint;
                }
            }
            for (Logger logger : loggers) {
                logger.info(stringToPrint.replaceFirst("^\\n", "").replaceFirst("\\n$", ""));
            }
            for (org.apache.log4j.Logger logger : log4jLoggers) {
                logger.info(stringToPrint.replaceFirst("^\\n", "").replaceFirst("\\n$", ""));
            }
            for (OutputStream outputStream : outputStreams) {
                if (waitingForLineEnd & !endLine) {
                    outputStream.write('\n');
                    if (linePrefix != null) outputStream.write(linePrefix.getBytes());
                }
                outputStream.write(stringToPrint.getBytes());
                outputStream.flush();
            }
            for (Writer writer : writers) {
                if (waitingForLineEnd & !endLine) {
                    writer.write('\n');
                    if (linePrefix != null) writer.write(linePrefix);
                }
                writer.write(stringToPrint);
                writer.flush();
            }
            for (OutputWriter outputWriter : outputWriters) {
                if (waitingForLineEnd & !endLine) {
                    outputWriter.printf("\n");
                    if (linePrefix != null) outputWriter.printf(linePrefix);
                }
                outputWriter.printf(stringToPrint);
            }
            atStartOfLine = formattedString.endsWith("\n");
            waitingForLineEnd = thenWaitForEndLine;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class WriterProxy extends Writer {

        public void write(char[] cbuf, int off, int len) throws IOException {
            printf("%s", new String(cbuf, off, len));
        }

        public void flush() throws IOException {
            // Do nothing - flushing is automatic
        }

        public void close() throws IOException {
            // Do nothing - dependent streams etc need to have control of closure themselves
        }
    }


}
