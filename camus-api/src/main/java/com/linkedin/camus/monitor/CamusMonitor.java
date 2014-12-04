package com.linkedin.camus.monitor;

/**
 * User: changye zhangxing
 * Date: 14-11-28 -- Time: 下午4:29
 * camus任务监控接口
 */
public interface CamusMonitor {
    /**
     * 运行成功时调用
     * @param jobName job的名称
     */
    void jobSuccess(String jobName);

    /**
     * job运行失败时调用
     * @param jobName job的名称
     */
    void jobFail(String jobName);

    /**
     * job因达到pullTime而终止时调用
     * @param jobName job的名称
     */
    void jobPullTimeReach(String jobName);
}
