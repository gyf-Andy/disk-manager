package com.diskmanager.dto;

import lombok.Data;
import java.util.List;

@Data
public class FileSearchDTO {
    
    private String keyword;
    private String fileType;
    private Long categoryId;
    private Long diskId;
    private List<Long> tagIds;
    private Long minSize;
    private Long maxSize;
    private String startTime;
    private String endTime;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String sortField;
    private String sortOrder;
}
