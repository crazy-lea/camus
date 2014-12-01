package com.linkedin.camus.config;

import com.linkedin.camus.config.reader.HttpConfigReader;
import com.linkedin.camus.config.utils.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * User: changye zhangxing
 * Date: 14-11-25 -- Time: 下午3:32
 */
public class ConfigMain {
    private static Logger log = LoggerFactory.getLogger(ConfigMain.class);
    private static String configDir = "/etc/camusconfig";

    public static void main(String[] args) {
        HttpConfigReader reader = new HttpConfigReader();

        String group = null;
        try {
            if (args.length == 0) {
                throw new RuntimeException("first args is not group");
            }
            group = args[0];
            log.info("reading config for group: " + group);
            if (args.length>1) {
                configDir = args[1];
                log.info("change config directory to: " + configDir);
            }

            Properties prop = reader.readByGroup(group);
            if (prop == null) {
                throw new RuntimeException("配置为空");
            }
            String groupName = prop.getProperty("groupName");
            String filename = configDir + "/" + groupName + ".camus.properties";

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
            for (Object k : prop.keySet()) {
                writer.write(k.toString() + "=" + prop.getProperty(k.toString()));
                writer.newLine();
            }
            writer.close();

            log.info("write config for group " + group + " to file: " + filename);
        } catch (Exception e) {
            String error = "CAMUS读取配置失败, group: " + group + ", 失败信息: " + e.getMessage();
            log.error(error, e);
            alert(error);
        }
    }

    private static void alert(String msg) {
        SmsSender.send(msg, "15669021269,18610933482,15867103616");
    }
}
