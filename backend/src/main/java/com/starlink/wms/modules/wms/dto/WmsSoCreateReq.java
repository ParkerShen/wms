package com.starlink.wms.modules.wms.dto;

import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotEmpty;
import lombok.Getter; import lombok.Setter;
import java.util.List;

@Getter @Setter
public class WmsSoCreateReq {
    private Long id;
    @NotBlank private String soNo;
    private Long custId; private String custCode;
    private Long whId; private String whCode;
    private String status; private String custReferenceNo;
    private Long productId; private String productCode; private String shippingType;
    private String consigneeName; private String consigneePhone; private String consigneeAddress;
    private String consigneeCity; private String consigneeState; private String consigneeZip; private String consigneeCountry;
    private Integer totalSkuQty; private String remark;
    @NotEmpty(message = "至少需要一个出库商品") private List<SoSkuItem> skuList;

    @Getter @Setter
    public static class SoSkuItem {
        private Long skuId; private String skuCode; private String custSkuCode;
        private String skuName; private Integer qty;
    }
}
