package com.diskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diskmanager.entity.FileTypeConfig;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FileTypeConfigMapper extends BaseMapper<FileTypeConfig> {
    
    List<String> selectEnabledExtensions();
}
