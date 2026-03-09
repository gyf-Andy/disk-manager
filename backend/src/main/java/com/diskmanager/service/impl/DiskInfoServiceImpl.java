package com.diskmanager.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diskmanager.entity.DiskInfo;
import com.diskmanager.entity.FileInfo;
import com.diskmanager.mapper.DiskInfoMapper;
import com.diskmanager.mapper.FileInfoMapper;
import com.diskmanager.service.DiskInfoService;
import com.diskmanager.service.FileTypeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiskInfoServiceImpl extends ServiceImpl<DiskInfoMapper, DiskInfo> implements DiskInfoService {
    
    @Autowired
    private FileInfoMapper fileInfoMapper;
    
    @Autowired
    private FileTypeConfigService fileTypeConfigService;
    
    private static final Set<String> PREVIEWABLE_TYPES = Set.of(
            "pdf", "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    );
    
    private static final Set<String> SKIP_DIRECTORIES = Set.of(
            "$RECYCLE.BIN", "System Volume Information", "$Extend", 
            "Windows", "Program Files", "Program Files (x86)",
            "ProgramData", "Config.Msi"
    );
    
    @Override
    public List<DiskInfo> getSystemDisks() {
        List<DiskInfo> disks = new ArrayList<>();
        
        Iterable<File> roots;
        if (isWindows()) {
            roots = Arrays.asList(File.listRoots());
        } else {
            File mediaDir = new File("/media");
            File mntDir = new File("/mnt");
            List<File> mountPoints = new ArrayList<>();
            
            if (mediaDir.exists()) {
                mountPoints.addAll(Arrays.asList(mediaDir.listFiles(File::isDirectory)));
            }
            if (mntDir.exists()) {
                mountPoints.addAll(Arrays.asList(mntDir.listFiles(File::isDirectory)));
            }
            mountPoints.add(new File("/"));
            roots = mountPoints;
        }
        
        for (File root : roots) {
            try {
                DiskInfo disk = new DiskInfo();
                String path = root.getAbsolutePath();
                
                if (isWindows()) {
                    String driveLetter = path.substring(0, 1);
                    disk.setDiskName("本地磁盘 (" + driveLetter + ":)");
                } else {
                    disk.setDiskName(root.getName().isEmpty() ? "根目录" : root.getName());
                }
                
                disk.setDiskPath(path);
                disk.setTotalSpace(root.getTotalSpace());
                disk.setUsedSpace(root.getTotalSpace() - root.getFreeSpace());
                disk.setFreeSpace(root.getFreeSpace());
                disk.setStatus(1);
                
                try {
                    FileStore store = Files.getFileStore(Paths.get(path));
                    String type = store.type();
                    disk.setDiskType(type);
                } catch (Exception e) {
                    disk.setDiskType("UNKNOWN");
                }
                
                DiskInfo existingDisk = getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DiskInfo>()
                        .eq(DiskInfo::getDiskPath, path));
                
                if (existingDisk != null) {
                    disk.setId(existingDisk.getId());
                    updateById(disk);
                } else {
                    save(disk);
                }
                
                disks.add(disk);
            } catch (Exception e) {
                log.warn("获取磁盘信息失败: {}", root.getAbsolutePath(), e);
            }
        }
        
        return disks;
    }
    
    @Override
    public DiskInfo getDiskInfo(Long id) {
        return getById(id);
    }
    
    @Override
    public boolean refreshDiskInfo(Long id) {
        DiskInfo disk = getById(id);
        if (disk == null) {
            return false;
        }
        
        File root = new File(disk.getDiskPath());
        if (!root.exists()) {
            disk.setStatus(0);
            updateById(disk);
            return false;
        }
        
        disk.setTotalSpace(root.getTotalSpace());
        disk.setUsedSpace(root.getTotalSpace() - root.getFreeSpace());
        disk.setFreeSpace(root.getFreeSpace());
        disk.setStatus(1);
        
        return updateById(disk);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean scanDisk(Long diskId) {
        DiskInfo disk = getById(diskId);
        if (disk == null || disk.getStatus() != 1) {
            return false;
        }
        
        Path rootPath = Paths.get(disk.getDiskPath());
        if (!Files.exists(rootPath)) {
            return false;
        }
        
        List<String> enabledExtensions = fileTypeConfigService.getEnabledExtensions();
        Set<String> enabledExtensionSet = enabledExtensions.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        if (enabledExtensionSet.isEmpty()) {
            log.warn("没有启用任何文件类型配置，将扫描所有文件");
        }
        
        try {
            Set<String> existingPaths = fileInfoMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FileInfo>()
                            .eq(FileInfo::getDiskId, diskId)
                            .select(FileInfo::getFilePath)
            ).stream().map(FileInfo::getFilePath).collect(Collectors.toSet());
            
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    String dirName = dir.getFileName() != null ? dir.getFileName().toString() : "";
                    if (SKIP_DIRECTORIES.contains(dirName)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                    try {
                        if (!Files.isRegularFile(path)) {
                            return FileVisitResult.CONTINUE;
                        }
                        
                        String extension = FileUtil.extName(path.getFileName().toString()).toLowerCase();
                        if (!enabledExtensionSet.isEmpty() && !enabledExtensionSet.contains(extension)) {
                            return FileVisitResult.CONTINUE;
                        }
                        
                        String filePath = path.toString();
                        if (existingPaths.contains(filePath)) {
                            return FileVisitResult.CONTINUE;
                        }
                        
                        FileInfo fileInfo = createFileInfo(path, diskId);
                        fileInfoMapper.insert(fileInfo);
                        
                    } catch (Exception e) {
                        log.debug("扫描文件失败: {}", path, e);
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path path, IOException exc) {
                    log.debug("无法访问文件: {} - {}", path, exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }
            });
            
            return true;
        } catch (IOException e) {
            log.error("扫描磁盘失败", e);
            return false;
        }
    }
    
    private FileInfo createFileInfo(Path path, Long diskId) throws IOException {
        FileInfo fileInfo = new FileInfo();
        
        String fileName = path.getFileName().toString();
        String extension = FileUtil.extName(fileName);
        
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(path.toString());
        fileInfo.setFileSize(Files.size(path));
        fileInfo.setFileType(extension.toLowerCase());
        fileInfo.setDiskId(diskId);
        fileInfo.setPreviewAvailable(PREVIEWABLE_TYPES.contains(extension.toLowerCase()) ? 1 : 0);
        
        try {
            String mimeType = Files.probeContentType(path);
            fileInfo.setMimeType(mimeType != null ? mimeType : "application/octet-stream");
        } catch (Exception e) {
            fileInfo.setMimeType("application/octet-stream");
        }
        
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            fileInfo.setLastModified(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(attrs.lastModifiedTime().toMillis()),
                    ZoneId.systemDefault()
            ));
        } catch (Exception e) {
            fileInfo.setLastModified(LocalDateTime.now());
        }
        
        return fileInfo;
    }
    
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
}
