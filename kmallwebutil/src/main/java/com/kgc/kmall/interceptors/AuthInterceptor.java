package com.kgc.kmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.util.CookieUtil;
import com.kgc.kmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否是HandlerMethod，因为访问静态资源时handler是ResourceHttpRequestHandler
        if (handler.getClass().equals(HandlerMethod.class)) {
            //获取注解信息
            HandlerMethod hm = (HandlerMethod) handler;
            LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);
            // 没有LoginRequired注解不拦截
            if (methodAnnotation == null) {
                return true;
            }

            //获取token
            //如果cookie中和request中都有token，则说明原来的登录没有失效，但是又重新登录了，这时request中的token要覆盖cookie中的token
            //如果只有cookie中有token，则说明原来登录状态未失效，继续使用此token进行验证
            //如果只有request中有token，说明原登录状态失效，或第一次登录，使用此token进行验证
            //如果都没有，则说明没有登录，需要返回进行登录
            String token = "";
            String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
            if (StringUtils.isNotBlank(oldToken)) {
                token = oldToken;
            }

            String newToken = request.getParameter("token");
            if (StringUtils.isNotBlank(newToken)) {
                token = newToken;
            }

            //token为空验证不通过
            String result="fail";
            Map<String,String> successMap = new HashMap<>();
            if (StringUtils.isNotBlank(token)){
                // 通过nginx转发的客户端ip
                //String ip = request.getHeader("x-forwarded-for");

                //调用验证中心的验证方法进行验证
                String ip = request.getRemoteAddr();// 从request中获取ip
                if(StringUtils.isBlank(ip)||ip.equals("0:0:0:0:0:0:0:1")){
                    ip = "127.0.0.1";
                }
                String successJson  = HttpclientUtil.doGet("http://127.0.0.1:8086/verify?token=" + token+"&currentIp="+ip);

                successMap = JSON.parseObject(successJson,Map.class);
                result = successMap.get("status");
            }

            // 是否必须登录,true必须登录
            boolean loginSuccess = methodAnnotation.value();// 获得该请求是否必登录成功
            if (loginSuccess) {
                if (!result.equals("success")) {
                    //重定向会passport登录
                    StringBuffer requestURL = request.getRequestURL();
                    response.sendRedirect("http://localhost:8086/index?ReturnUrl="+requestURL);
                    return false;
                }

                // 需要将token携带的用户信息写入
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("nickname", successMap.get("nickname"));
                //验证通过，覆盖cookie中的token
                if(StringUtils.isNotBlank(token)){
                    CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
                }

            } else {
                if (result.equals("success")) {
                    // 需要将token携带的用户信息写入
                    request.setAttribute("memberId", successMap.get("memberId"));
                    request.setAttribute("nickname", successMap.get("nickname"));

                    //验证通过，覆盖cookie中的token
                    if(StringUtils.isNotBlank(token)){
                        CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
                    }

                }
            }
        }
        return true;
    }
}