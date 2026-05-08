package com.starlink.wms.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.wms.dto.*;

public interface WmsSoService {
    IPage<WmsSoResp> listPage(WmsSoPageReq req);
    WmsSoResp getById(Long id);
    void create(WmsSoCreateReq req);
    void update(WmsSoCreateReq req);
    void deleteById(Long id);
    void submit(Long id);
}
