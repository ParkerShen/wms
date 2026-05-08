package com.starlink.wms.modules.wms.dto;

import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotEmpty;
import lombok.Getter; import lombok.Setter;
import java.util.List;

@Getter @Setter
public class WmsAsnCreateReq {
    private Long id;
    @NotBlank private String asnNo;
    private Long custId; private String custCode;
    private Long whId; private String whCode;
    private String asnType; private String status;
    private String custReferenceNo; private String receiptMode; private String remark;
    private Integer totalSkuQty; private Integer totalPkgQty;
    @NotEmpty(message = "至少需要一个入库商品") private List<AsnSkuItem> skuList;

    @Getter @Setter
    public static class AsnSkuItem {
        private Long skuId; private String skuCode; private String custSkuCode;
        private String skuName; private Integer expectedQty;
    }
}
