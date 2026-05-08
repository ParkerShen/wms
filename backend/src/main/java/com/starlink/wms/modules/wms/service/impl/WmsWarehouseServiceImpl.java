package com.starlink.wms.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.entity.WmsWarehouse;
import com.starlink.wms.modules.wms.mapper.WmsWarehouseMapper;
import com.starlink.wms.modules.wms.service.WmsWarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WmsWarehouseServiceImpl implements WmsWarehouseService {

    private static final Logger log = LoggerFactory.getLogger(WmsWarehouseServiceImpl.class);

    private final WmsWarehouseMapper warehouseMapper;

    public WmsWarehouseServiceImpl(WmsWarehouseMapper warehouseMapper) {
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    public IPage<WmsWarehouseResp> listPage(WmsWarehousePageReq req) {
        LambdaQueryWrapper<WmsWarehouse> wrapper = new LambdaQueryWrapper<WmsWarehouse>()
                .like(StringUtils.hasText(req.getWhCode()), WmsWarehouse::getWhCode, req.getWhCode())
                .like(StringUtils.hasText(req.getWhName()), WmsWarehouse::getWhName, req.getWhName())
                .eq(StringUtils.hasText(req.getWhType()), WmsWarehouse::getWhType, req.getWhType())
                .eq(StringUtils.hasText(req.getCountryCode()), WmsWarehouse::getCountryCode, req.getCountryCode())
                .eq(StringUtils.hasText(req.getStatus()), WmsWarehouse::getStatus, req.getStatus())
                .orderByDesc(WmsWarehouse::getId);

        IPage<WmsWarehouse> page = warehouseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()), wrapper);

        return page.convert(this::toResp);
    }

    @Override
    public WmsWarehouseResp getById(Long id) {
        WmsWarehouse entity = warehouseMapper.selectById(id);
        if (entity == null) throw new BusinessException("仓库不存在");
        return toResp(entity);
    }

    @Override
    @Transactional
    public void create(WmsWarehouseCreateReq req) {
        // 检查仓库代码唯一性
        Long count = warehouseMapper.selectCount(
                new LambdaQueryWrapper<WmsWarehouse>().eq(WmsWarehouse::getWhCode, req.getWhCode()));
        if (count > 0) throw new BusinessException("仓库代码已存在");

        WmsWarehouse entity = new WmsWarehouse();
        merge(entity, req);
        warehouseMapper.insert(entity);
        log.info("新增仓库: {}", req.getWhCode());
    }

    @Override
    @Transactional
    public void update(WmsWarehouseCreateReq req) {
        WmsWarehouse entity = warehouseMapper.selectById(req.getId());
        if (entity == null) throw new BusinessException("仓库不存在");

        // 如果修改了仓库代码，检查唯一性
        if (!entity.getWhCode().equals(req.getWhCode())) {
            Long count = warehouseMapper.selectCount(
                    new LambdaQueryWrapper<WmsWarehouse>()
                            .eq(WmsWarehouse::getWhCode, req.getWhCode())
                            .ne(WmsWarehouse::getId, req.getId()));
            if (count > 0) throw new BusinessException("仓库代码已存在");
        }

        merge(entity, req);
        warehouseMapper.updateById(entity);
        log.info("更新仓库: id={}, code={}", req.getId(), req.getWhCode());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        WmsWarehouse entity = warehouseMapper.selectById(id);
        if (entity == null) throw new BusinessException("仓库不存在");
        warehouseMapper.deleteById(id);
        log.info("删除仓库: id={}, code={}", id, entity.getWhCode());
    }

    private void merge(WmsWarehouse entity, WmsWarehouseCreateReq req) {
        entity.setWhCode(req.getWhCode());
        entity.setWhName(req.getWhName());
        entity.setWhType(req.getWhType() != null ? req.getWhType() : "WH");
        entity.setCountryCode(req.getCountryCode());
        entity.setCurrencyCode(req.getCurrencyCode());
        entity.setTimeZone(req.getTimeZone());
        entity.setWeightUnit(req.getWeightUnit());
        entity.setLengthUnit(req.getLengthUnit());
        entity.setStatus(req.getStatus() != null ? req.getStatus() : "VALID");
        entity.setRemark(req.getRemark());
        entity.setShippingName(req.getShippingName());
        entity.setShippingTel(req.getShippingTel());
        entity.setShippingEmail(req.getShippingEmail());
        entity.setConsigneeName(req.getConsigneeName());
        entity.setConsigneeTel(req.getConsigneeTel());
        entity.setConsigneeEmail(req.getConsigneeEmail());
        entity.setPostalCode(req.getPostalCode());
        entity.setProvince(req.getProvince());
        entity.setCity(req.getCity());
        entity.setAddress1(req.getAddress1());
        entity.setContact(req.getContact());
        entity.setEmail(req.getEmail());
        entity.setTelNo(req.getTelNo());
        entity.setBizType(req.getBizType());
        entity.setServiceFunction(req.getServiceFunction());
        entity.setStorageArea(req.getStorageArea());
        entity.setStorageVolume(req.getStorageVolume());
        entity.setThroughput(req.getThroughput());
        entity.setHandleCapacity(req.getHandleCapacity());
        entity.setValidDay(req.getValidDay());
        entity.setStorageType(req.getStorageType());
        entity.setCoopPartner(req.getCoopPartner());
        entity.setWmsSystem(req.getWmsSystem());
    }

    @Override
    public List<WmsWarehouseSelectResp> listForSelect() {
        return warehouseMapper.selectList(
                new LambdaQueryWrapper<WmsWarehouse>()
                        .eq(WmsWarehouse::getStatus, "VALID")
                        .orderByAsc(WmsWarehouse::getWhCode))
                .stream().map(e -> {
                    WmsWarehouseSelectResp r = new WmsWarehouseSelectResp();
                    r.setId(e.getId()); r.setWhCode(e.getWhCode()); r.setWhName(e.getWhName());
                    return r;
                }).collect(Collectors.toList());
    }

    private WmsWarehouseResp toResp(WmsWarehouse entity) {
        WmsWarehouseResp resp = new WmsWarehouseResp();
        resp.setId(entity.getId());
        resp.setWhCode(entity.getWhCode());
        resp.setWhName(entity.getWhName());
        resp.setWhType(entity.getWhType());
        resp.setCountryCode(entity.getCountryCode());
        resp.setCurrencyCode(entity.getCurrencyCode());
        resp.setTimeZone(entity.getTimeZone());
        resp.setWeightUnit(entity.getWeightUnit());
        resp.setLengthUnit(entity.getLengthUnit());
        resp.setStatus(entity.getStatus());
        resp.setRemark(entity.getRemark());
        resp.setShippingName(entity.getShippingName());
        resp.setShippingTel(entity.getShippingTel());
        resp.setShippingEmail(entity.getShippingEmail());
        resp.setConsigneeName(entity.getConsigneeName());
        resp.setConsigneeTel(entity.getConsigneeTel());
        resp.setConsigneeEmail(entity.getConsigneeEmail());
        resp.setPostalCode(entity.getPostalCode());
        resp.setProvince(entity.getProvince());
        resp.setCity(entity.getCity());
        resp.setAddress1(entity.getAddress1());
        resp.setContact(entity.getContact());
        resp.setEmail(entity.getEmail());
        resp.setTelNo(entity.getTelNo());
        resp.setBizType(entity.getBizType());
        resp.setServiceFunction(entity.getServiceFunction());
        resp.setStorageArea(entity.getStorageArea());
        resp.setStorageVolume(entity.getStorageVolume());
        resp.setThroughput(entity.getThroughput());
        resp.setHandleCapacity(entity.getHandleCapacity());
        resp.setValidDay(entity.getValidDay());
        resp.setStorageType(entity.getStorageType());
        resp.setCoopPartner(entity.getCoopPartner());
        resp.setWmsSystem(entity.getWmsSystem());
        resp.setCreateTime(entity.getCreateTime());
        resp.setModifyTime(entity.getModifyTime());
        return resp;
    }
}
