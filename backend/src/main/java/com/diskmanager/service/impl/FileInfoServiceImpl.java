package com.diskmanager.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diskmanager.common.PageResult;
import com.diskmanager.dto.FileSearchDTO;
import com.diskmanager.dto.FileUpdateDTO;
import com.diskmanager.entity.FileInfo;
import com.diskmanager.entity.FileTag;
import com.diskmanager.entity.Tag;
import com.diskmanager.mapper.FileInfoMapper;
import com.diskmanager.mapper.FileTagMapper;
import com.diskmanager.mapper.TagMapper;
import com.diskmanager.service.DiskInfoService;
import com.diskmanager.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {
    
    @Autowired
    private FileInfoMapper fileInfoMapper;
    
    @Autowired
    private FileTagMapper fileTagMapper;
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private DiskInfoService diskInfoService;
    
    @Value("${file.upload-path:./uploads}")
    private String uploadPath;
    
    private static final Set<String> PREVIEWABLE_TYPES = Set.of(
            "pdf", "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    );
    
    private static final Set<String> DOCUMENT_TYPES = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md"
    );
    
    @Override
    public PageResult<FileInfo> searchFiles(FileSearchDTO params) {
        Page<FileInfo> page = new Page<>(params.getPageNum(), params.getPageSize());
        IPage<FileInfo> result = fileInfoMapper.searchFiles(page, params);
        
        List<FileInfo> records = result.getRecords();
        records.forEach(file -> {
            List<Tag> tags = tagMapper.selectTagsByFileId(file.getId());
            file.setCategoryName(tags.isEmpty() ? null : tags.stream().map(Tag::getName).collect(Collectors.joining(", ")));
        });
        
        return PageResult.of(records, result.getTotal(), result.getSize(), result.getCurrent());
    }
    
    @Override
    public FileInfo getFileDetail(Long id) {
        FileInfo fileInfo = getById(id);
        if (fileInfo != null) {
            List<Tag> tags = tagMapper.selectTagsByFileId(id);
        }
        return fileInfo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFileInfo(FileUpdateDTO dto) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(dto.getId());
        fileInfo.setFileName(dto.getFileName());
        fileInfo.setCategoryId(dto.getCategoryId());
        fileInfo.setDescription(dto.getDescription());
        
        boolean updated = updateById(fileInfo);
        
        if (updated && dto.getTagIds() != null) {
            fileTagMapper.deleteByFileId(dto.getId());
            if (!dto.getTagIds().isEmpty()) {
                List<FileTag> fileTags = dto.getTagIds().stream()
                        .map(tagId -> {
                            FileTag ft = new FileTag();
                            ft.setFileId(dto.getId());
                            ft.setTagId(tagId);
                            return ft;
                        })
                        .collect(Collectors.toList());
                fileTagMapper.batchInsert(fileTags);
            }
        }
        
        return updated;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long id) {
        FileInfo fileInfo = getById(id);
        if (fileInfo != null) {
            fileTagMapper.deleteByFileId(id);
            return removeById(id);
        }
        return false;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFiles(List<Long> ids) {
        for (Long id : ids) {
            fileTagMapper.deleteByFileId(id);
        }
        return removeByIds(ids);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTagsToFile(Long fileId, List<Long> tagIds) {
        List<Long> existingTagIds = fileTagMapper.selectTagIdsByFileId(fileId);
        List<Long> newTagIds = tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId))
                .collect(Collectors.toList());
        
        if (!newTagIds.isEmpty()) {
            List<FileTag> fileTags = newTagIds.stream()
                    .map(tagId -> {
                        FileTag ft = new FileTag();
                        ft.setFileId(fileId);
                        ft.setTagId(tagId);
                        return ft;
                    })
                    .collect(Collectors.toList());
            fileTagMapper.batchInsert(fileTags);
        }
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTagFromFile(Long fileId, Long tagId) {
        return fileTagMapper.delete(new LambdaQueryWrapper<FileTag>()
                .eq(FileTag::getFileId, fileId)
                .eq(FileTag::getTagId, tagId)) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(MultipartFile file, Long categoryId, String description) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.extName(originalFilename);
            String newFileName = System.currentTimeMillis() + "_" + originalFilename;
            
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            Path filePath = uploadDir.resolve(newFileName);
            file.transferTo(filePath.toFile());
            
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(originalFilename);
            fileInfo.setFilePath(filePath.toString());
            fileInfo.setFileSize(file.getSize());
            fileInfo.setFileType(extension.toLowerCase());
            fileInfo.setMimeType(file.getContentType());
            fileInfo.setCategoryId(categoryId);
            fileInfo.setDescription(description);
            fileInfo.setPreviewAvailable(PREVIEWABLE_TYPES.contains(extension.toLowerCase()) ? 1 : 0);
            fileInfo.setMd5(DigestUtil.md5Hex(file.getInputStream()));
            fileInfo.setLastModified(LocalDateTime.now());
            
            save(fileInfo);
            
            return filePath.toString();
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public void downloadFile(Long id, HttpServletResponse response) {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new RuntimeException("文件不存在");
        }
        
        Path path = Paths.get(fileInfo.getFilePath());
        if (!Files.exists(path)) {
            throw new RuntimeException("文件不存在");
        }
        
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + 
                    URLEncoder.encode(fileInfo.getFileName(), StandardCharsets.UTF_8));
            response.setHeader("Content-Length", String.valueOf(fileInfo.getFileSize()));
            
            try (InputStream is = Files.newInputStream(path);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }
    
    @Override
    public void previewFile(Long id, HttpServletResponse response) {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new RuntimeException("文件不存在");
        }
        
        if (fileInfo.getPreviewAvailable() != 1) {
            throw new RuntimeException("该文件不支持预览");
        }
        
        Path path = Paths.get(fileInfo.getFilePath());
        if (!Files.exists(path)) {
            throw new RuntimeException("文件不存在");
        }
        
        try {
            response.setContentType(fileInfo.getMimeType());
            response.setHeader("Content-Disposition", "inline; filename=" + 
                    URLEncoder.encode(fileInfo.getFileName(), StandardCharsets.UTF_8));
            response.setHeader("Content-Length", String.valueOf(fileInfo.getFileSize()));
            
            try (InputStream is = Files.newInputStream(path);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (Exception e) {
            log.error("文件预览失败", e);
            throw new RuntimeException("文件预览失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<FileInfo> getFilesByTagId(Long tagId) {
        return fileInfoMapper.selectFilesByTagId(tagId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanDiskFiles(Long diskId) {
        diskInfoService.scanDisk(diskId);
    }
}
