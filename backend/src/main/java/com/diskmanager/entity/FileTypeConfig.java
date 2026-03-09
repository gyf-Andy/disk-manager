package com.diskmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("file_type_config")
public class FileTypeConfig implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String extension;
    
    private String description;
    
    private Integer enabled;
    
    private Integer previewAvailable;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
