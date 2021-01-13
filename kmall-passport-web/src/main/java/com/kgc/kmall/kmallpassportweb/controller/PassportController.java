package com.kgc.kmall.kmallpassportweb.controller;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {
    @Reference
    MemberService memberService;

    @RequestMapping("index")
    public String index(String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "index";
    }
    @RequestMapping("login")
    @ResponseBody
    public String login(Member member, String username, String password, Model model, HttpServletRequest request){
        String token="";
        //验证用户名和密码
        Member umsmember=memberService.login(member);
        if(umsmember!=null) {
            //制作token
            Map<String, Object> map = new HashMap<>();
            map.put("memberId", umsmember.getId());
            map.put("nickname", umsmember.getNickname());
            String ip = request.getRemoteAddr();
            // 从request中获取ip
            String encode = JwtUtil.encode("kmall077", map, ip);
            if (StringUtils.isNotBlank(ip)||ip.equals("0:0:0:0:0:0:0:1")){
                ip="127.0.0.1";
            }
            // 按照设计的算法对参数进行加密后，生成token
            token = JwtUtil.encode("kmall077", map, ip);
            // 将token存入redis一份
            memberService.addUserToken(token,umsmember.getId());
        }else{
            token = "fail";
        //验证不成功
        }
        return token;
    }
}
