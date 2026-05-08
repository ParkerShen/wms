package com.starlink.wms.modules.wms.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WmsSkuResp {
    private Long id;
    private Long custId;
    private String custCode;
    private String skuCode;
    private String customerSkuCode;
    private String barCode;
    private String hsCode;
    private String skuType;
    private String skuNameZh;
    private String skuNameEn;
    private String skuNameFr;
    private BigDecimal declaredAmount;
    private BigDecimal declaredWeight;
    private BigDecimal declaredLength;
    private BigDecimal declaredWidth;
    private BigDecimal declaredHeight;
    private BigDecimal declaredVolume;
    private Long classifyId;
    private String brand;
    private String originCountry;
    private String snType;
    private String lotType;
    private Integer magneticFlag;
    private Integer dangerFlag;
    private Integer chargedFlag;
    private Integer liquidFlag;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
}
