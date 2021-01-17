package com.kgc.kmall.kmallpassportweb.controller;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.bean.Member;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.util.HttpclientUtil;
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

    @LoginRequired(value = false)
    @RequestMapping("index")
    public String index(String ReturnUrl, Model model) {
        model.addAttribute("ReturnUrl", ReturnUrl);
        return "index";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(Member member, String username, String password, Model model, HttpServletRequest request) {
        String token = "";
        //验证用户名和密码
        Member umsmember = memberService.login(member);
        if (umsmember != null) {
            //制作token
            Map<String, Object> map = new HashMap<>();
            map.put("memberId", umsmember.getId());
            map.put("nickname", umsmember.getNickname());
            String ip = request.getRemoteAddr();
            // 从request中获取ip
            String encode = JwtUtil.encode("kmall077", map, ip);
            if (StringUtils.isNotBlank(ip) || ip.equals("0:0:0:0:0:0:0:1")) {
                ip = "127.0.0.1";
            }
            // 按照设计的算法对参数进行加密后，生成token
            token = JwtUtil.encode("kmall077", map, ip);
            // 将token存入redis一份
            memberService.addUserToken(token, umsmember.getId());
        } else {
            token = "fail";
            //验证不成功
        }
        return token;
    }

    @RequestMapping("verify")
    @ResponseBody
    public Map<String, Object> verify(String token, String currentIp, HttpServletRequest request) {
        // 通过jwt校验token真假
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> decode = JwtUtil.decode(token, "kmall077", currentIp);

        if (decode != null) {
            map.put("status", "success");
            Object memberId = decode.get("memberId");
            map.put("memberId", memberId);
            map.put("nickname", decode.get("nickname"));
        } else {
            map.put("status", "fail");
        }

        return map;
    }

    @RequestMapping("/vlogin")
    public String vlogin(String code, HttpServletRequest request) {
        //code接收授权码

        //根据授权码获取access_token
        String s3 = "https://api.weibo.com/oauth2/access_token";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("client_id", "2595464645");
        paramMap.put("client_secret", "73ac347f92981c2978a04b948f26a719");
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("redirect_uri", "http://passport.kmall.com:8086/vlogin");
        paramMap.put("code", code);// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        System.out.println("code号"+code);
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);
        Map<String, String> access_map = JSON.parseObject(access_token_json, Map.class);
        String access_token = access_map.get("access_token");
        String uid = access_map.get("uid");
        //根据access_token获取用户信息
        // 4 用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + uid;
        String user_json = HttpclientUtil.doGet(s4);
        Map<String, String> user_map = JSON.parseObject(user_json, Map.class);
        System.out.println(user_map);
        // 将用户信息保存数据库，用户类型设置为微博用户
        Member umsMember = new Member();
        umsMember.setSourceType(2);
        //umsMember.setAccessToken(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid(Long.parseLong((String) user_map.get("idstr")));
        umsMember.setCity((String) user_map.get("location"));
        umsMember.setNickname((String) user_map.get("screen_name"));
        Integer g = 0;
        String gender = user_map.get("gender");
        //男==m 女==f
        if (gender.equals("m")) {
            g = 1;
        }
        umsMember.setGender(g);

//        Member umsCheck = new Member();
//        umsCheck.setSourceUid(umsMember.getSourceUid());
        Member umsMemberCheck = memberService.checkOauthUser(umsMember.getSourceUid());

        if (umsMemberCheck == null) {
            memberService.addOauthUser(umsMember);
        } else {
            umsMember = umsMemberCheck;
        }

        // 用jwt制作token
        String token = "";
        Long memberId = umsMember.getId();
        String nickname = umsMember.getNickname();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("memberId", memberId);
        userMap.put("nickname", nickname);

        // 如果使用了nginx，则需要如此获取客户端ip
        //            String ip = request.getHeader("x-forwarded-for");
        //如果没有使用nginx
        String ip = request.getRemoteAddr();// 从request中获取ip
        if (StringUtils.isBlank(ip) || ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }

        // 按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("kmall077", userMap, ip);

        // 将token存入redis一份
        memberService.addUserToken(token, memberId);
        return "redirect:http://search.kmall.com:8084/index.html?token=" + token;
    }

}
