package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bms_product")
public class BmsProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String productCode;
    private String productName;
    private Integer productVersion;
    private String bizType;
    private String productType;
    private String countryCode;
    private String currencyCode;
    private String channelRules;
    private String billingRules;
    private String billableWeightType;
    private Integer dimWeightCoefficient;
    private BigDecimal freightCoefficient;
    private String weightUnit;
    private String lengthUnit;
    private String onlineFlag;
    private String status;
    private String remark;
    private Integer odaFlag;
    private Integer podFlag;
    private Integer insuranceFlag;
    private Integer dangerousFlag;
    private Integer returnLabelFlag;
    private String endProviderCode;
    private Long zoneTemplateId;
    private Long weightTemplateId;
    private Long creator;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private Long modifier;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyTime;
    @TableLogic
    private Integer deleted;
}
