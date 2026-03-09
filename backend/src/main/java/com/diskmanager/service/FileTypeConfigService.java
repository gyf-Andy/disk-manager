package com.diskmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diskmanager.entity.FileTypeConfig;
import java.util.List;

public interface FileTypeConfigService extends IService<FileTypeConfig> {
    
    List<FileTypeConfig> getAllConfigs();
    
    List<String> getEnabledExtensions();
    
    boolean addConfig(FileTypeConfig config);
    
    boolean updateConfig(FileTypeConfig config);
    
    boolean deleteConfig(Long id);
    
    boolean toggleEnabled(Long id, Integer enabled);
    
    boolean batchUpdateEnabled(List<Long> ids, Integer enabled);
}
