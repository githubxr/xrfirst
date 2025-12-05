package org.first.product.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品
 * */
@Data
@TableName(value = "xr_goods")
public class Goods {

    //@TableId(type = IdType.AUTO)数据库自增
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String goodsCode;
    private String title;
    private BigDecimal price;
    private String categoryType;
    private String createTime;
    private String updateTime;
    private int status;

}
