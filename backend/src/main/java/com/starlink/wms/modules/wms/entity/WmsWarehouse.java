package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wms_warehouse")
public class WmsWarehouse {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String whCode;
    private String whName;
    private String whType;
    private String countryCode;
    private String currencyCode;
    private String timeZone;
    private String weightUnit;
    private String lengthUnit;
    private String status;
    private String remark;

    private String shippingName;
    private String shippingTel;
    private String shippingEmail;
    private String consigneeName;
    private String consigneeTel;
    private String consigneeEmail;
    private String postalCode;
    private String province;
    private String city;
    private String address1;
    private String contact;
    private String email;
    private String telNo;

    private String bizType;
    private String serviceFunction;
    private BigDecimal storageArea;
    private BigDecimal storageVolume;
    private Integer throughput;
    private Integer handleCapacity;
    private Integer validDay;
    private String storageType;
    private String coopPartner;
    private String wmsSystem;

    private Long creator;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private Long modifier;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyTime;
    @TableLogic
    private Integer deleted;
}
