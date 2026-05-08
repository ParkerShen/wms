package com.starlink.wms.modules.wms.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BmsProductResp {
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
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
}
