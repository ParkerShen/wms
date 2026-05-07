package com.starlink.wms.modules.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starlink.wms.modules.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
