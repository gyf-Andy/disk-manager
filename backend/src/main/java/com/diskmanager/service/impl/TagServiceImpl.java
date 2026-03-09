package com.diskmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diskmanager.entity.Tag;
import com.diskmanager.mapper.FileTagMapper;
import com.diskmanager.mapper.TagMapper;
import com.diskmanager.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private FileTagMapper fileTagMapper;
    
    @Override
    public List<Tag> getAllTags() {
        return list(new LambdaQueryWrapper<Tag>()
                .orderByDesc(Tag::getCreateTime));
    }
    
    @Override
    public List<Tag> getTagsByFileId(Long fileId) {
        return tagMapper.selectTagsByFileId(fileId);
    }
    
    @Override
    public boolean addTag(Tag tag) {
        if (tag.getColor() == null || tag.getColor().isEmpty()) {
            tag.setColor("#1890ff");
        }
        return save(tag);
    }
    
    @Override
    public boolean updateTag(Tag tag) {
        return updateById(tag);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTag(Long id) {
        fileTagMapper.deleteByTagId(id);
        return removeById(id);
    }
}
