package com.example.demo.service.impl;

import com.example.demo.cache.service.CacheServiceFactory;
import com.example.demo.service.SequenceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class SequenceServiceImpl implements SequenceService {

    //编号key
    private static final String ID_KEY_DEMO = "demo";
    //自增长的值
    private final Long AUTO_INCREMENT = 1L;
    //定义自增长最大位数
    private final Integer DIGIT_LENGTH = 2;

    private DateFormat dfDay = new SimpleDateFormat("yyyyMMdd");
    private DateFormat dfTimestamp = new SimpleDateFormat("yyyyMMddHHmm");

    @Override
    public String getSequence(String key) {
        if (StringUtils.isBlank(key)) {
            log.error("传输key值不能为空。");
            return "传输key值不能为空。";
        }
        Date now = new Date();
        String day = dfDay.format(now);
        String timestamp = dfTimestamp.format(now);
        //新的一天，通过新key获取值
        String newKey = key+ "_" + day;

        try {
            Long num = CacheServiceFactory.getService().incrBy(newKey, AUTO_INCREMENT);
            //设置key过期时间
            if (num == 1) {
                CacheServiceFactory.getService().expire(newKey, getRemainSecondsToday());
            }
            //序列数到达指定上限，就删除key值重新获取
            if ((double) num == (Math.pow(10, DIGIT_LENGTH) - 1)) {
                CacheServiceFactory.getService().del(newKey);
            }
            //格式化num
            String id = String.valueOf(num);
            if (id.length() < DIGIT_LENGTH) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);
                nf.setMaximumIntegerDigits(DIGIT_LENGTH);
                nf.setMinimumIntegerDigits(DIGIT_LENGTH);
                id = nf.format(num);
            }
            return timestamp + id;
        } catch (Exception e) {
            log.error("redis生成id异常。", e);
        }
        return null;
    }

    /**
     * jdk获取当天剩余秒数
     * @return
     */
    private int getRemainSecondsToday() {
        Date currentDate = new Date();
        Calendar midnight = Calendar.getInstance();
        midnight.setTime(currentDate);
        midnight.add(midnight.DAY_OF_MONTH, 1);
        midnight.set(midnight.HOUR_OF_DAY, 0);
        midnight.set(midnight.MINUTE, 0);
        midnight.set(midnight.SECOND, 0);
        midnight.set(midnight.MILLISECOND, 0);
        return (int) (midnight.getTime().getTime() - currentDate.getTime()) / 1000;
    }


}
