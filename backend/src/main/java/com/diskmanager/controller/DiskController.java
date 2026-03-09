package com.diskmanager.controller;

import com.diskmanager.common.Result;
import com.diskmanager.entity.DiskInfo;
import com.diskmanager.service.DiskInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/disk")
@Api(tags = "磁盘管理")
public class DiskController {
    
    @Autowired
    private DiskInfoService diskInfoService;
    
    @GetMapping("/list")
    @ApiOperation("获取系统磁盘列表")
    public Result<List<DiskInfo>> getDiskList() {
        return Result.success(diskInfoService.getSystemDisks());
    }
    
    @GetMapping("/{id}")
    @ApiOperation("获取磁盘详情")
    public Result<DiskInfo> getDiskInfo(@PathVariable Long id) {
        return Result.success(diskInfoService.getDiskInfo(id));
    }
    
    @PostMapping("/refresh/{id}")
    @ApiOperation("刷新磁盘信息")
    public Result<Boolean> refreshDisk(@PathVariable Long id) {
        return Result.success(diskInfoService.refreshDiskInfo(id));
    }
    
    @PostMapping("/scan/{id}")
    @ApiOperation("扫描磁盘文件")
    public Result<Boolean> scanDisk(@PathVariable Long id) {
        return Result.success(diskInfoService.scanDisk(id));
    }
}
