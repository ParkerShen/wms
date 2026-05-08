package com.starlink.wms.modules.wms.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class WmsSkuPageReq {
    private int page = 1;
    private int pageSize = 20;
    private String skuCode;
    private String skuName;
    private String custCode;
    private String status;
    private Long custId;
}
