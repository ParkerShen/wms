package com.starlink.wms.modules.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class BmsProductCreateReq {
    private Long id;
    @NotBlank(message = "产品代码不能为空")
    private String productCode;
    @NotBlank(message = "产品名称不能为空")
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
}
