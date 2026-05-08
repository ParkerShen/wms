package com.starlink.wms.modules.wms.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WmsInvLocResp {
    private Long id; private Long whId; private String whCode;
    private String locCode; private String zoneCode;
    private Long skuId; private String skuCode; private Long custId; private String custCode;
    private String lotNo;
    private Integer qty; private Integer totalQty; private Integer badQty;
    private Integer holdQty; private Integer allocQty; private Integer pickingQty;
    private LocalDateTime lastUpdateTime; private LocalDateTime createTime;
}
