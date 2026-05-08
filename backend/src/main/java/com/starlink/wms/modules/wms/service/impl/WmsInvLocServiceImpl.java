package com.starlink.wms.modules.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starlink.wms.modules.wms.dto.*;
import com.starlink.wms.modules.wms.entity.WmsInvLoc;
import com.starlink.wms.modules.wms.mapper.WmsInvLocMapper;
import com.starlink.wms.modules.wms.service.WmsInvLocService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WmsInvLocServiceImpl implements WmsInvLocService {
    private final WmsInvLocMapper invLocMapper;
    public WmsInvLocServiceImpl(WmsInvLocMapper invLocMapper) { this.invLocMapper = invLocMapper; }

    @Override
    public IPage<WmsInvLocResp> listPage(WmsInvLocPageReq req) {
        return invLocMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                new LambdaQueryWrapper<WmsInvLoc>()
                        .eq(StringUtils.hasText(req.getWhCode()), WmsInvLoc::getWhCode, req.getWhCode())
                        .like(StringUtils.hasText(req.getLocCode()), WmsInvLoc::getLocCode, req.getLocCode())
                        .like(StringUtils.hasText(req.getSkuCode()), WmsInvLoc::getSkuCode, req.getSkuCode())
                        .eq(StringUtils.hasText(req.getCustCode()), WmsInvLoc::getCustCode, req.getCustCode())
                        .orderByDesc(WmsInvLoc::getTotalQty))
                .convert(e -> {
                    WmsInvLocResp r = new WmsInvLocResp();
                    r.setId(e.getId()); r.setWhId(e.getWhId()); r.setWhCode(e.getWhCode());
                    r.setLocCode(e.getLocCode()); r.setZoneCode(e.getZoneCode());
                    r.setSkuId(e.getSkuId()); r.setSkuCode(e.getSkuCode());
                    r.setCustId(e.getCustId()); r.setCustCode(e.getCustCode()); r.setLotNo(e.getLotNo());
                    r.setQty(e.getQty()); r.setTotalQty(e.getTotalQty()); r.setBadQty(e.getBadQty());
                    r.setHoldQty(e.getHoldQty()); r.setAllocQty(e.getAllocQty()); r.setPickingQty(e.getPickingQty());
                    r.setLastUpdateTime(e.getLastUpdateTime()); r.setCreateTime(e.getCreateTime());
                    return r;
                });
    }
}
