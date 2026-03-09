package com.diskmanager.controller;

import com.diskmanager.common.Result;
import com.diskmanager.entity.FileTypeConfig;
import com.diskmanager.service.FileTypeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file-type-config")
@Api(tags = "文件类型配置")
public class FileTypeConfigController {
    
    @Autowired
    private FileTypeConfigService fileTypeConfigService;
    
    @GetMapping("/list")
    @ApiOperation("获取所有文件类型配置")
    public Result<List<FileTypeConfig>> getAllConfigs() {
        return Result.success(fileTypeConfigService.getAllConfigs());
    }
    
    @GetMapping("/enabled-extensions")
    @ApiOperation("获取启用的文件扩展名列表")
    public Result<List<String>> getEnabledExtensions() {
        return Result.success(fileTypeConfigService.getEnabledExtensions());
    }
    
    @PostMapping
    @ApiOperation("添加文件类型配置")
    public Result<Boolean> addConfig(@RequestBody FileTypeConfig config) {
        return Result.success(fileTypeConfigService.addConfig(config));
    }
    
    @PutMapping
    @ApiOperation("更新文件类型配置")
    public Result<Boolean> updateConfig(@RequestBody FileTypeConfig config) {
        return Result.success(fileTypeConfigService.updateConfig(config));
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除文件类型配置")
    public Result<Boolean> deleteConfig(@PathVariable Long id) {
        return Result.success(fileTypeConfigService.deleteConfig(id));
    }
    
    @PutMapping("/toggle/{id}")
    @ApiOperation("切换启用状态")
    public Result<Boolean> toggleEnabled(@PathVariable Long id, @RequestParam Integer enabled) {
        return Result.success(fileTypeConfigService.toggleEnabled(id, enabled));
    }
    
    @PutMapping("/batch-toggle")
    @ApiOperation("批量切换启用状态")
    public Result<Boolean> batchToggleEnabled(@RequestBody List<Long> ids, @RequestParam Integer enabled) {
        return Result.success(fileTypeConfigService.batchUpdateEnabled(ids, enabled));
    }
}
