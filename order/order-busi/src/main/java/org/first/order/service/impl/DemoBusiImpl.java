package org.first.order.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.first.order.service.IDemoBusi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DemoBusiImpl implements IDemoBusi {

    @Override
    public String testQuery() {
        return "成功调用testQuery";
    }
}
