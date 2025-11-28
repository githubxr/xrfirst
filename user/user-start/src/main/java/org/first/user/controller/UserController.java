package org.first.user.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.first.user.bean.User;
import org.first.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private IUserService iUserService;

    static final String testKey = "testKey";

    //Feign链测试
    @GetMapping("test")
    public String test(){
        String res = "controller.call api:" + iUserService.callOrderApi("str");
        return res;
    }

    //for 题目G
    @GetMapping("initTestRedisData")
    public String initTestRedisData(){
        User user = new User("0001","101","张三", "#L#R#","114514");
        //JSONObject res = JSONObject.from(user);
        if(! redisTemplate.hasKey(testKey)){
            redisTemplate.opsForValue().set(testKey, user);
        }
        return "测试初始化成功！";
    }

    //for 题目G
    @GetMapping("/getTestRedisData")
    public JSONObject getTestRedisData(){
        Object obj = redisTemplate.opsForValue().get(testKey);
        JSONObject res = null;
        if(obj==null) {
            res = new JSONObject();
            res.put("先初始化再测试","先初始化再测试");
        } else {
            User user = (User) obj;
            res = JSONObject.from(user);
        }
        return res;
    }

    //for 题目E
    @ResponseBody
    @PostMapping("userInfo")
    public JSONObject userInfo(String placeHolderParameter) {
        //假装通过鉴权获取 登录用户信息
        User user = new User("0001","101","张三", "#L#R#","114514");
        JSONObject res = JSONObject.from(user);
        return res;
    }
}
