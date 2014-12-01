package com.linkedin.camus.etl.kafka.coders;

import com.linkedin.camus.etl.IEtlKey;
import org.apache.hadoop.mapreduce.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: changye zhangxing
 * Date: 14-11-19 -- Time: 下午7:20
 */
public class MoguPartitioner extends DefaultPartitioner {
    private static Logger log = LoggerFactory.getLogger(MoguPartitioner.class);
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HH");

    @Override
    public String encodePartition(JobContext context, IEtlKey key) {
        return format.format(new Date(key.getTime()));
    }

    @Override
    public String generatePartitionedPath(JobContext context, String topic, String brokerId,
                                          int partitionId, long offset, String encodedPartition) {
        return topic + "/" + encodedPartition + "_" + long2String(partitionId, 3) + "_" + long2String(offset, 12);
    }

    private String long2String(long i, int width) {
        return String.format("%0" + width + "d", i);
    }
}
