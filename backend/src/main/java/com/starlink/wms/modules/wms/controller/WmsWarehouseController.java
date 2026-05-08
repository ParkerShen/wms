package com.starlink.wms.modules.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.common.Result;
import com.starlink.wms.modules.wms.dto.*;
import java.util.List;
import com.starlink.wms.modules.wms.service.WmsWarehouseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wms/warehouse")
public class WmsWarehouseController {

    private final WmsWarehouseService warehouseService;

    public WmsWarehouseController(WmsWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/page")
    public Result<IPage<WmsWarehouseResp>> page(WmsWarehousePageReq req) {
        return Result.success(warehouseService.listPage(req));
    }

    @GetMapping("/{id}")
    public Result<WmsWarehouseResp> get(@PathVariable Long id) {
        return Result.success(warehouseService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody WmsWarehouseCreateReq req) {
        warehouseService.create(req);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody WmsWarehouseCreateReq req) {
        warehouseService.update(req);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        warehouseService.deleteById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/list-select")
    public Result<List<WmsWarehouseSelectResp>> listForSelect() {
        return Result.success(warehouseService.listForSelect());
    }
}
