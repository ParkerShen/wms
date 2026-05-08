package com.starlink.wms.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.entity.BmsCustomer;
import com.starlink.wms.modules.wms.mapper.BmsCustomerMapper;
import com.starlink.wms.modules.wms.service.BmsCustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BmsCustomerServiceImpl implements BmsCustomerService {
    private static final Logger log = LoggerFactory.getLogger(BmsCustomerServiceImpl.class);
    private final BmsCustomerMapper customerMapper;
    public BmsCustomerServiceImpl(BmsCustomerMapper customerMapper) { this.customerMapper = customerMapper; }

    @Override
    public IPage<BmsCustomerResp> listPage(BmsCustomerPageReq req) {
        return customerMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                new LambdaQueryWrapper<BmsCustomer>()
                        .like(StringUtils.hasText(req.getCustCode()), BmsCustomer::getCustCode, req.getCustCode())
                        .like(StringUtils.hasText(req.getCustName()), BmsCustomer::getCustName, req.getCustName())
                        .eq(StringUtils.hasText(req.getStatus()), BmsCustomer::getStatus, req.getStatus())
                        .eq(StringUtils.hasText(req.getServicePlatform()), BmsCustomer::getServicePlatform, req.getServicePlatform())
                        .orderByDesc(BmsCustomer::getId))
                .convert(this::toResp);
    }

    @Override
    public BmsCustomerResp getById(Long id) {
        BmsCustomer entity = customerMapper.selectById(id);
        if (entity == null) throw new BusinessException("客户不存在");
        return toResp(entity);
    }

    @Override
    @Transactional
    public void create(BmsCustomerCreateReq req) {
        if (customerMapper.selectCount(new LambdaQueryWrapper<BmsCustomer>().eq(BmsCustomer::getCustCode, req.getCustCode())) > 0)
            throw new BusinessException("客户代码已存在");
        BmsCustomer entity = new BmsCustomer(); merge(entity, req);
        customerMapper.insert(entity);
        log.info("新增客户: {}", req.getCustCode());
    }

    @Override
    @Transactional
    public void update(BmsCustomerCreateReq req) {
        BmsCustomer entity = customerMapper.selectById(req.getId());
        if (entity == null) throw new BusinessException("客户不存在");
        if (!entity.getCustCode().equals(req.getCustCode()) &&
            customerMapper.selectCount(new LambdaQueryWrapper<BmsCustomer>().eq(BmsCustomer::getCustCode, req.getCustCode()).ne(BmsCustomer::getId, req.getId())) > 0)
            throw new BusinessException("客户代码已存在");
        merge(entity, req); customerMapper.updateById(entity);
        log.info("更新客户: id={}", req.getId());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (customerMapper.selectById(id) == null) throw new BusinessException("客户不存在");
        customerMapper.deleteById(id);
    }

    private void merge(BmsCustomer e, BmsCustomerCreateReq r) {
        e.setCustCode(r.getCustCode()); e.setCustName(r.getCustName());
        e.setContact(r.getContact()); e.setEmail(r.getEmail()); e.setTelNo(r.getTelNo());
        e.setAddress(r.getAddress()); e.setApiAccount(r.getApiAccount()); e.setApiPassword(r.getApiPassword());
        e.setStatus(r.getStatus() != null ? r.getStatus() : "VALID"); e.setRemark(r.getRemark());
        e.setAutoAuditSku(r.getAutoAuditSku()); e.setAutoSkuCode(r.getAutoSkuCode());
        e.setSkuClassifyRequired(r.getSkuClassifyRequired()); e.setServicePlatform(r.getServicePlatform());
        e.setSkuWhSyncType(r.getSkuWhSyncType()); e.setExpressAdvanceFlag(r.getExpressAdvanceFlag());
        e.setSupportCustProvider(r.getSupportCustProvider()); e.setMatchLabelFlag(r.getMatchLabelFlag());
        e.setNewFeeStructure(r.getNewFeeStructure()); e.setOrderSource(r.getOrderSource());
        e.setShippingCode(r.getShippingCode()); e.setShippingAddress(r.getShippingAddress());
    }

    private BmsCustomerResp toResp(BmsCustomer e) {
        BmsCustomerResp r = new BmsCustomerResp();
        r.setId(e.getId()); r.setCustCode(e.getCustCode()); r.setCustName(e.getCustName());
        r.setContact(e.getContact()); r.setEmail(e.getEmail()); r.setTelNo(e.getTelNo());
        r.setAddress(e.getAddress()); r.setApiAccount(e.getApiAccount()); r.setApiPassword(e.getApiPassword());
        r.setStatus(e.getStatus()); r.setRemark(e.getRemark());
        r.setAutoAuditSku(e.getAutoAuditSku()); r.setAutoSkuCode(e.getAutoSkuCode());
        r.setSkuClassifyRequired(e.getSkuClassifyRequired()); r.setServicePlatform(e.getServicePlatform());
        r.setSkuWhSyncType(e.getSkuWhSyncType()); r.setExpressAdvanceFlag(e.getExpressAdvanceFlag());
        r.setSupportCustProvider(e.getSupportCustProvider()); r.setMatchLabelFlag(e.getMatchLabelFlag());
        r.setNewFeeStructure(e.getNewFeeStructure()); r.setOrderSource(e.getOrderSource());
        r.setShippingCode(e.getShippingCode()); r.setShippingAddress(e.getShippingAddress());
        r.setCreateTime(e.getCreateTime()); r.setModifyTime(e.getModifyTime());
        return r;
    }
}
