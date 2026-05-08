package com.starlink.wms.modules.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BmsCustomerCreateReq {
    private Long id;
    @NotBlank(message = "客户代码不能为空")
    private String custCode;
    @NotBlank(message = "客户名称不能为空")
    private String custName;
    private String contact;
    private String email;
    private String telNo;
    private String address;
    private String apiAccount;
    private String apiPassword;
    private String status;
    private String remark;
    private Integer autoAuditSku;
    private Integer autoSkuCode;
    private Integer skuClassifyRequired;
    private String servicePlatform;
    private String skuWhSyncType;
    private Integer expressAdvanceFlag;
    private Integer supportCustProvider;
    private Integer matchLabelFlag;
    private Integer newFeeStructure;
    private Integer orderSource;
    private String shippingCode;
    private String shippingAddress;
}
