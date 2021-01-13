package com.kgc.kmall.interceptors;

import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      //  System.out.println("测试拦截器");
        //判断是否是后端handler
        if (handler.equals(HandlerMethod.class)) {
            //是后端，使用反射获取方法上的LoginRequired注解
            HandlerMethod handlerMethod= (HandlerMethod) handler;
            LoginRequired methodAnnotation=handlerMethod.getMethodAnnotation(LoginRequired.class);
            if (methodAnnotation!=null) {
                String token = "";
                String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
                if (StringUtils.isNotBlank(oldToken)) {
                    token = oldToken;
                }
                String newToken = request.getParameter("token");
                if (StringUtils.isNotBlank(newToken)) {
                    token = newToken;
                }
                //token验证

                //判断methodAnnotation的value属性值
                boolean value = methodAnnotation.value();
                if (value) {
                    //必须登录,如果token无效返回false，跳转login-

                   // return false;
                    //  System.out.println("拦截，必须登录");
                }
                //  System.out.println("拦截，但不需要登录");
                //如果登录成
                if (true) {
                    request.setAttribute("memberId", "");
                    request.setAttribute("nickname", "");
                    //保存cookie
                    if (StringUtils.isNotBlank(token)) {
                        CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
                    }
                    return true;
                }
            }
           // System.out.println("没注解不需要拦截");
        }
      //  System.out.println("访问前端资源，不需要拦截");
        return true;
    }
}
