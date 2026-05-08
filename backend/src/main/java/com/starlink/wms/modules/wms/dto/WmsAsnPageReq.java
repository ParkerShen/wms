package com.starlink.wms.modules.wms.dto;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class WmsAsnPageReq {
    private int page = 1; private int pageSize = 20;
    private String asnNo; private String custCode; private String whCode;
    private String asnType; private String status;
}
