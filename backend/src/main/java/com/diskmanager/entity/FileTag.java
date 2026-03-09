package com.diskmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("file_tag")
public class FileTag implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long fileId;
    
    private Long tagId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
