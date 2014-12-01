package com.linkedin.camus.monitor;

/**
 * User: changye zhangxing
 * Date: 14-11-28 -- Time: 下午4:29
 * camus任务监控接口
 */
public interface CamusMonitor {
    void jobSuccess(String jobName);

    void jobFail(String jobName);

    void jobPullTimeReach(String jobName);
}
