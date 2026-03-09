package com.diskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diskmanager.entity.FileTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FileTagMapper extends BaseMapper<FileTag> {
    
    void deleteByFileId(@Param("fileId") Long fileId);
    
    void deleteByTagId(@Param("tagId") Long tagId);
    
    void batchInsert(@Param("list") List<FileTag> list);
    
    List<Long> selectTagIdsByFileId(@Param("fileId") Long fileId);
}
