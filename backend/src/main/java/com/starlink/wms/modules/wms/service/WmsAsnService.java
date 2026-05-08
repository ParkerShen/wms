package com.starlink.wms.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.wms.dto.*;

public interface WmsAsnService {
    IPage<WmsAsnResp> listPage(WmsAsnPageReq req);
    WmsAsnResp getById(Long id);
    void create(WmsAsnCreateReq req);
    void update(WmsAsnCreateReq req);
    void deleteById(Long id);
    void submit(Long id);
}
