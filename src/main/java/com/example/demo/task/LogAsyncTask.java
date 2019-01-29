package com.example.demo.task;

//import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
//import com.cmos.net.iservice.user.IUserLogSV;
import com.example.demo.beans.common.UserInfo;
import com.example.demo.beans.system.User;
import com.example.demo.beans.user.UserLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Date;

@Component
@Slf4j
public class LogAsyncTask {

    /*@Reference(group = "net")
    private IUserLogSV userLogSV = null;*/

    /**
     * 日志记录
     * @param log
     */
    @Async("logExecutor")
    public void asyncLoginLog(UserLog log) {
        System.out.println("保存日志: " + log);
        //userLogSV.insertSelective(log);
    }


    @Async("logExecutor")
    public void asyncSaveRecord(HttpServletRequest request, UserInfo userInfo) {
        try {
            UserLog userLog = new UserLog();
            if (null != request.getHeader("deviceInfos")) {
                JSONObject object = (JSONObject) JSONObject.parse(
                        URLDecoder.decode(request.getHeader("deviceInfos"), "UTF-8"));
                String uri = request.getRequestURI();
                if (StringUtils.isNotEmpty(uri)) {
                    userLog.setLogCode(uri);//日志编码
                    userLog.setLogTime(new Date());//日志时间
                    userLog.setLogContent(StringUtils.substring(request.getParameterMap().toString(), 5000));//日志内容
                    userLog.setModelType(object.getString("brand"));//设备型号
                    userLog.setModel(object.getString("uniqueId"));//设备标识
                    userLog.setModelName(object.getString("termModelName"));//设备标识
                    userLog.setVersionNumber(object.getString("version"));//版本号
                    userLog.setUserIp(object.getString("ip"));//用户IP
                    userLog.setUserMac(object.getString("MACAddress"));//MAC地址
                    if (StringUtils.isNotEmpty(object.getString("GPS"))
                    && null != object.getString("GPS")) {
                        userLog.setGpsLocation(object.getJSONObject("GPS").getString("regnNm"));//GPS定位
                    }
                    userLog.setOnlineType("");//上网方式
                    userLog.setRsrvStr1(object.getString("phoneNumber"));
                    userLog.setRsrvStr2(object.getString("readableVersion"));
                    if (userInfo.isLogin()) {
                        User user = userInfo.getUser();
                        userLog.setUserId(user.getUserId());
                        userLog.setUserCode(user.getUserCode());
                        userLog.setUserName(user.getUserName());
                        userLog.setSerialNumber(user.getSerialNumber());
                        userLog.setUserProvice(user.getUserProvice());
                        userLog.setUserCity(user.getUserCity());
                        userLog.setUserArea(user.getUserArea());
                        userLog.setUserType(user.getUserType());
                        userLog.setRegistTime(user.getRegistTime());
                        userLog.setCustManagerNo(user.getCustManagerNo());
                        userLog.setCustManagerLevel(user.getCustManagerLevel());
                    }

                    System.out.println("保存日志: " + userLog);
                    //userLogSV.insertSelective(userLog);
                }
            }
        } catch (Exception e) {
            log.error("记录操作日志失败:" + Thread.currentThread().getName(), e);
        }
    }
}
