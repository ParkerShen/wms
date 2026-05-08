package com.starlink.wms.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.entity.WmsSku;
import com.starlink.wms.modules.wms.mapper.WmsSkuMapper;
import com.starlink.wms.modules.wms.service.WmsSkuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class WmsSkuServiceImpl implements WmsSkuService {
    private static final Logger log = LoggerFactory.getLogger(WmsSkuServiceImpl.class);
    private final WmsSkuMapper skuMapper;
    public WmsSkuServiceImpl(WmsSkuMapper skuMapper) { this.skuMapper = skuMapper; }

    @Override
    public IPage<WmsSkuResp> listPage(WmsSkuPageReq req) {
        return skuMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                new LambdaQueryWrapper<WmsSku>()
                        .like(StringUtils.hasText(req.getSkuCode()), WmsSku::getSkuCode, req.getSkuCode())
                        .like(StringUtils.hasText(req.getSkuName()), WmsSku::getSkuNameZh, req.getSkuName())
                        .eq(StringUtils.hasText(req.getCustCode()), WmsSku::getCustCode, req.getCustCode())
                        .eq(req.getCustId() != null, WmsSku::getCustId, req.getCustId())
                        .eq(StringUtils.hasText(req.getStatus()), WmsSku::getStatus, req.getStatus())
                        .orderByDesc(WmsSku::getId))
                .convert(this::toResp);
    }

    @Override
    public WmsSkuResp getById(Long id) {
        WmsSku entity = skuMapper.selectById(id);
        if (entity == null) throw new BusinessException("SKU不存在");
        return toResp(entity);
    }

    @Override
    @Transactional
    public void create(WmsSkuCreateReq req) {
        if (skuMapper.selectCount(new LambdaQueryWrapper<WmsSku>().eq(WmsSku::getSkuCode, req.getSkuCode())) > 0)
            throw new BusinessException("SKU编码已存在");
        WmsSku entity = new WmsSku(); merge(entity, req);
        skuMapper.insert(entity);
        log.info("新增SKU: {}", req.getSkuCode());
    }

    @Override
    @Transactional
    public void update(WmsSkuCreateReq req) {
        WmsSku entity = skuMapper.selectById(req.getId());
        if (entity == null) throw new BusinessException("SKU不存在");
        if (!entity.getSkuCode().equals(req.getSkuCode()) &&
            skuMapper.selectCount(new LambdaQueryWrapper<WmsSku>().eq(WmsSku::getSkuCode, req.getSkuCode()).ne(WmsSku::getId, req.getId())) > 0)
            throw new BusinessException("SKU编码已存在");
        merge(entity, req); skuMapper.updateById(entity);
        log.info("更新SKU: id={}", req.getId());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (skuMapper.selectById(id) == null) throw new BusinessException("SKU不存在");
        skuMapper.deleteById(id);
    }

    private void merge(WmsSku e, WmsSkuCreateReq r) {
        e.setCustId(r.getCustId()); e.setCustCode(r.getCustCode());
        e.setSkuCode(r.getSkuCode()); e.setCustomerSkuCode(r.getCustomerSkuCode());
        e.setBarCode(r.getBarCode()); e.setHsCode(r.getHsCode());
        e.setSkuType(r.getSkuType() != null ? r.getSkuType() : "SKU");
        e.setSkuNameZh(r.getSkuNameZh()); e.setSkuNameEn(r.getSkuNameEn()); e.setSkuNameFr(r.getSkuNameFr());
        e.setDeclaredAmount(r.getDeclaredAmount()); e.setDeclaredWeight(r.getDeclaredWeight());
        e.setDeclaredLength(r.getDeclaredLength()); e.setDeclaredWidth(r.getDeclaredWidth());
        e.setDeclaredHeight(r.getDeclaredHeight()); e.setDeclaredVolume(r.getDeclaredVolume());
        e.setClassifyId(r.getClassifyId()); e.setBrand(r.getBrand()); e.setOriginCountry(r.getOriginCountry());
        e.setSnType(r.getSnType()); e.setLotType(r.getLotType());
        e.setMagneticFlag(r.getMagneticFlag()); e.setDangerFlag(r.getDangerFlag());
        e.setChargedFlag(r.getChargedFlag()); e.setLiquidFlag(r.getLiquidFlag());
        e.setStatus(r.getStatus() != null ? r.getStatus() : "VALID"); e.setRemark(r.getRemark());
    }

    private WmsSkuResp toResp(WmsSku e) {
        WmsSkuResp r = new WmsSkuResp();
        r.setId(e.getId()); r.setCustId(e.getCustId()); r.setCustCode(e.getCustCode());
        r.setSkuCode(e.getSkuCode()); r.setCustomerSkuCode(e.getCustomerSkuCode());
        r.setBarCode(e.getBarCode()); r.setHsCode(e.getHsCode());
        r.setSkuType(e.getSkuType());
        r.setSkuNameZh(e.getSkuNameZh()); r.setSkuNameEn(e.getSkuNameEn()); r.setSkuNameFr(e.getSkuNameFr());
        r.setDeclaredAmount(e.getDeclaredAmount()); r.setDeclaredWeight(e.getDeclaredWeight());
        r.setDeclaredLength(e.getDeclaredLength()); r.setDeclaredWidth(e.getDeclaredWidth());
        r.setDeclaredHeight(e.getDeclaredHeight()); r.setDeclaredVolume(e.getDeclaredVolume());
        r.setClassifyId(e.getClassifyId()); r.setBrand(e.getBrand()); r.setOriginCountry(e.getOriginCountry());
        r.setSnType(e.getSnType()); r.setLotType(e.getLotType());
        r.setMagneticFlag(e.getMagneticFlag()); r.setDangerFlag(e.getDangerFlag());
        r.setChargedFlag(e.getChargedFlag()); r.setLiquidFlag(e.getLiquidFlag());
        r.setStatus(e.getStatus()); r.setRemark(e.getRemark());
        r.setCreateTime(e.getCreateTime()); r.setModifyTime(e.getModifyTime());
        return r;
    }
}
