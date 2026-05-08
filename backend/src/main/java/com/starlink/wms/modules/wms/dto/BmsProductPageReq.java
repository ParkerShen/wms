package com.starlink.wms.modules.wms.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class BmsProductPageReq {
    private int page = 1;
    private int pageSize = 20;
    private String productCode;
    private String productName;
    private String bizType;
    private String countryCode;
    private String status;
}
