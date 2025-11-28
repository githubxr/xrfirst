package org.first.user.service.impl;

import org.first.order.api.OrderApi;
import org.first.user.service.IUserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImp implements IUserService {

    @Lazy
    @Resource
    private OrderApi orderApi;

    @Override
    public String callOrderApi(String parm) {
        String apiRes = orderApi.hello();
        String msg = "Feign result：" + apiRes;
        return msg;
    }
}
