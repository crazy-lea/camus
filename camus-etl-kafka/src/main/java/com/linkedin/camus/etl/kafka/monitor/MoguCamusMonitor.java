package com.linkedin.camus.etl.kafka.monitor;

import com.linkedin.camus.config.utils.SmsSender;
import com.linkedin.camus.monitor.CamusMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: changye zhangxing
 * Date: 14-11-28 -- Time: 下午4:32
 */
public class MoguCamusMonitor implements CamusMonitor {
    private static Logger log = LoggerFactory.getLogger(MoguCamusMonitor.class);
    private String phone = "15669021269";

    public void jobSuccess(String jobName) {
        log.info("job success: " + jobName);
    }

    public void jobFail(String jobName) {
        log.info("job fail: " + jobName);
    }

    public void jobPullTimeReach(String jobName) {
        log.info("job reach pull time: " + jobName);
    }
}
