package com.starlink.wms.modules.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WmsWarehouseCreateReq {
    private Long id;

    @NotBlank(message = "仓库代码不能为空")
    private String whCode;

    @NotBlank(message = "仓库名称不能为空")
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
}
