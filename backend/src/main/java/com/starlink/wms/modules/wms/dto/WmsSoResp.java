package com.starlink.wms.modules.wms.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WmsSoResp {
    private Long id; private String soNo; private Long custId; private String custCode;
    private Long whId; private String whCode; private String status;
    private String custReferenceNo; private Long productId; private String productCode;
    private String shippingType;
    private String consigneeName; private String consigneePhone; private String consigneeAddress;
    private String consigneeCity; private String consigneeState; private String consigneeZip; private String consigneeCountry;
    private Integer totalSkuQty; private Integer totalPkgQty; private String remark;
    private LocalDateTime createTime; private LocalDateTime modifyTime;
    private List<SoSkuResp> skuList;

    @Data
    public static class SoSkuResp {
        private Long id; private Long soId;
        private Long skuId; private String skuCode; private String custSkuCode;
        private String skuName; private Integer qty; private Integer pickedQty;
    }
}
