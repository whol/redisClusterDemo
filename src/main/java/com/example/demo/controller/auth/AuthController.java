package com.example.demo.controller.auth;

import com.alibaba.fastjson.JSONObject;
/*import com.alibaba.dubbo.config.annotation.Reference;
import com.cmos.net.iservice.param.IParamSV;
import com.cmos.net.iservice.system.IUserSV;*/
import com.example.demo.entity.FantasyUser;
import com.example.demo.service.FantasyUserService;
import com.example.demo.service.SequenceService;
import com.example.demo.utils.common.ConstantVar;
import com.example.demo.utils.common.DateUtil;
import com.example.demo.utils.common.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
@Validated
@Slf4j
@Api(value = "/auth", description = "token获取相关接口")
public class AuthController {

    @Autowired
    private FantasyUserService fantasyUserService;

    @Autowired
    private SequenceService sequenceService;

    /*@Reference(group = "net")
    private IParamSV paramSV = null;

    @Reference(group = "net")
    private IUserSV userSV = null;*/

    /**
     * 获取token信息
     * @param appId
     * @param appSecret
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/access", method = RequestMethod.GET)
    @ApiOperation(value = "/access", notes = "获取token信息", httpMethod = "GET",
    produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 500, message = "服务器内部错误", response = ResponseEntity.class),
            @ApiResponse(code = 200, message = "请求成功", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "请求资源不存在", response = ResponseEntity.class),
    })
    public ResponseEntity generateToken(@RequestParam(name="appId") String appId, @RequestParam(name="appSecret") String appSecret)
    throws Exception {
        //校验appId和appSecret是否和库存的一致
        String token = TokenUtil.generateToken();
        JSONObject data = new JSONObject();
        data.put(ConstantVar.ACCESS_TOKEN, token);
        data.put(ConstantVar.EXPIRED_IN, DateFormatUtils.format(TokenUtil.getExpirationDateFromToken(token),
                DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS));
        return new ResponseEntity(data, HttpStatus.OK);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    @ApiOperation(value = "/refresh", notes = "刷新token超时时间", httpMethod = "GET",
            produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 500, message = "服务器内部错误", response = ResponseEntity.class),
            @ApiResponse(code = 200, message = "请求成功", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "请求资源不存在", response = ResponseEntity.class),
    })
    public ResponseEntity refresh(@RequestParam(name="token") String token)
            throws Exception {
        String refreshToken = TokenUtil.refreshToken(token);
        if (StringUtils.isEmpty(refreshToken)) {
            throw new Exception();
        }
        JSONObject data = new JSONObject();
        data.put(ConstantVar.ACCESS_TOKEN, refreshToken);
        data.put(ConstantVar.EXPIRED_IN, DateFormatUtils.format(TokenUtil.getExpirationDateFromToken(token),
                DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS));
        return new ResponseEntity(data, HttpStatus.OK);
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ApiOperation(value = "/login", notes = "app需要支持自动登陆", httpMethod = "GET",
            produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 500, message = "服务器内部错误", response = ResponseEntity.class),
            @ApiResponse(code = 200, message = "请求成功", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "请求资源不存在", response = ResponseEntity.class),
    })
    public ResponseEntity login(@RequestParam(name="appId") String appId,
                                @RequestParam(name="appSecret") String appSecret,
                                @RequestParam(name="userId", required = false) String userId)
            throws Exception {
        //校验appId和appSecret是否和库存的一致
       /* Param param = null;
        try {
            param = new Param();//paramSV.getParamByTypeIdDataId("type", appId);
        } catch (Exception e) {
            log.error("获取参数异常", e);
        }
        if (null == param) {
            throw new NullPointerException("1");
        } else {
            if (!StringUtils.equals(appSecret, param.getDataName())) {
                throw new NullPointerException("2");
            }
        }*/

        String token;
        if (StringUtils.isEmpty(userId)) {
            token = TokenUtil.generateToken();
        } else {
            Integer userLogin = null;
            try {
                FantasyUser fantasyUser = new FantasyUser();
                fantasyUser.setUsername(userId);
                userLogin = fantasyUserService.checkFantasyUser(fantasyUser);//userSV.selectUserInfoByKeyForMap(Long.parseLong(userId));
            } catch (Exception e) {
                userLogin = null;
                log.error("APP初始化，根据用户编码获取不到用户信息，userId：" + userId, e);
            }
            if (null != userLogin) {
                token = TokenUtil.generateLoginToken(userId, "");
            } else {
                token = TokenUtil.generateToken();
            }
        }
        JSONObject data = new JSONObject();
        data.put(ConstantVar.ACCESS_TOKEN, token);
        data.put(ConstantVar.EXPIRED_IN, DateFormatUtils.format(TokenUtil.getExpirationDateFromToken(token),
                DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS));
        return new ResponseEntity(data, HttpStatus.OK);
    }



    @RequestMapping(value = "/getSequence", method = RequestMethod.GET)
    @ApiOperation(value = "/getSequence", notes = "redis生成唯一序列（时间戳yyyyMMddHHmm+自增数）", httpMethod = "GET",
            produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 500, message = "服务器内部错误", response = ResponseEntity.class),
            @ApiResponse(code = 200, message = "请求成功", response = ResponseEntity.class),
            @ApiResponse(code = 404, message = "请求资源不存在", response = ResponseEntity.class),
    })
    public JSONObject getSequence(@RequestParam String key) {

        JSONObject returnJson = new JSONObject();
        returnJson.put("rtnCd", 0);
        returnJson.put("rtnMsg", "redis生成唯一序列成功。");

        List<String> idList = new ArrayList<>();
        for (int i=0; i<110; i++) {
            String id = sequenceService.getSequence(key);
            if (null == id) {
                returnJson.put("rtnCd", -1);
                returnJson.put("rtnMsg", "redis生成id异常。");
                return returnJson;
            }
            idList.add(id);
        }

        returnJson.put("idList", idList);
        return returnJson;

    }
}
