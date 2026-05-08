package com.starlink.wms.modules.wms.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WmsInvTransResp {
    private Long id; private String billType; private String billNo; private String operationType;
    private Long whId; private String whCode; private String skuCode; private String custCode;
    private String locCode; private String lotNo;
    private Integer transQty; private Integer beforeQty; private Integer afterQty;
    private String remark; private LocalDateTime createTime;
}
