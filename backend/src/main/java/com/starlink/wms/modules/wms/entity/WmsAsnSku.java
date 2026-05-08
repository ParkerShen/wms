package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

@Data @TableName("wms_asn_sku")
public class WmsAsnSku {
    @TableId(type = IdType.AUTO) private Long id;
    private Long asnId; private String asnNo;
    private Long skuId; private String skuCode; private String custSkuCode;
    private String skuName; private Integer expectedQty; private Integer actualQty;
    private Integer badQty; private String remark;
}
