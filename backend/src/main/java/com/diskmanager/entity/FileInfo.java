package com.diskmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("file_info")
public class FileInfo implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String fileName;
    
    private String filePath;
    
    private Long fileSize;
    
    private String fileType;
    
    private String mimeType;
    
    private Long diskId;
    
    private Long categoryId;
    
    private LocalDateTime lastModified;
    
    private String md5;
    
    private String description;
    
    private Integer previewAvailable;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String categoryName;
    
    @TableField(exist = false)
    private String diskName;
}
