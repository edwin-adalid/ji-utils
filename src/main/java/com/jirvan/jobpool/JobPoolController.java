package com.jirvan.jobpool;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JobPoolController {

    @RequestMapping(value = "/jobpool/getJobDetails", method = RequestMethod.POST)
    public @ResponseBody JobPool.Job getJobDetails(@RequestBody Long jobId) {

        JobPool.Job job = JobPool.commonJobPool.getJob(jobId);
        if (job != null) {
            return job;
        } else {
            throw new RuntimeException(String.format("Job \"%s\" not found", jobId));
        }
    }

}
