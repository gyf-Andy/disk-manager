package com.diskmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diskmanager.entity.FileCategory;
import java.util.List;

public interface FileCategoryService extends IService<FileCategory> {
    
    List<FileCategory> getCategoryTree();
    
    List<FileCategory> getAllCategories();
    
    boolean addCategory(FileCategory category);
    
    boolean updateCategory(FileCategory category);
    
    boolean deleteCategory(Long id);
}
