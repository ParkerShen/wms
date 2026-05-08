package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("wms_inv_trans")
public class WmsInvTrans {
    @TableId(type = IdType.AUTO) private Long id;
    private String billType; private String billNo; private String operationType;
    private Long whId; private String whCode; private String skuCode; private String custCode;
    private String locCode; private String lotNo;
    private Integer transQty; private Integer beforeQty; private Integer afterQty;
    private String remark; private Long creator; private LocalDateTime createTime;
}
