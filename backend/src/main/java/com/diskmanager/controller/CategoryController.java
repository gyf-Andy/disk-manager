package com.diskmanager.controller;

import com.diskmanager.common.Result;
import com.diskmanager.entity.FileCategory;
import com.diskmanager.service.FileCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@Api(tags = "分类管理")
public class CategoryController {
    
    @Autowired
    private FileCategoryService fileCategoryService;
    
    @GetMapping("/tree")
    @ApiOperation("获取分类树")
    public Result<List<FileCategory>> getCategoryTree() {
        return Result.success(fileCategoryService.getCategoryTree());
    }
    
    @GetMapping("/list")
    @ApiOperation("获取所有分类")
    public Result<List<FileCategory>> getAllCategories() {
        return Result.success(fileCategoryService.getAllCategories());
    }
    
    @PostMapping
    @ApiOperation("添加分类")
    public Result<Boolean> addCategory(@RequestBody FileCategory category) {
        return Result.success(fileCategoryService.addCategory(category));
    }
    
    @PutMapping
    @ApiOperation("更新分类")
    public Result<Boolean> updateCategory(@RequestBody FileCategory category) {
        return Result.success(fileCategoryService.updateCategory(category));
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除分类")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        return Result.success(fileCategoryService.deleteCategory(id));
    }
}
