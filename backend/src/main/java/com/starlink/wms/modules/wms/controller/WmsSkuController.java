package com.starlink.wms.modules.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.common.Result;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.service.WmsSkuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wms/sku")
public class WmsSkuController {
    private final WmsSkuService skuService;
    public WmsSkuController(WmsSkuService skuService) { this.skuService = skuService; }

    @GetMapping("/page")
    public Result<IPage<WmsSkuResp>> page(WmsSkuPageReq req) { return Result.success(skuService.listPage(req)); }
    @GetMapping("/{id}")
    public Result<WmsSkuResp> get(@PathVariable Long id) { return Result.success(skuService.getById(id)); }
    @PostMapping
    public Result<Void> create(@Valid @RequestBody WmsSkuCreateReq req) {
        skuService.create(req); return Result.success("新增成功");
    }
    @PutMapping
    public Result<Void> update(@Valid @RequestBody WmsSkuCreateReq req) {
        skuService.update(req); return Result.success("修改成功");
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        skuService.deleteById(id); return Result.success("删除成功");
    }
}
