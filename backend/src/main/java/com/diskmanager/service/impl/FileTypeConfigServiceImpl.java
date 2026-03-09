package com.diskmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diskmanager.entity.FileTypeConfig;
import com.diskmanager.mapper.FileTypeConfigMapper;
import com.diskmanager.service.FileTypeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FileTypeConfigServiceImpl extends ServiceImpl<FileTypeConfigMapper, FileTypeConfig> implements FileTypeConfigService {
    
    @Autowired
    private FileTypeConfigMapper fileTypeConfigMapper;
    
    @Override
    public List<FileTypeConfig> getAllConfigs() {
        return list(new LambdaQueryWrapper<FileTypeConfig>()
                .orderByAsc(FileTypeConfig::getSortOrder)
                .orderByAsc(FileTypeConfig::getId));
    }
    
    @Override
    public List<String> getEnabledExtensions() {
        return fileTypeConfigMapper.selectEnabledExtensions();
    }
    
    @Override
    public boolean addConfig(FileTypeConfig config) {
        if (config.getEnabled() == null) {
            config.setEnabled(1);
        }
        if (config.getPreviewAvailable() == null) {
            config.setPreviewAvailable(0);
        }
        if (config.getSortOrder() == null) {
            config.setSortOrder(0);
        }
        return save(config);
    }
    
    @Override
    public boolean updateConfig(FileTypeConfig config) {
        return updateById(config);
    }
    
    @Override
    public boolean deleteConfig(Long id) {
        return removeById(id);
    }
    
    @Override
    public boolean toggleEnabled(Long id, Integer enabled) {
        LambdaUpdateWrapper<FileTypeConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FileTypeConfig::getId, id)
                .set(FileTypeConfig::getEnabled, enabled);
        return update(updateWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateEnabled(List<Long> ids, Integer enabled) {
        LambdaUpdateWrapper<FileTypeConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FileTypeConfig::getId, ids)
                .set(FileTypeConfig::getEnabled, enabled);
        return update(updateWrapper);
    }
}
