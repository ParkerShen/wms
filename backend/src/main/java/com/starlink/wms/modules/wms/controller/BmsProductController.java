package com.starlink.wms.modules.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.common.Result;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.service.BmsProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wms/product")
public class BmsProductController {
    private final BmsProductService productService;
    public BmsProductController(BmsProductService productService) { this.productService = productService; }

    @GetMapping("/page")
    public Result<IPage<BmsProductResp>> page(BmsProductPageReq req) { return Result.success(productService.listPage(req)); }
    @GetMapping("/{id}")
    public Result<BmsProductResp> get(@PathVariable Long id) { return Result.success(productService.getById(id)); }
    @PostMapping
    public Result<Void> create(@Valid @RequestBody BmsProductCreateReq req) {
        productService.create(req); return Result.success("新增成功");
    }
    @PutMapping
    public Result<Void> update(@Valid @RequestBody BmsProductCreateReq req) {
        productService.update(req); return Result.success("修改成功");
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteById(id); return Result.success("删除成功");
    }
}
