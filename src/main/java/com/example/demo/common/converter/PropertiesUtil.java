package com.example.demo.common.converter;

import com.example.demo.config.ConfigHelper;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-8-9.
 */
public final class PropertiesUtil {

    private static Environment environment = null;

    static class StandarONestEnvironment extends StandardEnvironment {
        @Override
        protected void customizePropertySources(MutablePropertySources propertySources) {
            super.customizePropertySources(propertySources);
        }
    }

    /**
     * @param key 取得其值的键
     * @return key的值
     */
    public static String getValue(String key) {
        Environment environment = getEnvironment();
        assert (environment != null);
        return environment.getProperty(key);
    }

    public static Environment getEnvironment() {
        PropertiesUtil.environment = ConfigHelper.getEnvironment();
        if (environment == null) {
            environment = createStandarEnvironment(loadSystemProperties());
        }
        return environment;
    }

    private static Environment createStandarEnvironment(Properties properties) {
        StandarONestEnvironment env = new StandarONestEnvironment();
        env.getPropertySources().addLast(new PropertiesPropertySource("onestPropertySource", properties));
        return env;
    }

    private static Properties loadSystemProperties() {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("config/system.properties")) {
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException("加载本地配置文件失败", exception);
        }
        return properties;
    }


}
