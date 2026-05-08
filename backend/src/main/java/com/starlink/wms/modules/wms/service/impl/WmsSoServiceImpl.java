package com.starlink.wms.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.dto.WmsSoResp.SoSkuResp;
import com.starlink.wms.modules.wms.entity.*;
import com.starlink.wms.modules.wms.mapper.*;
import com.starlink.wms.modules.wms.service.WmsSoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.stream.Collectors;

@Service
public class WmsSoServiceImpl implements WmsSoService {
    private static final Logger log = LoggerFactory.getLogger(WmsSoServiceImpl.class);
    private final WmsSoMapper soMapper; private final WmsSoSkuMapper soSkuMapper;
    public WmsSoServiceImpl(WmsSoMapper soMapper, WmsSoSkuMapper soSkuMapper) { this.soMapper = soMapper; this.soSkuMapper = soSkuMapper; }

    @Override
    public IPage<WmsSoResp> listPage(WmsSoPageReq req) {
        return soMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                new LambdaQueryWrapper<WmsSo>()
                        .like(StringUtils.hasText(req.getSoNo()), WmsSo::getSoNo, req.getSoNo())
                        .eq(StringUtils.hasText(req.getCustCode()), WmsSo::getCustCode, req.getCustCode())
                        .eq(StringUtils.hasText(req.getStatus()), WmsSo::getStatus, req.getStatus())
                        .orderByDesc(WmsSo::getId))
                .convert(this::toHeaderResp);
    }

    @Override
    public WmsSoResp getById(Long id) {
        WmsSo h = soMapper.selectById(id);
        if (h == null) throw new BusinessException("出库单不存在");
        WmsSoResp r = toHeaderResp(h);
        r.setSkuList(soSkuMapper.selectList(new LambdaQueryWrapper<WmsSoSku>().eq(WmsSoSku::getSoId, id))
                .stream().map(this::toSkuResp).collect(Collectors.toList()));
        return r;
    }

    @Override @Transactional
    public void create(WmsSoCreateReq req) {
        if (soMapper.selectCount(new LambdaQueryWrapper<WmsSo>().eq(WmsSo::getSoNo, req.getSoNo())) > 0)
            throw new BusinessException("出库单号已存在");
        WmsSo h = new WmsSo(); mergeSo(h, req); soMapper.insert(h);
        for (WmsSoCreateReq.SoSkuItem s : req.getSkuList()) {
            WmsSoSku sku = new WmsSoSku(); sku.setSoId(h.getId()); sku.setSoNo(h.getSoNo());
            sku.setSkuId(s.getSkuId()); sku.setSkuCode(s.getSkuCode()); sku.setCustSkuCode(s.getCustSkuCode());
            sku.setSkuName(s.getSkuName()); sku.setQty(s.getQty());
            soSkuMapper.insert(sku);
        }
        log.info("新增出库单: {}", req.getSoNo());
    }

    @Override @Transactional
    public void update(WmsSoCreateReq req) {
        WmsSo h = soMapper.selectById(req.getId());
        if (h == null) throw new BusinessException("出库单不存在");
        mergeSo(h, req); soMapper.updateById(h);
        soSkuMapper.delete(new LambdaQueryWrapper<WmsSoSku>().eq(WmsSoSku::getSoId, h.getId()));
        for (WmsSoCreateReq.SoSkuItem s : req.getSkuList()) {
            WmsSoSku sku = new WmsSoSku(); sku.setSoId(h.getId()); sku.setSoNo(h.getSoNo());
            sku.setSkuId(s.getSkuId()); sku.setSkuCode(s.getSkuCode()); sku.setCustSkuCode(s.getCustSkuCode());
            sku.setSkuName(s.getSkuName()); sku.setQty(s.getQty());
            soSkuMapper.insert(sku);
        }
        log.info("更新出库单: id={}", req.getId());
    }

    @Override @Transactional
    public void deleteById(Long id) {
        WmsSo h = soMapper.selectById(id);
        if (h == null) throw new BusinessException("出库单不存在");
        soSkuMapper.delete(new LambdaQueryWrapper<WmsSoSku>().eq(WmsSoSku::getSoId, id));
        soMapper.deleteById(id);
    }

    @Override @Transactional
    public void submit(Long id) {
        WmsSo h = soMapper.selectById(id);
        if (h == null) throw new BusinessException("出库单不存在");
        h.setStatus("SUBMITTED"); soMapper.updateById(h);
        log.info("提交出库单: {}", h.getSoNo());
    }

    private void mergeSo(WmsSo e, WmsSoCreateReq r) {
        e.setSoNo(r.getSoNo()); e.setCustId(r.getCustId()); e.setCustCode(r.getCustCode());
        e.setWhId(r.getWhId()); e.setWhCode(r.getWhCode());
        e.setStatus(r.getStatus() != null ? r.getStatus() : "DRAFT");
        e.setCustReferenceNo(r.getCustReferenceNo()); e.setProductId(r.getProductId()); e.setProductCode(r.getProductCode());
        e.setShippingType(r.getShippingType() != null ? r.getShippingType() : "CHANNEL");
        e.setConsigneeName(r.getConsigneeName()); e.setConsigneePhone(r.getConsigneePhone());
        e.setConsigneeAddress(r.getConsigneeAddress()); e.setConsigneeCity(r.getConsigneeCity());
        e.setConsigneeState(r.getConsigneeState()); e.setConsigneeZip(r.getConsigneeZip());
        e.setConsigneeCountry(r.getConsigneeCountry());
        e.setTotalSkuQty(r.getTotalSkuQty()); e.setRemark(r.getRemark());
    }

    private WmsSoResp toHeaderResp(WmsSo e) {
        WmsSoResp r = new WmsSoResp();
        r.setId(e.getId()); r.setSoNo(e.getSoNo()); r.setCustId(e.getCustId()); r.setCustCode(e.getCustCode());
        r.setWhId(e.getWhId()); r.setWhCode(e.getWhCode()); r.setStatus(e.getStatus());
        r.setCustReferenceNo(e.getCustReferenceNo()); r.setProductId(e.getProductId()); r.setProductCode(e.getProductCode());
        r.setShippingType(e.getShippingType());
        r.setConsigneeName(e.getConsigneeName()); r.setConsigneePhone(e.getConsigneePhone());
        r.setConsigneeAddress(e.getConsigneeAddress()); r.setConsigneeCity(e.getConsigneeCity());
        r.setConsigneeState(e.getConsigneeState()); r.setConsigneeZip(e.getConsigneeZip());
        r.setConsigneeCountry(e.getConsigneeCountry());
        r.setTotalSkuQty(e.getTotalSkuQty()); r.setTotalPkgQty(e.getTotalPkgQty()); r.setRemark(e.getRemark());
        r.setCreateTime(e.getCreateTime()); r.setModifyTime(e.getModifyTime());
        return r;
    }

    private SoSkuResp toSkuResp(WmsSoSku e) {
        SoSkuResp r = new SoSkuResp();
        r.setId(e.getId()); r.setSoId(e.getSoId()); r.setSkuId(e.getSkuId());
        r.setSkuCode(e.getSkuCode()); r.setCustSkuCode(e.getCustSkuCode());
        r.setSkuName(e.getSkuName()); r.setQty(e.getQty()); r.setPickedQty(e.getPickedQty());
        return r;
    }
}
