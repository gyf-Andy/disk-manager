package com.diskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diskmanager.dto.FileSearchDTO;
import com.diskmanager.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    
    IPage<FileInfo> searchFiles(Page<FileInfo> page, @Param("params") FileSearchDTO params);
    
    List<FileInfo> selectFilesByTagId(@Param("tagId") Long tagId);
    
    void updateCategoryNull(@Param("categoryId") Long categoryId);
}
