package com.starlink.wms.modules.wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("wms_inv_loc")
public class WmsInvLoc {
    @TableId(type = IdType.AUTO) private Long id;
    private Long whId; private String whCode; private String locCode; private String zoneCode;
    private Long skuId; private String skuCode; private Long custId; private String custCode;
    private String lotNo;
    private Integer qty; private Integer totalQty; private Integer badQty;
    private Integer holdQty; private Integer allocQty; private Integer pickingQty;
    private LocalDateTime lastUpdateTime; private LocalDateTime createTime;
}
