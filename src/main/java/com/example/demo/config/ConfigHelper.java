package com.example.demo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 全局环境配置帮助类，提供如下几个功能
 *
 * 1. 可以使用该获取当前Spring的上下文对象
 * 2. 获取全局环境配置对象：Environment
 * 3. 向全局环境对象添加或更新配置源
 * 4. 获取全局环境对象的所有配置源
 *
 * Created by alen on 17-8-3.
 */
@Slf4j
public final class ConfigHelper {

    private ConfigHelper() {
        // 不允许创建实例
    }

    /**
     * 重新刷新全局环境变量对象
     * @param inputStream 配置输入源
     * */
    public static void reloadEnvironment(InputStream inputStream) throws IOException {
        // 解析配置为Map
        List<String> lines = IOUtils.readLines(inputStream, SysConfig.ENCODING_UTF8);
        if (lines.size() > 0) {
            Gson gson = new GsonBuilder().create();
            Map props = gson.fromJson(lines.get(0), Map.class);
            ZookeeperPropertySource propertySource = ConfigHelper.getZookeeperPropertySource();
            propertySource.getProperties().putAll(props);
            ConfigHelper.addPropertySource(propertySource, ConfigHelper.Place.First);

        }

    }

    public static enum Place {
        Last,
        First,
        After,
        Before
    }

    private static PropertyTransformer propertyTransformer;

    /** 配置类 */
    private static ZookeeperPropertySource zookeeperPropertySource;

    // 全局上下文对象
    private static ApplicationContext applicationContext;

    // 全局环境变量对象
    private static Environment environment;

    /**
     * 常量
     */
    public static final String ZOOKEEPER_SOURCE_NAME = "zookeeperSource";

    public static void setPropertyTransformer(PropertyTransformer propertyTransformer) {
        if (ConfigHelper.propertyTransformer == null) {
            ConfigHelper.propertyTransformer = propertyTransformer;
        }
    }

    public static PropertyTransformer getPropertyTransformer() {
        return ConfigHelper.propertyTransformer;
    }

    public static void setEnvironment(Environment environment) {
        if (ConfigHelper.environment == null) {
            ConfigHelper.environment = environment;
        }
    }

    public static final Environment getEnvironment() {
        return ConfigHelper.environment;
    }

    public static final ApplicationContext getApplicationContext() {
        return ConfigHelper.applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ConfigHelper.applicationContext = applicationContext;
    }

    /***
     * 获取所有配置源
     * */
    public static List<PropertySource<?>> getPropertySources() {
        List<PropertySource<?>> propertySources = new ArrayList<PropertySource<?>>();
        for (PropertySource<?> propertySource : ((ConfigurableEnvironment)environment).getPropertySources()) {
            propertySources.add(propertySource);
        }
        return propertySources;
    }

    /**
     * 获取Zookeeper配置源
     * */
    public static ZookeeperPropertySource getZookeeperPropertySource() {
        if (zookeeperPropertySource == null) {
            zookeeperPropertySource = new ZookeeperPropertySource(ConfigHelper.ZOOKEEPER_SOURCE_NAME);
        }
        return zookeeperPropertySource;
    }

    /**
     * 根据配置源名称获取指定的配置源对象
     * @param propertySourceName 配置源名称
     * */
    public static PropertySource<?> getPropertySource(String propertySourceName) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) getEnvironment();
        return env.getPropertySources().get(propertySourceName);
    }

    /**
     * 获取当前所有配置源的配置项
     * */
    public static Properties getProperties() {
        return getProperties("");
    }

    /***
     * 获取以prefix作为前缀所有配置源的配置项
     * @param prefix
     * */
    public static Properties getProperties(String prefix) {
        Properties properties = new Properties();
        for (PropertySource propertySource : getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                String[] propertyNames = ((EnumerablePropertySource) propertySource).getPropertyNames();
                for (String propertyName : propertyNames) {
                    if (StringUtils.isEmpty(prefix) || propertyName.startsWith(prefix))
                        properties.put(propertyName, environment.getProperty(propertyName));
                }
            }
        }
        return properties;
    }

    /***
     * @param propertySource  配置源对象
     * @param place 添加位置 此处只允许添加到Place.Last | Place.First
     * */
    public static void addPropertySource(PropertySource<?> propertySource, Place place) {
        addPropertySource(propertySource, null, place);
    }

    /***
     * @param propertySource  配置源对象
     * @param relatedSourceName 添加相对位置的配置源名称
     * @param place 添加位置
     * */
    public static void addPropertySource(PropertySource<?> propertySource, String relatedSourceName, Place place) {
        if (StringUtils.isEmpty(propertySource.getName())) {
            throw new IllegalArgumentException("配置源未指定名称");
        }

        MutablePropertySources propertySources = ((ConfigurableEnvironment)environment).getPropertySources();

        if (propertySources.contains(propertySource.getName())) {
            // 只替换，不添加新的配置源
            propertySources.replace(propertySource.getName(), propertySource);
            return;
        }

        if (place == Place.First) {
            propertySources.addFirst(propertySource);
        } else if (place == place.Last) {
            propertySources.addLast(propertySource);
        } else {
            if (StringUtils.isEmpty(relatedSourceName)) {
                throw new IllegalArgumentException("必须指定相对位的配置源名称");
            }
            if (place == Place.Before) {
                propertySources.addBefore(relatedSourceName, propertySource);
            } else {
                propertySources.addAfter(relatedSourceName, propertySource);
            }
        }
    }

}
