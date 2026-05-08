package com.starlink.wms.modules.wms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsWarehousePageReq {
    private int page = 1;
    private int pageSize = 20;

    private String whCode;
    private String whName;
    private String whType;
    private String countryCode;
    private String status;
}
