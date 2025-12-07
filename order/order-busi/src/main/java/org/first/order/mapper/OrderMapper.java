package org.first.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.first.order.entity.Order;


public interface OrderMapper extends BaseMapper<Order> {

//    //创建草稿订单（status=0）
//    void insertDraft(@Param("orderCode")String orderCode,@Param("userCode") String userCode);


}

