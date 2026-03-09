package com.diskmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diskmanager.entity.DiskInfo;
import java.util.List;

public interface DiskInfoService extends IService<DiskInfo> {
    
    List<DiskInfo> getSystemDisks();
    
    DiskInfo getDiskInfo(Long id);
    
    boolean refreshDiskInfo(Long id);
    
    boolean scanDisk(Long diskId);
}
