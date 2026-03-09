package com.diskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diskmanager.entity.DiskInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiskInfoMapper extends BaseMapper<DiskInfo> {
}
