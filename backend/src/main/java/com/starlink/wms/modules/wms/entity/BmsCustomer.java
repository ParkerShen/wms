package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bms_customer")
public class BmsCustomer {
    @TableId(type = IdType.AUTO)
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
    private Integer showFlag;
    private Long creator;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private Long modifier;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyTime;
    @TableLogic
    private Integer deleted;
}
