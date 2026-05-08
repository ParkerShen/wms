package com.starlink.wms.modules.wms.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BmsCustomerResp {
    private Long id;
    private String custCode;
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
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
}
