package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.User;
import com.pinyougou.service.SmsService;
import com.pinyougou.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference(timeout = 10000)
    private UserService userService;


    /**
     * 用户注册
     */
    @PostMapping("/save")
    public boolean save(@RequestBody User user, String smsCode) {
        try {
            boolean ok = userService.checkSmsCode(user.getPhone(),smsCode);
            if (ok) {
                userService.save(user);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 发送注册验证码
     */
    @GetMapping("/sendCode")
    public boolean sendCode(String phone) {
        try {
            if (StringUtils.isNoneBlank(phone)) {
                return userService.sendCode(phone);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * 获取用户登录名
     */
    @GetMapping("/showName")
    public Map<String,String> showName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,String> data = new HashMap<>();
        data.put("loginName",name);
        return data;
    }
}
