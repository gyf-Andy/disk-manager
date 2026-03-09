package com.diskmanager.dto;

import lombok.Data;
import java.util.List;

@Data
public class FileUpdateDTO {
    
    private Long id;
    private String fileName;
    private Long categoryId;
    private String description;
    private List<Long> tagIds;
}
