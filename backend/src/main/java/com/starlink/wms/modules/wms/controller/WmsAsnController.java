package com.starlink.wms.modules.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.common.Result;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.service.WmsAsnService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/wms/asn")
public class WmsAsnController {
    private final WmsAsnService asnService;
    public WmsAsnController(WmsAsnService asnService) { this.asnService = asnService; }

    @GetMapping("/page") public Result<IPage<WmsAsnResp>> page(WmsAsnPageReq req) { return Result.success(asnService.listPage(req)); }
    @GetMapping("/{id}") public Result<WmsAsnResp> get(@PathVariable Long id) { return Result.success(asnService.getById(id)); }
    @PostMapping public Result<Void> create(@Valid @RequestBody WmsAsnCreateReq req) { asnService.create(req); return Result.success("新增成功"); }
    @PutMapping public Result<Void> update(@Valid @RequestBody WmsAsnCreateReq req) { asnService.update(req); return Result.success("修改成功"); }
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) { asnService.deleteById(id); return Result.success("删除成功"); }
    @PutMapping("/{id}/submit") public Result<Void> submit(@PathVariable Long id) { asnService.submit(id); return Result.success("提交成功"); }
}
