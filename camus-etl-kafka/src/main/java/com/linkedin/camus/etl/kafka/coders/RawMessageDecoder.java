package com.linkedin.camus.etl.kafka.coders;

import com.linkedin.camus.coders.CamusWrapper;
import com.linkedin.camus.coders.MessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: changye zhangxing
 * Date: 14-11-19 -- Time: 下午7:29
 * 原样读取日志的Decoder
 */
public class RawMessageDecoder extends MessageDecoder<byte[], String> {
    private static Logger log = LoggerFactory.getLogger(RawMessageDecoder.class);

    @Override
    public CamusWrapper<String> decode(byte[] message) {
        String msg = new String(message).trim();
        if (msg.isEmpty()) {
            log.info("empty message");
            return null;
        }

        // 不解析日志, 将当前时间作为时间戳
        return new CamusWrapper<String>(msg, System.currentTimeMillis());
    }
}
