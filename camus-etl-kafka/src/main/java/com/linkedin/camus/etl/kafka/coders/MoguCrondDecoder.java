package com.linkedin.camus.etl.kafka.coders;

import com.linkedin.camus.coders.CamusWrapper;
import com.linkedin.camus.coders.MessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * User: changye zhangxing
 * Date: 14-11-18 -- Time: 下午7:23
 * 解析 Mogujie Crond 日志
 */
public class MoguCrondDecoder extends MessageDecoder<byte[], String> {
    private static Logger log = LoggerFactory.getLogger(MoguCrondDecoder.class);
    private static String timeFormat = "yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat format = new SimpleDateFormat(timeFormat);

    @Override
    public CamusWrapper<String> decode(byte[] message) {
        String msg = new String(message).trim();
        if (msg.isEmpty()) {
            log.info("empty message");
            return null;
        }

        if (msg.length()<timeFormat.length()) {
            log.info("invalid message");
            return null;
        }
        String timeStr = msg.substring(0, timeFormat.length());
        long time;
        try {
            time = format.parse(timeStr).getTime();
        } catch (Exception e) {
            log.info("time format error: " + timeStr + ", expected: " + timeFormat);
            return null;
        }
        return new CamusWrapper<String>(msg, time);
    }
}
