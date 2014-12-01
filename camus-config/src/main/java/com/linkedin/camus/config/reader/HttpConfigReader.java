package com.linkedin.camus.config.reader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.linkedin.camus.config.utils.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: changye zhangxing
 * Date: 14-11-25 -- Time: 下午1:49
 */
public class HttpConfigReader {
    private static Logger log = LoggerFactory.getLogger(HttpConfigReader.class);
    // 字段映射 config中的字段名 => camus的配置的key
    private static Map<String, String> fieldMap = new HashMap<String, String>();
    // 默认配置
    private static Properties defaultConfigMap = new Properties();
    private static String urlPrefix = "http://data.mogujie.org/exch/api/camusconfig/requestone.htm?groupName=";

    static {
        fieldMap.put("groupName", "groupName");
        fieldMap.put("topics", "kafka.whitelist.topics");
        fieldMap.put("destDir", "etl.destination.path");
        fieldMap.put("baseDir", "etl.execution.base.path");
        fieldMap.put("historyDir", "etl.execution.history.path");
        fieldMap.put("messageDecoder", "camus.message.decoder.class");
        fieldMap.put("writeProvider", "etl.record.writer.provider.class");
        fieldMap.put("partitioner", "etl.partitioner.class");
        fieldMap.put("compressMode", "etl.output.codec");
        fieldMap.put("maxMapNumber", "mapred.map.tasks");
        fieldMap.put("throttle", "kafka.mapper.max.qps");
        fieldMap.put("maxExecTime", "kafka.max.pull.minutes.per.task");
        fieldMap.put("kafkaMoveList", "kafka.move.to.last.offset.list");

        defaultConfigMap.put("fs.default.name", "hdfs://mgjcluster");
        defaultConfigMap.put("etl.camus.monitor.class", "com.linkedin.camus.etl.kafka.monitor.MoguCamusMonitor");
        defaultConfigMap.put("etl.default.timezone", "Asia/Shanghai");
        defaultConfigMap.put("camus.default.timezone", "Asia/Shanghai");
        defaultConfigMap.put("kafka.brokers",
                "10.11.7.23:9092,10.11.7.24:9092,10.11.7.25:9092,10.11.7.26:9092,10.11.7.161:9092,10.11.7.162:9092");
    }

    public Properties readByGroup(String group) {
        Properties prop = new Properties();
        prop.putAll(defaultConfigMap);

        String url = urlPrefix + group;
        String content = Http.execGet(url);
        if (content == null || "".equals(content)) {
            throw new IllegalStateException("no such group: " + group);
        }
        content = content.trim();

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(content, JsonObject.class);
        json = json.getAsJsonObject("object");

        for (String field : fieldMap.keySet()) {
            JsonElement elem = json.get(field);
            if (elem == null || elem.isJsonNull()) continue;
            String v = elem.getAsString();
            if (v == null || "".equals(v)) continue;
            prop.setProperty(fieldMap.get(field), v);
        }

        String groupName = prop.getProperty("groupName");
        if (groupName == null) {
            throw new IllegalStateException("no groupName in properties");
        }

        String topics = prop.getProperty("kafka.whitelist.topics");
        if (topics == null) {
            throw new IllegalStateException("kafka.whitelist.topics is empty");
        }

        String jobName = "camus_" + groupName;
        prop.setProperty("kafka.client.name", jobName);
        prop.setProperty("camus.job.name", jobName);

        String codec = prop.getProperty("etl.output.codec");
        if (codec == null || "".equals(codec)) {
            prop.setProperty("mapreduce.output.fileoutputformat.compress", "false");
        } else {
            prop.setProperty("mapreduce.output.fileoutputformat.compress", "true");
        }

        return prop;
    }

    public static void main(String[] args) {
        HttpConfigReader reader = new HttpConfigReader();
        System.out.println(reader.readByGroup("datacenter"));
        System.out.println(reader.readByGroup("items_action"));
    }
}
