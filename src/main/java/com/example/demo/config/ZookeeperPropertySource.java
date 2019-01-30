package com.example.demo.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.Enumeration;
import java.util.Properties;

/**
 * 可遍历的动态Zookeeper配置源
 *
 * Created by alen on 16-12-1.
 */
public class ZookeeperPropertySource extends EnumerablePropertySource<Properties> {

    private String zkServers;

    private ConfigurableEnvironment environment;

    /**
     * 构造函数
     * @param name
     */
    public ZookeeperPropertySource(String name) {
        super(name, new Properties());
    }


    @Override
    public String[] getPropertyNames() {
        Enumeration<?> e = getSource().propertyNames();

        String[] propertyNames = new String[this.getSource().size()];

        int index = 0;
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            propertyNames[index++] = key;
        }

        return propertyNames;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Object getProperty(String key) {
        if (!StringUtils.isBlank(SysConfig.INST_CODE)) {
            String instanceKey = key + "." + SysConfig.INST_CODE;
            if (getSource().containsKey(instanceKey)) {
                Object value = getSource().getProperty(instanceKey);
                return transformValue(instanceKey, value);
            }
        }
        return transformValue(key, getSource().get(key));
    }

    private Object transformValue(String key, Object value) {
        PropertyTransformer transformer = ConfigHelper.getPropertyTransformer();
        if (transformer != null) {
            value = transformer.transform(key, value != null ? value.toString() : "");
        }
        return value;
    }

    public Properties getProperties() {
        return getSource();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZookeeperPropertySource)){
            return false;
        }
        if (!super.equals(o)){
            return false;
        }

        ZookeeperPropertySource that = (ZookeeperPropertySource) o;

        if (getProperties() != null ? !getProperties().equals(that.getProperties()) : that.getProperties() != null)
            return false;
        if (getZkServers() != null ? !getZkServers().equals(that.getZkServers()) : that.getZkServers() != null)
            return false;
        return environment != null ? environment.equals(that.environment) : that.environment == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getProperties() != null ? getProperties().hashCode() : 0);
        result = 31 * result + (getZkServers() != null ? getZkServers().hashCode() : 0);
        result = 31 * result + (environment != null ? environment.hashCode() : 0);
        return result;
    }
}
