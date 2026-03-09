package com.diskmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diskmanager.common.PageResult;
import com.diskmanager.dto.FileSearchDTO;
import com.diskmanager.dto.FileUpdateDTO;
import com.diskmanager.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileInfoService extends IService<FileInfo> {
    
    PageResult<FileInfo> searchFiles(FileSearchDTO params);
    
    FileInfo getFileDetail(Long id);
    
    boolean updateFileInfo(FileUpdateDTO dto);
    
    boolean deleteFile(Long id);
    
    boolean deleteFiles(List<Long> ids);
    
    boolean addTagsToFile(Long fileId, List<Long> tagIds);
    
    boolean removeTagFromFile(Long fileId, Long tagId);
    
    String uploadFile(MultipartFile file, Long categoryId, String description);
    
    void downloadFile(Long id, HttpServletResponse response);
    
    void previewFile(Long id, HttpServletResponse response);
    
    List<FileInfo> getFilesByTagId(Long tagId);
    
    void scanDiskFiles(Long diskId);
}
