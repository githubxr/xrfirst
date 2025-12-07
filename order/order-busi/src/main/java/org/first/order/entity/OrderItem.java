package org.first.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@TableName("xr_order_item")
@Data
public class OrderItem {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String orderCode;
    private String goodsCode;
    private int goodsNum;
    private BigDecimal goodsPriceSnapshot;
    private String createTime;
    private int deleteFlag;

}
