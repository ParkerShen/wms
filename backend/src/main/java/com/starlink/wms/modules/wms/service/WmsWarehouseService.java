package com.starlink.wms.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.wms.dto.WmsWarehouseCreateReq;
import com.starlink.wms.modules.wms.dto.WmsWarehousePageReq;
import com.starlink.wms.modules.wms.dto.WmsWarehouseResp;
import com.starlink.wms.modules.wms.dto.WmsWarehouseSelectResp;

import java.util.List;

public interface WmsWarehouseService {
    IPage<WmsWarehouseResp> listPage(WmsWarehousePageReq req);
    WmsWarehouseResp getById(Long id);
    void create(WmsWarehouseCreateReq req);
    void update(WmsWarehouseCreateReq req);
    void deleteById(Long id);
    List<WmsWarehouseSelectResp> listForSelect();
}
