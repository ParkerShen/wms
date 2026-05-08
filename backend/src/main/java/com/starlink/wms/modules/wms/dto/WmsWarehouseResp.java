package com.starlink.wms.modules.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WmsWarehouseResp {
    private Long id;
    private String whCode;
    private String whName;
    private String whType;
    private String countryCode;
    private String currencyCode;
    private String timeZone;
    private String weightUnit;
    private String lengthUnit;
    private String status;
    private String remark;

    private String shippingName;
    private String shippingTel;
    private String shippingEmail;
    private String consigneeName;
    private String consigneeTel;
    private String consigneeEmail;
    private String postalCode;
    private String province;
    private String city;
    private String address1;
    private String contact;
    private String email;
    private String telNo;

    private String bizType;
    private String serviceFunction;
    private BigDecimal storageArea;
    private BigDecimal storageVolume;
    private Integer throughput;
    private Integer handleCapacity;
    private Integer validDay;
    private String storageType;
    private String coopPartner;
    private String wmsSystem;

    private LocalDateTime createTime;
    private LocalDateTime modifyTime;
}
