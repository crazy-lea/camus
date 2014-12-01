package com.linkedin.camus.config.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * User: changye zhangxing
 * Date: 14-2-19 -- Time: 下午2:21
 */
public class SmsSender {
    private static Logger log = LoggerFactory.getLogger(SmsSender.class);

    /**
     * 通过mogujie短信平台发送短信
     *
     * @param msg   短信内容
     * @param phone 手机号码. 可以多个
     */
    public static void send(String msg, String phone) {
        log.info("sms, to: " + phone + ", content: " + msg);

        int maxLen = 60;
        if (msg.length() > maxLen) {
            List<String> msgs = new ArrayList<String>();
            int i = 0;
            while (true) {
                int end = i + maxLen > msg.length() ? msg.length() : i + maxLen;
                if (i >= end) break;
                String m = msg.substring(i, end);
                i = end;
                msgs.add(m);
            }
            for (String m : msgs) {
                send(m, phone);
            }
            return;
        }

        HttpClient client = new HttpClient();
        String url = "http://10.11.3.204/receive.php?type=sms";
        GetMethod method = null;
        try {
            url = url + getSmsContent(phone, msg);
            method = new GetMethod(url);
            client.executeMethod(method);
            log.info("response status code: " + method.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.error("sms send error", e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
    }

    private static String getSmsContent(String to, String msg) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("&key=")
                .append(URLEncoder.encode("related to the Japanese earthquake and tsunami", "utf-8"))
                .append("&")
                .append("to=")
                .append(to)
                .append("&").append("content=")
                .append(URLEncoder.encode(msg, "utf-8"))
                .append("&eventname=kuma");
        return sb.toString();
    }

    public static void main(String[] args) {
        send("hello world你好", "15669021269");
    }
}
