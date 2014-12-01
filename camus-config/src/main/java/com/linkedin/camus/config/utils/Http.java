package com.linkedin.camus.config.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * User: changye zhangxing
 * Date: 14-11-25 -- Time: 下午2:09
 */
public class Http {
    private static Logger log = LoggerFactory.getLogger(Http.class);

    /**
     * 执行GET请求
     */
    public static String execGet(String url) {
        HttpURLConnection conn = null;
        try {
            URL u = new URL(url);
            conn= (HttpURLConnection) u.openConnection();
            conn.connect();
            byte[] bytes = readBytesFromInput(conn.getInputStream());
            if (bytes == null) return null;
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            log.error("exec get error", e);
            throw new RuntimeException("exec get error", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static byte[] readBytesFromInput(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        in.close();
        return out.toByteArray();
    }
}
