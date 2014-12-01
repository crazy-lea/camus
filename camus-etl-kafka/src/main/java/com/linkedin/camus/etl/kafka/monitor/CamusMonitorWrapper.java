package com.linkedin.camus.etl.kafka.monitor;

import com.linkedin.camus.monitor.CamusMonitor;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: changye zhangxing
 * Date: 14-11-28 -- Time: 下午4:40
 */
public class CamusMonitorWrapper {
    private static Logger log = LoggerFactory.getLogger(CamusMonitorWrapper.class);

    public static final String ETL_CAMUS_MONITOR_CLASS = "etl.camus.monitor.class";
    public static final String CAMUS_JOB_NAME = "camus.job.name";

    private static CamusMonitorWrapper instance = new CamusMonitorWrapper();
    private static CamusMonitor monitor;
    private static String jobName;

    public synchronized static void init(Configuration conf) {
        if (monitor != null) {
            log.warn("already init " + ETL_CAMUS_MONITOR_CLASS);
            return;
        }

        if (conf == null) {
            log.error("conf is null");
            return;
        }

        String monitorClass = conf.get(ETL_CAMUS_MONITOR_CLASS);
        if (monitorClass == null) {
            log.warn("no config for " + ETL_CAMUS_MONITOR_CLASS);
            return;
        }
        try {
            Object obj = Class.forName(monitorClass).getConstructor().newInstance();
            if (obj instanceof CamusMonitor) {
                monitor = (CamusMonitor) obj;
                jobName = conf.get(CAMUS_JOB_NAME, "Camus Job");
            } else {
                log.error(monitorClass + " instance is not implement " + CamusMonitor.class.getName() + " interface");
            }
        } catch (Exception e) {
            log.error("new monitor class " + monitorClass + " error", e);
        }
    }

    public static CamusMonitorWrapper getInstance() {
        return instance;
    }

    private CamusMonitorWrapper() {
    }

    public void jobSuccess() {
        if (monitor != null && jobName != null) {
            monitor.jobSuccess(jobName);
        }
    }

    public void jobFail() {
        if (monitor != null && jobName != null) {
            monitor.jobFail(jobName);
        }
    }

    public void jobPullTimeReach() {
        if (monitor != null && jobName != null) {
            monitor.jobPullTimeReach(jobName);
        }
    }
}
