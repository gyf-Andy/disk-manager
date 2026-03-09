package com.diskmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diskmanager.entity.FileCategory;
import com.diskmanager.mapper.FileCategoryMapper;
import com.diskmanager.mapper.FileInfoMapper;
import com.diskmanager.service.FileCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileCategoryServiceImpl extends ServiceImpl<FileCategoryMapper, FileCategory> implements FileCategoryService {
    
    
    @Autowired
    private FileInfoMapper fileInfoMapper;
    
    @Override
    public List<FileCategory> getCategoryTree() {
        List<FileCategory> allCategories = list(new LambdaQueryWrapper<FileCategory>()
                .orderByAsc(FileCategory::getSortOrder)
                .orderByAsc(FileCategory::getId));
        
        Map<Long, FileCategory> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(FileCategory::getId, c -> c));
        
        List<FileCategory> rootCategories = new ArrayList<>();
        for (FileCategory category : allCategories) {
            if (category.getParentId() == null || category.getParentId() == 0) {
                rootCategories.add(category);
            } else {
                FileCategory parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(category);
                }
            }
        }
        
        return rootCategories;
    }
    
    @Override
    public List<FileCategory> getAllCategories() {
        return list(new LambdaQueryWrapper<FileCategory>()
                .orderByAsc(FileCategory::getSortOrder)
                .orderByAsc(FileCategory::getId));
    }
    
    @Override
    public boolean addCategory(FileCategory category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        return save(category);
    }
    
    @Override
    public boolean updateCategory(FileCategory category) {
        return updateById(category);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        fileInfoMapper.updateCategoryNull(id);
        return removeById(id);
    }
}
