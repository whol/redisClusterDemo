package com.example.demo.cache.config;

import com.example.demo.cache.cacheException.CMOSCacheBaseException;
import com.example.demo.cache.cacheException.entity.CustomException;
import com.example.demo.cache.guava.LocalCacheFactory;
import com.example.demo.cache.log.LogUtil;
import com.example.demo.cache.util.CheckClientUtil;
import com.example.demo.cache.util.CacheConstants;
import com.example.demo.cache.util.CacheConstants.PROPERTY_KYE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Order(3)
@Configuration
@EnableCaching
@PropertySource("classpath:" + com.example.demo.cache.config.CacheInnerConfig.CONFIG_FILE)
/**
 * Spring Cache组件，集群监控组件配置
 */
public class CacheInnerConfig {

    public static final String CONFIG_FILE = "config/cache.properties";

    @Autowired
    private Environment env;

    /**
     * 初始化缓存异常对象list
     *
     * @return
     */
    @Bean(name = "customExceptions")
    public List<CustomException> getCustomExceptions() {
        List<CustomException> customExceptions = new ArrayList<>();
        String cacheExceptionsPrefix = env.getProperty(PROPERTY_KYE.CACHEEXCEPTIONSPREFIX, String.class);

        String cacheExceptionStr = env.getProperty(PROPERTY_KYE.CACHEEXCEPTIONS, String.class);
        LogUtil.setCacheExceptionPrefix(cacheExceptionsPrefix);

        if (!StringUtils.isEmpty(cacheExceptionStr)) {
            String[] cacheExceptionArraySemicolon = cacheExceptionStr.split(";");
            for (String cacheExceptionSemicolon :
                    cacheExceptionArraySemicolon) {
                if (!StringUtils.isEmpty(cacheExceptionSemicolon)) {
                    String[] cacheExceptionArrayComma = cacheExceptionSemicolon.split(",");
                    CustomException customException = new CustomException();
                    customException.setName(cacheExceptionArrayComma[0]);
                    try {
                        int type = Integer.parseInt(cacheExceptionArrayComma[1]);
                        customException.setType(type);

                        int priority = Integer.parseInt(cacheExceptionArrayComma[2]);
                        customException.setPriority(priority);

                        int dealType = Integer.parseInt(cacheExceptionArrayComma[3]);
                        customException.setDealType(dealType);

                        customExceptions.add(customException);
                    } catch (Exception e) {
                        throw new CMOSCacheBaseException("读取配置文件出错",e);
                    }
                }
            }
        }
        LogUtil.setCustomExceptions(customExceptions);
        getClientValidate();
        return customExceptions;
    }

    /**
     * 获取是否做value验证
     *
     * @return
     */
    private void getClientValidate() {
        boolean checkValueIsBig = Boolean.parseBoolean(env.getProperty(PROPERTY_KYE.CHECKVALUEISBIG, String.class, "false"));

        boolean CheckValueisEmpty = Boolean.parseBoolean(env.getProperty(PROPERTY_KYE.CHECKVALUEISEMPTY, String.class, "false"));

        CheckClientUtil.setCheckValueIsBig(checkValueIsBig);
        CheckClientUtil.setCheckValueisEmpty(CheckValueisEmpty);
    }

    @Bean
    public LocalCacheConfig initCacheConfig() throws IOException {
        Map<String, Long> LocalCacheParameter = new HashMap<>();
        Map<String, String> keysRelateCache = new HashMap<>();
        LocalCacheConfig localCacheConfig = new LocalCacheConfig();
        LocalCacheFactory.setInitialCapacity(env.getProperty(PROPERTY_KYE.INITIALCAPCTITY,Integer.class,CacheConstants.GUAVA_CACHE_PROPERT.INITIALCAPCTITY));
        LocalCacheFactory.setMaximumSize(env.getProperty(PROPERTY_KYE.MAXIMUMSIZE,Long.class,CacheConstants.GUAVA_CACHE_PROPERT.MAXIMUMSIZE));
        LocalCacheFactory.setConcurrencyLevel(env.getProperty(PROPERTY_KYE.CONCURRENCYLEVEL,Integer.class,CacheConstants.GUAVA_CACHE_PROPERT.CONCURRENCYLEVEL));

        Set<String> cacheKeys = parseLocalCacheKey(env.getProperty(PROPERTY_KYE.RULESECONDKEYS, String.class, ""));
        if (cacheKeys.size() > 0) {
            LocalCacheParameter.put(CacheConstants.GUAVA_CACHE_PROPERT.SECONDCACHE, env.getProperty(PROPERTY_KYE.EXPIREAFTERWRITEINSECONDS, Long.class, CacheConstants.GUAVA_CACHE_PROPERT.EXPIREAFTERWRITEINSECONDS));
            for (String cachekey : cacheKeys) {
                keysRelateCache.put(cachekey, CacheConstants.GUAVA_CACHE_PROPERT.SECONDCACHE);
            }
        }

        cacheKeys = parseLocalCacheKey(env.getProperty(PROPERTY_KYE.RULEMINUTEKEYS, String.class, ""));
        if (cacheKeys.size() > 0) {
            LocalCacheParameter.put(CacheConstants.GUAVA_CACHE_PROPERT.MINUTECACHE, env.getProperty(PROPERTY_KYE.EXPIREAFTERWRITEINMINUTES, Long.class, CacheConstants.GUAVA_CACHE_PROPERT.EXPIREAFTERWRITEINMINUTES));
            for (String cachekey : cacheKeys) {
                keysRelateCache.put(cachekey, CacheConstants.GUAVA_CACHE_PROPERT.MINUTECACHE);
            }
        }

        cacheKeys = parseLocalCacheKey(env.getProperty(PROPERTY_KYE.RULEHOURKEYS, String.class, ""));
        if (cacheKeys.size() > 0) {
            LocalCacheParameter.put(CacheConstants.GUAVA_CACHE_PROPERT.HOURSCACHE, env.getProperty(PROPERTY_KYE.EXPIREAFTERWRITEINHOURS, Long.class, CacheConstants.GUAVA_CACHE_PROPERT.EXPIREAFTERWRITEINHOURS));
            for (String cachekey : cacheKeys) {
                keysRelateCache.put(cachekey, CacheConstants.GUAVA_CACHE_PROPERT.HOURSCACHE);
            }
        }

        localCacheConfig.setKeysRelateCache(keysRelateCache);
        localCacheConfig.setLocalCacheParameter(LocalCacheParameter);
        if (LocalCacheParameter.size() != 0) {
            localCacheConfig.addLocalCacheMap();
        }
        return localCacheConfig;
    }

    private Set<String> parseLocalCacheKey(String keys) {
        String[] LocalCacheKey = keys.split(",");
        Set<String> LocalCacheKeys = new HashSet<String>();
        for (String string : LocalCacheKey) {
            if (StringUtils.isEmpty(string)) {
                continue;
            }
            LocalCacheKeys.add(string);
        }
        return LocalCacheKeys;
    }
}
