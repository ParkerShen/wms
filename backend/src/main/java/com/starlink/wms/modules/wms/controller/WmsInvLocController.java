package com.starlink.wms.modules.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.common.Result;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.service.WmsInvLocService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/wms/inventory")
public class WmsInvLocController {
    private final WmsInvLocService invLocService;
    public WmsInvLocController(WmsInvLocService invLocService) { this.invLocService = invLocService; }
    @GetMapping("/page") public Result<IPage<WmsInvLocResp>> page(WmsInvLocPageReq req) { return Result.success(invLocService.listPage(req)); }
}
