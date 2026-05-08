package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("wms_asn")
public class WmsAsn {
    @TableId(type = IdType.AUTO) private Long id;
    private String asnNo; private Long custId; private String custCode;
    private Long whId; private String whCode; private String asnType;
    private String status; private String custReferenceNo;
    private Integer totalSkuQty; private Integer totalPkgQty;
    private String receiptMode; private String remark;
    private Long creator; @TableField(fill = FieldFill.INSERT) private LocalDateTime createTime;
    private Long modifier; @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime modifyTime;
    @TableLogic private Integer deleted;
}
