package com.starlink.wms.modules.wms.dto;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class WmsInvLocPageReq {
    private int page = 1; private int pageSize = 20;
    private String whCode; private String locCode; private String skuCode;
    private String custCode; private String zoneCode;
}
