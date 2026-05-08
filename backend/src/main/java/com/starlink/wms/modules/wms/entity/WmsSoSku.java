package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

@Data @TableName("wms_so_sku")
public class WmsSoSku {
    @TableId(type = IdType.AUTO) private Long id;
    private Long soId; private String soNo;
    private Long skuId; private String skuCode; private String custSkuCode;
    private String skuName; private Integer qty; private Integer pickedQty;
    private String remark;
}
