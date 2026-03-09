package com.diskmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diskmanager.entity.Tag;
import java.util.List;

public interface TagService extends IService<Tag> {
    
    List<Tag> getAllTags();
    
    List<Tag> getTagsByFileId(Long fileId);
    
    boolean addTag(Tag tag);
    
    boolean updateTag(Tag tag);
    
    boolean deleteTag(Long id);
}
