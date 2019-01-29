package com.example.demo.cache.redis;

import com.example.demo.cache.cacheException.CMOSCacheBaseException;
import com.example.demo.cache.cacheException.CachePropertyException;
import com.example.demo.cache.cacheException.RouteException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jedisCluster对象工厂
 *
 * @author liuyan
 * @since 1.0
 */
public class JedisClusterFactory implements FactoryBean<JedisCluster>, InitializingBean {
    private static GenericObjectPoolConfig genericObjectPoolConfig;
    private static JedisCluster jedisCluster;
    private static int connectionTimeout = 2000;
    private static int soTimeout = 3000;
    private static int maxRedirections = 18;
    private Set<String> jedisClusterNodes;

    // 路由配置<路由名称,JedisCluster客户端>
    private static final Map<String, JedisCluster> CLUSTER_MAP = new ConcurrentHashMap<>();

    public void afterPropertiesSet() throws Exception {
        jedisCluster = createJedisCluster(jedisClusterNodes);
    }

    /**
     * 获取单集群配置下的JedisCluster对象
     *
     * @return JedisCluster对象
     */
    public static JedisCluster getJedisCluster() {
        if (CLUSTER_MAP.size() > 0) {
            throw new CMOSCacheBaseException(new RouteException("当前模式为集群路由模式,必须使用带路由参数的方法获取集群!"));
        }
        return jedisCluster;
    }

    /**
     * 获取多集群路由配置下的某个JedisCluster对象
     *
     * @param clusterName 集群名称
     * @return JedisCluster对象
     */
    public static JedisCluster getJedisCluster(String clusterName) {
        if (StringUtils.isBlank(clusterName)) {
            return getJedisCluster();
        }
        return CLUSTER_MAP.get(clusterName);
    }

    /**
     * 判断多集群路由配置下是否包含某个集群
     *
     * @param clusterName 集群名称
     * @return 如果包含, 则返回true, 否则返回false
     */
    public static boolean containsJedisCluster(String clusterName) {
        return CLUSTER_MAP.containsKey(clusterName);
    }

    /**
     * 获取所有集群名称
     *
     * @return
     */
    public static Set<String> getAllClusterNames() {
        return CLUSTER_MAP.keySet();
    }

    public JedisCluster getObject() throws Exception {
        return jedisCluster;
    }

    public Class<?> getObjectType() {
        return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
    }

    public boolean isSingleton() {
        return true;
    }

    public GenericObjectPoolConfig getGenericObjectPoolConfig() {
        return genericObjectPoolConfig;
    }

    public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
        this.genericObjectPoolConfig = genericObjectPoolConfig;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getMaxRedirections() {
        return maxRedirections;
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = maxRedirections;
    }

    public Set<String> getJedisClusterNodes() {
        return jedisClusterNodes;
    }

    public void setJedisClusterNodes(Set<String> jedisClusterNodes) {
        this.jedisClusterNodes = jedisClusterNodes;
    }

    /**
     * 添加路由集群
     *
     * @param name  集群名
     * @param nodes 集群节点信息
     */
    public void addRouterCluster(String name, Set<String> nodes) {
        JedisCluster cluster = null;
        if (CollectionUtils.isEmpty(nodes)) {
            throw new CMOSCacheBaseException(new CachePropertyException("集群'" + name + "'的Redis地址配置有误"));
        }
        try {
            cluster = createJedisCluster(nodes);
        } catch (Exception e) {
            throw new CMOSCacheBaseException("初始化JedisCluster失败,nodes=" + nodes, e);
        }
        CLUSTER_MAP.put(name, cluster);
    }

    /**
     * 创建JedisCluster对象
     *
     * @param nodes
     * @return
     * @throws ParseException
     */
    public static JedisCluster createJedisCluster(Set<String> nodes) throws ParseException {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        Set<HostAndPort> haps = new HashSet<HostAndPort>();
        for (String node : nodes) {
            String[] arr = node.split(":");
            if (arr.length != 2) {
                throw new CMOSCacheBaseException(new CachePropertyException("node address error !"));
            }
            haps.add(new HostAndPort(arr[0], Integer.valueOf(arr[1])));
        }
        return new JedisCluster(haps, connectionTimeout, soTimeout, maxRedirections, genericObjectPoolConfig);
    }

}