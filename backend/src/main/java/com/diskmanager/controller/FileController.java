package com.diskmanager.controller;

import com.diskmanager.common.PageResult;
import com.diskmanager.common.Result;
import com.diskmanager.dto.FileSearchDTO;
import com.diskmanager.dto.FileUpdateDTO;
import com.diskmanager.entity.FileInfo;
import com.diskmanager.entity.Tag;
import com.diskmanager.service.FileInfoService;
import com.diskmanager.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/file")
@Api(tags = "文件管理")
public class FileController {
    
    @Autowired
    private FileInfoService fileInfoService;
    
    @Autowired
    private TagService tagService;
    
    @PostMapping("/search")
    @ApiOperation("搜索文件")
    public Result<PageResult<FileInfo>> searchFiles(@RequestBody FileSearchDTO params) {
        return Result.success(fileInfoService.searchFiles(params));
    }
    
    @GetMapping("/{id}")
    @ApiOperation("获取文件详情")
    public Result<FileInfo> getFileDetail(@PathVariable Long id) {
        FileInfo fileInfo = fileInfoService.getFileDetail(id);
        if (fileInfo != null) {
            List<Tag> tags = tagService.getTagsByFileId(id);
        }
        return Result.success(fileInfo);
    }
    
    @PutMapping
    @ApiOperation("更新文件信息")
    public Result<Boolean> updateFile(@RequestBody FileUpdateDTO dto) {
        return Result.success(fileInfoService.updateFileInfo(dto));
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除文件")
    public Result<Boolean> deleteFile(@PathVariable Long id) {
        return Result.success(fileInfoService.deleteFile(id));
    }
    
    @DeleteMapping("/batch")
    @ApiOperation("批量删除文件")
    public Result<Boolean> deleteFiles(@RequestBody List<Long> ids) {
        return Result.success(fileInfoService.deleteFiles(ids));
    }
    
    @PostMapping("/tag")
    @ApiOperation("为文件添加标签")
    public Result<Boolean> addTagsToFile(@RequestParam Long fileId, @RequestParam List<Long> tagIds) {
        return Result.success(fileInfoService.addTagsToFile(fileId, tagIds));
    }
    
    @DeleteMapping("/tag")
    @ApiOperation("移除文件标签")
    public Result<Boolean> removeTagFromFile(@RequestParam Long fileId, @RequestParam Long tagId) {
        return Result.success(fileInfoService.removeTagFromFile(fileId, tagId));
    }
    
    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "description", required = false) String description) {
        return Result.success("上传成功", fileInfoService.uploadFile(file, categoryId, description));
    }
    
    @GetMapping("/download/{id}")
    @ApiOperation("下载文件")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
        fileInfoService.downloadFile(id, response);
    }
    
    @GetMapping("/preview/{id}")
    @ApiOperation("预览文件")
    public void previewFile(@PathVariable Long id, HttpServletResponse response) {
        fileInfoService.previewFile(id, response);
    }
    
    @GetMapping("/tag/{tagId}")
    @ApiOperation("根据标签获取文件列表")
    public Result<List<FileInfo>> getFilesByTag(@PathVariable Long tagId) {
        return Result.success(fileInfoService.getFilesByTagId(tagId));
    }
}
