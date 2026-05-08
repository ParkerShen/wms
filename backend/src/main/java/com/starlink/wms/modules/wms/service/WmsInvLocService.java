package com.starlink.wms.modules.wms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.modules.wms.dto.*;

public interface WmsInvLocService {
    IPage<WmsInvLocResp> listPage(WmsInvLocPageReq req);
}
