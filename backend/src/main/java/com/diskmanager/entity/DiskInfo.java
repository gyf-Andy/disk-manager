package com.diskmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("disk_info")
public class DiskInfo implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String diskName;
    
    private String diskPath;
    
    private Long totalSpace;
    
    private Long usedSpace;
    
    private Long freeSpace;
    
    private String diskType;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
