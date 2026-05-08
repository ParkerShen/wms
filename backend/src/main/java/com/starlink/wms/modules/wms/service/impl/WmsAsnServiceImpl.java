package com.starlink.wms.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.dto.WmsAsnResp.AsnSkuResp;
import com.starlink.wms.modules.wms.entity.WmsAsn;
import com.starlink.wms.modules.wms.entity.WmsAsnSku;
import com.starlink.wms.modules.wms.mapper.WmsAsnMapper;
import com.starlink.wms.modules.wms.mapper.WmsAsnSkuMapper;
import com.starlink.wms.modules.wms.service.WmsAsnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WmsAsnServiceImpl implements WmsAsnService {
    private static final Logger log = LoggerFactory.getLogger(WmsAsnServiceImpl.class);
    private final WmsAsnMapper asnMapper;
    private final WmsAsnSkuMapper asnSkuMapper;
    public WmsAsnServiceImpl(WmsAsnMapper asnMapper, WmsAsnSkuMapper asnSkuMapper) { this.asnMapper = asnMapper; this.asnSkuMapper = asnSkuMapper; }

    @Override
    public IPage<WmsAsnResp> listPage(WmsAsnPageReq req) {
        return asnMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                new LambdaQueryWrapper<WmsAsn>()
                        .like(StringUtils.hasText(req.getAsnNo()), WmsAsn::getAsnNo, req.getAsnNo())
                        .eq(StringUtils.hasText(req.getCustCode()), WmsAsn::getCustCode, req.getCustCode())
                        .eq(StringUtils.hasText(req.getStatus()), WmsAsn::getStatus, req.getStatus())
                        .orderByDesc(WmsAsn::getId))
                .convert(this::toHeaderResp);
    }

    @Override
    public WmsAsnResp getById(Long id) {
        WmsAsn h = asnMapper.selectById(id);
        if (h == null) throw new BusinessException("入库单不存在");
        WmsAsnResp r = toHeaderResp(h);
        r.setSkuList(asnSkuMapper.selectList(new LambdaQueryWrapper<WmsAsnSku>().eq(WmsAsnSku::getAsnId, id))
                .stream().map(this::toSkuResp).collect(Collectors.toList()));
        return r;
    }

    @Override @Transactional
    public void create(WmsAsnCreateReq req) {
        if (asnMapper.selectCount(new LambdaQueryWrapper<WmsAsn>().eq(WmsAsn::getAsnNo, req.getAsnNo())) > 0)
            throw new BusinessException("入库单号已存在");
        WmsAsn h = new WmsAsn(); mergeAsn(h, req); asnMapper.insert(h);
        for (WmsAsnCreateReq.AsnSkuItem s : req.getSkuList()) {
            WmsAsnSku sku = new WmsAsnSku(); sku.setAsnId(h.getId()); sku.setAsnNo(h.getAsnNo());
            sku.setSkuId(s.getSkuId()); sku.setSkuCode(s.getSkuCode()); sku.setCustSkuCode(s.getCustSkuCode());
            sku.setSkuName(s.getSkuName()); sku.setExpectedQty(s.getExpectedQty()); sku.setActualQty(0);
            asnSkuMapper.insert(sku);
        }
        log.info("新增入库单: {}", req.getAsnNo());
    }

    @Override @Transactional
    public void update(WmsAsnCreateReq req) {
        WmsAsn h = asnMapper.selectById(req.getId());
        if (h == null) throw new BusinessException("入库单不存在");
        mergeAsn(h, req); asnMapper.updateById(h);
        asnSkuMapper.delete(new LambdaQueryWrapper<WmsAsnSku>().eq(WmsAsnSku::getAsnId, h.getId()));
        for (WmsAsnCreateReq.AsnSkuItem s : req.getSkuList()) {
            WmsAsnSku sku = new WmsAsnSku(); sku.setAsnId(h.getId()); sku.setAsnNo(h.getAsnNo());
            sku.setSkuId(s.getSkuId()); sku.setSkuCode(s.getSkuCode()); sku.setCustSkuCode(s.getCustSkuCode());
            sku.setSkuName(s.getSkuName()); sku.setExpectedQty(s.getExpectedQty());
            asnSkuMapper.insert(sku);
        }
        log.info("更新入库单: id={}", req.getId());
    }

    @Override @Transactional
    public void deleteById(Long id) {
        WmsAsn h = asnMapper.selectById(id);
        if (h == null) throw new BusinessException("入库单不存在");
        asnSkuMapper.delete(new LambdaQueryWrapper<WmsAsnSku>().eq(WmsAsnSku::getAsnId, id));
        asnMapper.deleteById(id);
    }

    @Override @Transactional
    public void submit(Long id) {
        WmsAsn h = asnMapper.selectById(id);
        if (h == null) throw new BusinessException("入库单不存在");
        h.setStatus("SUBMITTED"); asnMapper.updateById(h);
        log.info("提交入库单: {}", h.getAsnNo());
    }

    private void mergeAsn(WmsAsn e, WmsAsnCreateReq r) {
        e.setAsnNo(r.getAsnNo()); e.setCustId(r.getCustId()); e.setCustCode(r.getCustCode());
        e.setWhId(r.getWhId()); e.setWhCode(r.getWhCode());
        e.setAsnType(r.getAsnType() != null ? r.getAsnType() : "NORMAL");
        e.setStatus(r.getStatus() != null ? r.getStatus() : "DRAFT");
        e.setCustReferenceNo(r.getCustReferenceNo()); e.setReceiptMode(r.getReceiptMode());
        e.setTotalSkuQty(r.getTotalSkuQty()); e.setTotalPkgQty(r.getTotalPkgQty());
        e.setRemark(r.getRemark());
    }

    private WmsAsnResp toHeaderResp(WmsAsn e) {
        WmsAsnResp r = new WmsAsnResp();
        r.setId(e.getId()); r.setAsnNo(e.getAsnNo()); r.setCustId(e.getCustId()); r.setCustCode(e.getCustCode());
        r.setWhId(e.getWhId()); r.setWhCode(e.getWhCode()); r.setAsnType(e.getAsnType());
        r.setStatus(e.getStatus()); r.setCustReferenceNo(e.getCustReferenceNo());
        r.setTotalSkuQty(e.getTotalSkuQty()); r.setTotalPkgQty(e.getTotalPkgQty());
        r.setReceiptMode(e.getReceiptMode()); r.setRemark(e.getRemark());
        r.setCreateTime(e.getCreateTime()); r.setModifyTime(e.getModifyTime());
        return r;
    }

    private AsnSkuResp toSkuResp(WmsAsnSku e) {
        AsnSkuResp r = new AsnSkuResp();
        r.setId(e.getId()); r.setAsnId(e.getAsnId()); r.setSkuId(e.getSkuId());
        r.setSkuCode(e.getSkuCode()); r.setCustSkuCode(e.getCustSkuCode());
        r.setSkuName(e.getSkuName()); r.setExpectedQty(e.getExpectedQty());
        r.setActualQty(e.getActualQty()); r.setBadQty(e.getBadQty());
        return r;
    }
}
