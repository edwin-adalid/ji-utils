/*

Copyright (c) 2013, Jirvan Pty Ltd
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

package com.jirvan.jobpool;

import com.jirvan.io.OutputWriter;
import com.jirvan.lang.MessageException;
import com.jirvan.util.Utl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class JobPool {

    public static final JobPool commonJobPool = new JobPool();

    private Map<Long, Job> currentJobs = new HashMap<>();
    private int mostRecentJobId = 0;

    public static Job start(Task task) {
        return commonJobPool.startNewJobForTask(false, task);
    }

    public static Job start(Logger logger, Task task) {
        return commonJobPool.startNewJobForTask(false, logger, null, task);
    }

    private Job startNewJobForTask(boolean throwExceptions, Task task) {
        Job job = new Job(++mostRecentJobId);
        Runnable runnable = new JobRunnable(job, task, throwExceptions);
        new Thread(runnable).start();
        currentJobs.put(job.getJobId(), job);
        return job;
    }

    private Job startNewJobForTask(boolean throwExceptions, Logger logger, Level level, Task task) {
        Job job = new Job(++mostRecentJobId, logger, level);
        Runnable runnable = new JobRunnable(job, task, throwExceptions);
        new Thread(runnable).start();
        currentJobs.put(job.getJobId(), job);
        return job;
    }

    public Job getJob(long jobId) {
        return currentJobs.get(jobId);
    }

    public static abstract class Task {

        public abstract void perform(OutputWriter output);

    }

    public static class Job {

        private long jobId;
        private Status status;
        private StringWriter logWriter = new StringWriter();

        private boolean noLogger;

        public Job(long jobId) {
            this.jobId = jobId;
            this.status = Status.inProgress;
            noLogger = true;
        }

        public Job(long jobId, Logger logger, Level level) {
            this.jobId = jobId;
            this.status = Status.inProgress;
            noLogger = logger == null;

            if (logger != null) {
                LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
                Configuration config = ctx.getConfiguration();

                WriterAppender writerAppender = WriterAppender.newBuilder().setName("writeLogger").setTarget(logWriter)
                        .setLayout(PatternLayout.newBuilder().withPattern("%m\n").build()).build();
                writerAppender.start();
                config.addAppender(writerAppender);
                logger.addAppender(writerAppender);

                AppenderRef ref = AppenderRef.createAppenderRef("writeLogger", level, null);
                AppenderRef[] refs = new AppenderRef[] { ref };

                LoggerConfig loggerConfig = LoggerConfig.createLogger(false, level, "jobLogger" + this.jobId, null, refs, null, config,
                        null);

                loggerConfig.addAppender(writerAppender, level, null);
                config.addLogger("jobLogger" + this.jobId, loggerConfig);
                ctx.updateLoggers();
            }

        }

        public static enum Status {
            inProgress,
            finishedSuccessfully,
            finishedWithError
        }

        public long getJobId() {
            return jobId;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getLog() {
            return logWriter.toString();
        }

    }

    private static class JobRunnable implements Runnable {

        private Job job;
        private Task task;
        private boolean throwExceptions;

        public JobRunnable(Job job, Task task, boolean throwExceptions) {
            this.job = job;
            this.task = task;
            this.throwExceptions = throwExceptions;
        }

        public void run() {
            try {
//                task.perform(job.noLogger ? new OutputWriter(job.logWriter) : new OutputWriter());
                task.perform(new OutputWriter(job.logWriter));
                job.setStatus(Job.Status.finishedSuccessfully);
            } catch (Throwable t) {
                job.setStatus(Job.Status.finishedWithError);
                if (throwExceptions) {
                    if (t instanceof RuntimeException) {
                        throw (RuntimeException) t;
                    } else if (t instanceof Error) {
                        throw (Error) t;
                    } else {
                        throw new RuntimeException(t);
                    }
                } else {
                    if (t instanceof MessageException) {
                        System.err.printf("\nJob finished with ERROR: %s\n\n", t.getMessage());
                        job.logWriter.write(String.format("\nJob finished with ERROR: %s\n\n", t.getMessage()));
                    } else {
                        System.err.printf("\nJob finished with ERROR:\n\n %s\n\n", Utl.getStackTrace(t));
                        job.logWriter.write(String.format("\nJob finished with ERROR:\n\n %s\n\n", Utl.getStackTrace(t)));
                    }
                }
            }
        }

    }

}
