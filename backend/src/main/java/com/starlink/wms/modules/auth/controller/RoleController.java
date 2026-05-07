package com.starlink.wms.modules.auth.controller;

import com.starlink.wms.common.Result;
import com.starlink.wms.modules.auth.dto.*;
import com.starlink.wms.modules.auth.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/page")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<RoleResp>> page(RolePageReq req) {
        return Result.success(roleService.listPage(req));
    }

    @GetMapping("/{id}")
    public Result<RoleResp> get(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody RoleCreateReq req) {
        roleService.create(req);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleCreateReq req) {
        roleService.update(req);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteById(id);
        return Result.success("删除成功");
    }

    /** 分配菜单权限 */
    @PutMapping("/{id}/menu")
    public Result<Void> assignMenu(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenu(id, menuIds);
        return Result.success("分配成功");
    }
}
