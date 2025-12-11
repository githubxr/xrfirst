package org.first.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.first.product.entity.Goods;
import org.first.product.entity.Inventory;


public interface GoodsMapper extends BaseMapper<Goods> {


}
