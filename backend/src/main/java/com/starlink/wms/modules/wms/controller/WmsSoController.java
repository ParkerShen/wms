package com.starlink.wms.modules.wms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starlink.wms.common.Result;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.service.WmsSoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/wms/so")
public class WmsSoController {
    private final WmsSoService soService;
    public WmsSoController(WmsSoService soService) { this.soService = soService; }

    @GetMapping("/page") public Result<IPage<WmsSoResp>> page(WmsSoPageReq req) { return Result.success(soService.listPage(req)); }
    @GetMapping("/{id}") public Result<WmsSoResp> get(@PathVariable Long id) { return Result.success(soService.getById(id)); }
    @PostMapping public Result<Void> create(@Valid @RequestBody WmsSoCreateReq req) { soService.create(req); return Result.success("新增成功"); }
    @PutMapping public Result<Void> update(@Valid @RequestBody WmsSoCreateReq req) { soService.update(req); return Result.success("修改成功"); }
    @DeleteMapping("/{id}") public Result<Void> delete(@PathVariable Long id) { soService.deleteById(id); return Result.success("删除成功"); }
    @PutMapping("/{id}/submit") public Result<Void> submit(@PathVariable Long id) { soService.submit(id); return Result.success("提交成功"); }
}
