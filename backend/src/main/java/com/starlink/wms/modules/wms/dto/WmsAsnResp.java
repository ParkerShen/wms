package com.starlink.wms.modules.wms.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WmsAsnResp {
    private Long id; private String asnNo; private Long custId; private String custCode;
    private Long whId; private String whCode; private String asnType; private String status;
    private String custReferenceNo; private Integer totalSkuQty; private Integer totalPkgQty;
    private String receiptMode; private String remark;
    private LocalDateTime createTime; private LocalDateTime modifyTime;
    private List<AsnSkuResp> skuList;

    @Data
    public static class AsnSkuResp {
        private Long id; private Long asnId;
        private Long skuId; private String skuCode; private String custSkuCode;
        private String skuName; private Integer expectedQty; private Integer actualQty;
        private Integer badQty;
    }
}
