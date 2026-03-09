package com.diskmanager.controller;

import com.diskmanager.common.Result;
import com.diskmanager.entity.Tag;
import com.diskmanager.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tag")
@Api(tags = "标签管理")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @GetMapping("/list")
    @ApiOperation("获取所有标签")
    public Result<List<Tag>> getAllTags() {
        return Result.success(tagService.getAllTags());
    }
    
    @GetMapping("/file/{fileId}")
    @ApiOperation("获取文件的标签")
    public Result<List<Tag>> getTagsByFileId(@PathVariable Long fileId) {
        return Result.success(tagService.getTagsByFileId(fileId));
    }
    
    @PostMapping
    @ApiOperation("添加标签")
    public Result<Boolean> addTag(@RequestBody Tag tag) {
        return Result.success(tagService.addTag(tag));
    }
    
    @PutMapping
    @ApiOperation("更新标签")
    public Result<Boolean> updateTag(@RequestBody Tag tag) {
        return Result.success(tagService.updateTag(tag));
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除标签")
    public Result<Boolean> deleteTag(@PathVariable Long id) {
        return Result.success(tagService.deleteTag(id));
    }
}
