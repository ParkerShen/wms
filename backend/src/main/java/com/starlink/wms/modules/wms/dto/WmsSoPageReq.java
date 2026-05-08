package com.starlink.wms.modules.wms.dto;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class WmsSoPageReq {
    private int page = 1; private int pageSize = 20;
    private String soNo; private String custCode; private String whCode;
    private String status;
}
