/*

Copyright (c) 2017, Jirvan Pty Ltd
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

import com.jirvan.dates.Millisecond;
import com.jirvan.dates.Minute;
import com.jirvan.dates.Second;
import com.jirvan.io.OutputWriter;

import java.io.PrintStream;
import java.util.Date;

public class Timer {

    public static TimeStampDisplayPrecision timeStampDisplayPrecision = TimeStampDisplayPrecision.minute;
    public static DurationDisplayPrecision durationDisplayPrecision = DurationDisplayPrecision.second;

    /**
     * Creates a new timer which you can later start with {@link #startPeriod()}.
     * You could also create an immediately started timer if you didn't need
     * multiple "active" periods with {@link #startNew(String)}.
     * <p>
     * If you want a "snapshot blocked" timer that will prevent releases being built with the timer code,
     * then use SnapshotMarker.newTimer(String) instead of this method.
     *
     * @param timerTitle the title for the timer
     * @return the timer
     * @see #startPeriod()
     * @see #startNew(String)
     */
    public static Timer newTimer(String timerTitle) {
        return newTimer(timerTitle, true);
    }

    /**
     * Creates a new timer which you can later start with {@link #startPeriod()}.
     * You could also create an immediately started timer if you didn't need
     * multiple "active" periods with {@link #startNew(String, boolean)}.
     * <p>
     * If you want a "snapshot blocked" timer that will prevent releases being built with the timer code,
     * then use SnapshotMarker.newTimer(String, boolean) instead of this method.
     *
     * @param timerTitle                 the title for the timer
     * @param printStartAndFinishMessage whether to print a start and finish message
     * @return the timer
     */
    public static Timer newTimer(String timerTitle, boolean printStartAndFinishMessage) {
        return new Timer(timerTitle, printStartAndFinishMessage);
    }

    /**
     * Creates a new timer which you can later start with {@link #startPeriod()}.
     * You could also create an immediately started timer if you didn't need
     * multiple "active" periods with {@link #startNew(String, boolean)}.
     * <p>
     * If you want a "snapshot blocked" timer that will prevent releases being built with the timer code,
     * then use SnapshotMarker.newTimer(String, boolean) instead of this method.
     *
     * @param output                     an OutputWriter to write to
     * @param timerTitle                 the title for the timer
     * @param printStartAndFinishMessage whether to print a start and finish message
     * @return the timer
     * @see OutputWriter
     */
    public static Timer newTimer(OutputWriter output, String timerTitle, boolean printStartAndFinishMessage) {
        return new Timer(output, timerTitle, printStartAndFinishMessage);
    }

    /**
     * Creates and starts a new timer.  If you need a timer that can handle
     * multiple "active" periods then use {@link #newTimer(String)}.
     * <p>
     * If you want a "snapshot blocked" timer that will prevent releases being built with the timer code,
     * then use SnapshotMarker.startNew(String) instead of this method.
     *
     * @param timerTitle the title for the timer
     * @return the started timer
     * @see #newTimer(String)
     */
    public static Timer startNew(String timerTitle) {
        return startNew(timerTitle, true);
    }

    /**
     * Creates and starts a new timer.  If you need a timer that can handle
     * multiple "active" periods then use {@link #newTimer(String)}.
     * <p>
     * If you want a "snapshot blocked" timer that will prevent releases being built with the timer code,
     * then use SnapshotMarker.startNew(String) instead of this method.
     *
     * @param output     an OutputWriter to write to
     * @param timerTitle the title for the timer
     * @return the started timer
     * @see #newTimer(String)
     */
    public static Timer startNew(OutputWriter output, String timerTitle) {
        return startNew(output, timerTitle, true);
    }

    /**
     * Creates and starts a new timer.  If you need a timer that can handle
     * multiple "active" periods then use {@link #newTimer(String, boolean)}.
     * <p>
     * If you want a "snapshot blocked" timer that will prevent releases being built with the timer code,
     * then use SnapshotMarker.startNew(String, boolean) instead of this method.
     *
     * @param output                     an OutputWriter to write to
     * @param timerTitle                 the title for the timer
     * @param printStartAndFinishMessage whether to print a start and finish message
     * @return the started timer
     * @see #newTimer(String, boolean)
     * @see OutputWriter
     */
    public static Timer startNew(OutputWriter output, String timerTitle, boolean printStartAndFinishMessage) {
        Timer timer = new Timer(output, timerTitle, printStartAndFinishMessage);
        timer.startTimer();
        return timer;
    }

    /**
     * Creates and starts a new timer.  If you need a timer that can handle
     * multiple "active" periods then use {@link #newTimer(String, boolean)}.
     * <p>
     * If you want a "snapshot blocked" timer that will prevent releases being built with the timer code,
     * then use SnapshotMarker.startNew(String, boolean) instead of this method.
     *
     * @param timerTitle                 the title for the timer
     * @param printStartAndFinishMessage whether to print a start and finish message
     * @return the started timer
     * @see #newTimer(String, boolean)
     */
    public static Timer startNew(String timerTitle, boolean printStartAndFinishMessage) {
        Timer timer = new Timer(timerTitle, printStartAndFinishMessage);
        timer.startTimer();
        return timer;
    }

    /**
     * Get the timer title
     *
     * @return the timer title
     */
    public String getTitle() {
        return title;
    }

    /**
     * End the timer (and print finished message if appropriate) and "restart" it
     * for re-use.  This is only used if the timer was originally started with
     * {@link #startNew(String)} of {@link #startNew(String, boolean)}
     * and an "active periods" type timer is not being used (one created
     * with {@link #newTimer(String)} or {@link #newTimer(String, boolean)}).
     */
    public void endAndStartNew(String title) {
        this.endTimer();
        this.title = title;
        this.totalElapsedTime = 0;
        this.startTimer();
    }

    /**
     * End the timer (and print finished message if appropriate).  This is only
     * used if the timer was started with {@link #startNew(String)} of
     * {@link #startNew(String, boolean)} and an "active periods" type timer
     * is not being used (one created with {@link #newTimer(String)}
     * or {@link #newTimer(String, boolean)}).
     */
    public void endTimer() {
        Date endDate = new Date();
        if (printStartAndFinishMessage) {
            if (output.isAtStartOfLine()) {
                output.printf("%s  Finished %s  (%s)\n",
                              formatTimeStamp(endDate),
                              title,
                              formatDuration(endDate.getTime() - start));
            } else {
                output.printfAndEndLine("  (%s)\n", formatDuration(endDate.getTime() - start));
            }
        }
    }

    /**
     * Start a period of activity for the timer.
     */
    public void startPeriod() {
        if (periodStart != null) {
            throw new RuntimeException("Cannot start a period for %s (a period has already been started");
        } else {
            periodStart = new Date().getTime();
        }
    }

    /**
     * End a period of activity for the timer.
     */
    public void endPeriod() {
        if (periodStart == null) {
            throw new RuntimeException("Cannot end period for %s (a period has not been started");
        } else {
            totalElapsedTime += (new Date().getTime() - periodStart);
            periodStart = null;
        }
    }

    /**
     * Get the total elapsed time in milliseconds
     *
     * @return the total elapsed time in milliseconds
     */
    public long getTotalElapsedTime() {
        if (periodStart != null) {
            throw new RuntimeException("Cannot get total elapsed time for %s (a period has been started but not finished");
        } else {
            return totalElapsedTime;
        }
    }

    /**
     * Print the total elapsed time (formatted in an appropriate output line)
     * to the supplied PrintStream.
     *
     * @param printStream the PrintStream to print to
     * @see PrintStream
     */
    public void printTotalElapsedTimeString(PrintStream printStream) {
        if (periodStart != null) {
            throw new RuntimeException("Cannot get total elapsed time for %s (a period has been started but not finished");
        } else {
            printStream.printf("Total time spent on \"%s\": %s\n",
                               title,
                               formatDuration(totalElapsedTime));
        }
    }

    /**
     * Print the total elapsed time (formatted in an appropriate output line)
     * to the supplied OutputWriter.
     *
     * @param outputWriter the OutputWriter to print to
     * @see OutputWriter
     */
    public void printTotalElapsedTimeString(OutputWriter outputWriter) {
        if (periodStart != null) {
            throw new RuntimeException("Cannot get total elapsed time for %s (a period has been started but not finished");
        } else {
            outputWriter.printf("Total time spent on \"%s\": %s\n",
                                title,
                                formatDuration(totalElapsedTime));
        }
    }

    /**
     * Print the total elapsed time (formatted in an appropriate output line).
     *
     * @see OutputWriter
     */
    public void printTotalElapsedTimeString() {
        printTotalElapsedTimeString(this.output);
    }

    public static enum TimeStampDisplayPrecision {
        millisecond,
        second,
        minute
    }

    public static enum DurationDisplayPrecision {
        millisecond,
        second,
        minute
    }

    //======================== Everything below here is private ========================//

    private String title;
    private boolean printStartAndFinishMessage;
    private long totalElapsedTime;
    private long start;
    private Long periodStart;
    private OutputWriter output;

    private Timer(String title, boolean printStartAndFinishMessage) {
        this(new OutputWriter(System.out), title, printStartAndFinishMessage);
    }

    private Timer(OutputWriter output, String title, boolean printStartAndFinishMessage) {
        this.title = title;
        this.totalElapsedTime = 0;
        this.printStartAndFinishMessage = printStartAndFinishMessage;
        this.output = output;
    }

    private void startTimer() {
        Date startDate = new Date();
        this.start = startDate.getTime();
        this.periodStart = this.start;
        if (printStartAndFinishMessage) {
                output.printfAndWaitForLineEnd("%s  %s",
                              formatTimeStamp(startDate),
                              this.title);
        }
    }

    private String formatDuration(long totalElapsedTime) {
        if (durationDisplayPrecision == DurationDisplayPrecision.millisecond) {
            return Millisecond.formatDuration(totalElapsedTime);
        } else if (durationDisplayPrecision == DurationDisplayPrecision.second) {
            return Second.formatDuration(totalElapsedTime / 1000);
        } else if (durationDisplayPrecision == DurationDisplayPrecision.minute) {
            return Minute.formatDuration(totalElapsedTime / (60 * 1000));
        } else {
            throw new RuntimeException(String.format("Unexpected TimeStampDisplayPrecision \"%s\"", timeStampDisplayPrecision.name()));
        }
    }

    private String formatTimeStamp(Date startDate) {
        if (timeStampDisplayPrecision == TimeStampDisplayPrecision.millisecond) {
            return Millisecond.from(startDate).toString();
        } else if (timeStampDisplayPrecision == TimeStampDisplayPrecision.second) {
            return Second.from(startDate).toString();
        } else if (timeStampDisplayPrecision == TimeStampDisplayPrecision.minute) {
            return Minute.from(startDate).toString();
        } else {
            throw new RuntimeException(String.format("Unexpected TimeStampDisplayPrecision \"%s\"", timeStampDisplayPrecision.name()));
        }
    }

}
