package com.example.demo.common.exception.http;

import com.example.demo.common.exception.GeneralException;
import com.example.demo.common.exception.util.ErrorStackBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
class CmosLoggerAdapter {

    private static final String DUBBO_EXCEPTIONFILTER = "com.alibaba.dubbo.rpc.filter.ExceptionFilter";

    static void recordException(String errorMessage, Throwable error) {
        if (null != error && error.getStackTrace().length > 0) {
            String firstStackTraceClassName = error.getStackTrace()[0].getClassName();
            if (!(error instanceof GeneralException) && DUBBO_EXCEPTIONFILTER.equals(firstStackTraceClassName))
                try {
                    error = ErrorStackBuilder.rebuildException(ErrorStackBuilder.build(error.getMessage(), Charset.forName("UTF-8")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        log.error(errorMessage, error);
    }
}
