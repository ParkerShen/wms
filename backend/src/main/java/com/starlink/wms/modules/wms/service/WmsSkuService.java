package com.starlink.wms.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.wms.dto.*;

public interface WmsSkuService {
    IPage<WmsSkuResp> listPage(WmsSkuPageReq req);
    WmsSkuResp getById(Long id);
    void create(WmsSkuCreateReq req);
    void update(WmsSkuCreateReq req);
    void deleteById(Long id);
}
