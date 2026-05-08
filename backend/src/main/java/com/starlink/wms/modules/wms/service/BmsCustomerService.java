package com.starlink.wms.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.wms.dto.*;

public interface BmsCustomerService {
    IPage<BmsCustomerResp> listPage(BmsCustomerPageReq req);
    BmsCustomerResp getById(Long id);
    void create(BmsCustomerCreateReq req);
    void update(BmsCustomerCreateReq req);
    void deleteById(Long id);
}
