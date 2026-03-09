package com.diskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diskmanager.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    
    List<Tag> selectTagsByFileId(@Param("fileId") Long fileId);
}
