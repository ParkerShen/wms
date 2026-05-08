package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("wms_so")
public class WmsSo {
    @TableId(type = IdType.AUTO) private Long id;
    private String soNo; private Long custId; private String custCode;
    private Long whId; private String whCode; private String status;
    private String custReferenceNo; private Long productId; private String productCode;
    private String shippingType;
    private String consigneeName; private String consigneePhone; private String consigneeAddress;
    private String consigneeCity; private String consigneeState; private String consigneeZip; private String consigneeCountry;
    private Integer totalSkuQty; private Integer totalPkgQty; private String remark;
    private Long creator; @TableField(fill = FieldFill.INSERT) private LocalDateTime createTime;
    private Long modifier; @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime modifyTime;
    @TableLogic private Integer deleted;
}
