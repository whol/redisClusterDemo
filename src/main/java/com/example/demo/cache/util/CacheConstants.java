package com.example.demo.cache.util;

public final class CacheConstants {
    public interface CONFIG_NAME {
        String ZOOKEEPER = "zookeeper";
        String REDIS = "redis";
    }

    public interface PROPERTY_KYE {
        String MAXTOTAL = "MaxTotal";
        String MAXWAITMILLIS = "MaxWaitMillis";
        String MINIDLE = "MinIdle";
        String MAXREDIRECTIONS = "MaxRedirections";
        String MAXIDLE = "MaxIdle";
        String CONNECTIONTIMEOUT = "ConnectionTimeout";
        String SOTIMEOUT = "SoTimeout";
        String CONNECTIONTIMEOUTMS = "ConnectionTimeoutMs";
        String BASESLEEPTIMEMS = "BaseSleepTimeMs";
        String MAXRETRIES = "MaxRetries";
        String SESSIONTIMEOUTMS = "SessionTimeoutMs";
        String ZOOKEEPERADDRESS = "zookeeperAddress";
        String REDISADDRESS = "redisAddress";
        String LIVETIME = "LiveTime";
        String CACHEROUTER = "CacheRouter";
        String CACHECLUSTER = "CacheCluster";

        String LIFO = "Lifo";
        String MINEVICTABLEIDLETIMEMILLIS = "MinEvictableIdleTimeMillis";
        String SOFTMINEVICTABLEIDLETIMEMILLIS = "SoftMinEvictableIdleTimeMillis";
        String NUMTESTSPEREVICTIONRUN = "NumTestsPerEvictionRun";
        String EVICTIONPOLICYCLASSNAME = "EvictionPolicyClassName";
        String TESTONCREATE = "TestOnCreate";
        String TESTONBORROW = "TestOnBorrow";
        String TESTONRETURN = "TestOnReturn";
        String TESTWHILEIDLE = "TestWhileIdle";
        String TIMEBETWEENEVICTIONRUNSMILLIS = "TimeBetweenEvictionRunsMillis";
        String BLOCKWHENEXHAUSTED = "BlockWhenExhausted";
        String JMXENABLED = "JmxEnabled";
        String JMXNAMEPREFIX = "JmxNamePrefix";
        String JMXNAMEBASE = "JmxNameBase";

        String CACHEEXCEPTIONSPREFIX = "CacheExceptionsPrefix";
        String CACHEEXCEPTIONS = "CacheExceptions";

        String CHECKVALUEISBIG = "CheckValueIsBig";
        String CHECKVALUEISEMPTY = "CheckValueisEmpty";

        String RULESECONDKEYS="rule.second.keys";
        String RULEMINUTEKEYS="rule.minute.keys";
        String RULEHOURKEYS="rule.hour.keys";

        String  EXPIREAFTERWRITEINSECONDS="expire.write.in.seconds";
        String  EXPIREAFTERWRITEINMINUTES="expire.write.in.minutes";
        String  EXPIREAFTERWRITEINHOURS="expire.write.in.hours";
        String  INITIALCAPCTITY="initial.capctity";
        String  MAXIMUMSIZE="maximum.size";
        String  CONCURRENCYLEVEL="concurrency.level";
    }

    public interface GUAVA_CACHE_PROPERT{
        String SECONDCACHE="SECONDS_CACHE";
        String MINUTECACHE="MINUTES_CACHE";
        String HOURSCACHE="HOURS_CACHE";
        Long  EXPIREAFTERWRITEINSECONDS=30L;
        Long  EXPIREAFTERWRITEINMINUTES=2L;
        Long  EXPIREAFTERWRITEINHOURS=1L;
        int  INITIALCAPCTITY=100;
        Long  MAXIMUMSIZE=1000L;
        int  CONCURRENCYLEVEL=16;
    }
}
