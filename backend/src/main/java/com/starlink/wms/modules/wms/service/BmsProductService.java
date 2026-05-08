package com.starlink.wms.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.wms.dto.*;

public interface BmsProductService {
    IPage<BmsProductResp> listPage(BmsProductPageReq req);
    BmsProductResp getById(Long id);
    void create(BmsProductCreateReq req);
    void update(BmsProductCreateReq req);
    void deleteById(Long id);
}
