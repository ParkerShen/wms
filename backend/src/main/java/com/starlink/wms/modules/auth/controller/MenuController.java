package com.starlink.wms.modules.auth.controller;

import com.starlink.wms.common.Result;
import com.starlink.wms.modules.auth.dto.*;
import com.starlink.wms.modules.auth.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /** 获取菜单树 */
    @GetMapping("/tree")
    public Result<List<MenuResp>> tree() {
        return Result.success(menuService.getTree());
    }

    @GetMapping("/{id}")
    public Result<MenuResp> get(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody MenuCreateReq req) {
        menuService.create(req);
        return Result.success("新增成功");
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody MenuCreateReq req) {
        menuService.update(req);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteById(id);
        return Result.success("删除成功");
    }

    /** 获取所有菜单 ID */
    @GetMapping("/ids")
    public Result<List<Long>> allIds() {
        return Result.success(menuService.getAllMenuIds());
    }

    /** 获取当前用户有权限的菜单树 */
    @GetMapping("/user-tree")
    public Result<List<MenuResp>> userTree(@RequestAttribute Long userId) {
        return Result.success(menuService.getUserMenuTree(userId));
    }
}
