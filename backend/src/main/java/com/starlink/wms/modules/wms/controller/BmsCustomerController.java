package com.starlink.wms.modules.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.common.Result;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.service.BmsCustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wms/customer")
public class BmsCustomerController {
    private final BmsCustomerService customerService;
    public BmsCustomerController(BmsCustomerService customerService) { this.customerService = customerService; }

    @GetMapping("/page")
    public Result<IPage<BmsCustomerResp>> page(BmsCustomerPageReq req) {
        return Result.success(customerService.listPage(req));
    }
    @GetMapping("/{id}")
    public Result<BmsCustomerResp> get(@PathVariable Long id) { return Result.success(customerService.getById(id)); }
    @PostMapping
    public Result<Void> create(@Valid @RequestBody BmsCustomerCreateReq req) {
        customerService.create(req); return Result.success("新增成功");
    }
    @PutMapping
    public Result<Void> update(@Valid @RequestBody BmsCustomerCreateReq req) {
        customerService.update(req); return Result.success("修改成功");
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.deleteById(id); return Result.success("删除成功");
    }
}
