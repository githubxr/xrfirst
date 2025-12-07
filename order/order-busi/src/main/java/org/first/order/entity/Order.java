package org.first.order.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单
 * */
@Data
@TableName("xr_order")
public class Order {
    //@TableId(type = IdType.AUTO)数据库自增
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String orderCode;
    private String userCode;

    private BigDecimal totalAmount;
    private int status;//订单状态
    private String createTime;
    private String updateTime;
    private String payTime;
    private int deleteFlag;
}
