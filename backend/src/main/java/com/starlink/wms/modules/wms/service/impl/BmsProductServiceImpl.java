package com.starlink.wms.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.entity.BmsProduct;
import com.starlink.wms.modules.wms.mapper.BmsProductMapper;
import com.starlink.wms.modules.wms.service.BmsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BmsProductServiceImpl implements BmsProductService {
    private static final Logger log = LoggerFactory.getLogger(BmsProductServiceImpl.class);
    private final BmsProductMapper productMapper;
    public BmsProductServiceImpl(BmsProductMapper productMapper) { this.productMapper = productMapper; }

    @Override
    public IPage<BmsProductResp> listPage(BmsProductPageReq req) {
        return productMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                new LambdaQueryWrapper<BmsProduct>()
                        .like(StringUtils.hasText(req.getProductCode()), BmsProduct::getProductCode, req.getProductCode())
                        .like(StringUtils.hasText(req.getProductName()), BmsProduct::getProductName, req.getProductName())
                        .eq(StringUtils.hasText(req.getBizType()), BmsProduct::getBizType, req.getBizType())
                        .eq(StringUtils.hasText(req.getCountryCode()), BmsProduct::getCountryCode, req.getCountryCode())
                        .eq(StringUtils.hasText(req.getStatus()), BmsProduct::getStatus, req.getStatus())
                        .orderByDesc(BmsProduct::getId))
                .convert(this::toResp);
    }

    @Override
    public BmsProductResp getById(Long id) {
        BmsProduct entity = productMapper.selectById(id);
        if (entity == null) throw new BusinessException("产品不存在");
        return toResp(entity);
    }

    @Override
    @Transactional
    public void create(BmsProductCreateReq req) {
        if (productMapper.selectCount(new LambdaQueryWrapper<BmsProduct>().eq(BmsProduct::getProductCode, req.getProductCode())) > 0)
            throw new BusinessException("产品代码已存在");
        BmsProduct entity = new BmsProduct(); merge(entity, req);
        productMapper.insert(entity);
        log.info("新增产品: {}", req.getProductCode());
    }

    @Override
    @Transactional
    public void update(BmsProductCreateReq req) {
        BmsProduct entity = productMapper.selectById(req.getId());
        if (entity == null) throw new BusinessException("产品不存在");
        if (!entity.getProductCode().equals(req.getProductCode()) &&
            productMapper.selectCount(new LambdaQueryWrapper<BmsProduct>().eq(BmsProduct::getProductCode, req.getProductCode()).ne(BmsProduct::getId, req.getId())) > 0)
            throw new BusinessException("产品代码已存在");
        merge(entity, req); productMapper.updateById(entity);
        log.info("更新产品: id={}", req.getId());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (productMapper.selectById(id) == null) throw new BusinessException("产品不存在");
        productMapper.deleteById(id);
    }

    private void merge(BmsProduct e, BmsProductCreateReq r) {
        e.setProductCode(r.getProductCode()); e.setProductName(r.getProductName());
        e.setProductVersion(r.getProductVersion() != null ? r.getProductVersion() : 1);
        e.setBizType(r.getBizType()); e.setProductType(r.getProductType() != null ? r.getProductType() : "SINGLE_PIECE");
        e.setCountryCode(r.getCountryCode()); e.setCurrencyCode(r.getCurrencyCode());
        e.setChannelRules(r.getChannelRules()); e.setBillingRules(r.getBillingRules());
        e.setBillableWeightType(r.getBillableWeightType()); e.setDimWeightCoefficient(r.getDimWeightCoefficient());
        e.setFreightCoefficient(r.getFreightCoefficient()); e.setWeightUnit(r.getWeightUnit());
        e.setLengthUnit(r.getLengthUnit()); e.setOnlineFlag(r.getOnlineFlag());
        e.setStatus(r.getStatus() != null ? r.getStatus() : "VALID"); e.setRemark(r.getRemark());
        e.setOdaFlag(r.getOdaFlag()); e.setPodFlag(r.getPodFlag()); e.setInsuranceFlag(r.getInsuranceFlag());
        e.setDangerousFlag(r.getDangerousFlag()); e.setReturnLabelFlag(r.getReturnLabelFlag());
        e.setEndProviderCode(r.getEndProviderCode()); e.setZoneTemplateId(r.getZoneTemplateId());
        e.setWeightTemplateId(r.getWeightTemplateId());
    }

    private BmsProductResp toResp(BmsProduct e) {
        BmsProductResp r = new BmsProductResp();
        r.setId(e.getId()); r.setProductCode(e.getProductCode()); r.setProductName(e.getProductName());
        r.setProductVersion(e.getProductVersion()); r.setBizType(e.getBizType());
        r.setProductType(e.getProductType()); r.setCountryCode(e.getCountryCode());
        r.setCurrencyCode(e.getCurrencyCode()); r.setChannelRules(e.getChannelRules());
        r.setBillingRules(e.getBillingRules()); r.setBillableWeightType(e.getBillableWeightType());
        r.setDimWeightCoefficient(e.getDimWeightCoefficient()); r.setFreightCoefficient(e.getFreightCoefficient());
        r.setWeightUnit(e.getWeightUnit()); r.setLengthUnit(e.getLengthUnit());
        r.setOnlineFlag(e.getOnlineFlag()); r.setStatus(e.getStatus()); r.setRemark(e.getRemark());
        r.setOdaFlag(e.getOdaFlag()); r.setPodFlag(e.getPodFlag()); r.setInsuranceFlag(e.getInsuranceFlag());
        r.setDangerousFlag(e.getDangerousFlag()); r.setReturnLabelFlag(e.getReturnLabelFlag());
        r.setEndProviderCode(e.getEndProviderCode()); r.setZoneTemplateId(e.getZoneTemplateId());
        r.setWeightTemplateId(e.getWeightTemplateId());
        r.setCreateTime(e.getCreateTime()); r.setModifyTime(e.getModifyTime());
        return r;
    }
}
