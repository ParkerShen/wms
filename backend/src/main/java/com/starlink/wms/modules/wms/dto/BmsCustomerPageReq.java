package com.starlink.wms.modules.wms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BmsCustomerPageReq {
    private int page = 1;
    private int pageSize = 20;
    private String custCode;
    private String custName;
    private String status;
    private String servicePlatform;
}
