package com.example.demo.intercepter;

/*import com.alibaba.dubbo.config.annotation.Reference;
import com.cmos.net.iservice.system.IUserSV;*/
import com.example.demo.beans.common.UserInfo;
import com.example.demo.utils.annotation.LoginRequired;
import com.example.demo.common.utils.Constants;
import com.example.demo.utils.common.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    /*@Reference(group = "net")
    private IUserSV userService = null;*/

    /**
     * Autowired注解获取不到bean，通过WebApplicationContextUtils获取
     * @param clazz
     * @param request
     * @param <T>
     * @return
     */
    private <T> T getMapper(Class<T> clazz, HttpServletRequest request) {
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return factory.getBean(clazz);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从header中得到token
        String token = request.getHeader(Constants.AUTHORIZATION);
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }
        log.info("AuthorizationInterceptor request uri:" + request.getRequestURI() +"; token:" + token);

        //token校验不通过，返回false
        if (TokenUtil.verifyToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //获取用户信息，需要校验用户状态十分正常
        UserInfo userInfo = getUserType(token);
        request.setAttribute(Constants.USER_TYPE_SESSION, userInfo);

        //token校验通过，记录用户操作日志

        //判断接口是否需要登录
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if (null != loginRequired) {
                if (userInfo.isLogin()) {
                    request.setAttribute(Constants.USER_SESSION, userInfo.getUser());
                } else {
                    throw new Exception("3");
                }
            }
        }

        return true;
    }

    /**
     * 获取用户登录状态信息
     * @param token
     * @return
     * @throws Exception
     */
    private UserInfo getUserType(String token) throws Exception {
        UserInfo userInfo = new UserInfo();
        String userId = TokenUtil.getIdFromToken(token);
        if (StringUtils.isNotEmpty(userId)) {
            userInfo = null;//userService.getAuthUserInfo(Long.parseLong(userId));
            if (null == userInfo) {
                userInfo = new UserInfo();
                userInfo.setLogin(false);
                userInfo.setUserType("-1");
                userInfo.setMainRole(Constants.ROLE_MAIN.ROLE_GUEST);
            } else {
                userInfo.setLogin(true);
            }
        } else {
            userInfo.setLogin(false);
            userInfo.setUserType("-1");
            userInfo.setMainRole(Constants.ROLE_MAIN.ROLE_GUEST);
        }
        return userInfo;
    }
}
