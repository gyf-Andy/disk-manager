-- 创建数据库
CREATE DATABASE IF NOT EXISTS disk_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE disk_manager;

-- 硬盘信息表
CREATE TABLE IF NOT EXISTS `disk_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `disk_name` VARCHAR(100) NOT NULL COMMENT '硬盘名称',
    `disk_path` VARCHAR(500) NOT NULL COMMENT '硬盘挂载路径',
    `total_space` BIGINT NOT NULL DEFAULT 0 COMMENT '总空间(字节)',
    `used_space` BIGINT NOT NULL DEFAULT 0 COMMENT '已用空间(字节)',
    `free_space` BIGINT NOT NULL DEFAULT 0 COMMENT '剩余空间(字节)',
    `disk_type` VARCHAR(50) DEFAULT 'UNKNOWN' COMMENT '硬盘类型',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-离线 1-在线',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_disk_path` (`disk_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='硬盘信息表';

-- 文件分类表
CREATE TABLE IF NOT EXISTS `file_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件分类表';

-- 文件信息表
CREATE TABLE IF NOT EXISTS `file_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名称',
    `file_path` VARCHAR(1000) NOT NULL COMMENT '文件完整路径',
    `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
    `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型/扩展名',
    `mime_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
    `disk_id` BIGINT DEFAULT NULL COMMENT '所属硬盘ID',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `last_modified` DATETIME DEFAULT NULL COMMENT '文件最后修改时间',
    `md5` VARCHAR(64) DEFAULT NULL COMMENT '文件MD5值',
    `description` VARCHAR(1000) DEFAULT NULL COMMENT '文件描述',
    `preview_available` TINYINT NOT NULL DEFAULT 0 COMMENT '是否可预览: 0-否 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_file_name` (`file_name`),
    KEY `idx_file_type` (`file_type`),
    KEY `idx_disk_id` (`disk_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `color` VARCHAR(20) DEFAULT '#1890ff' COMMENT '标签颜色',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '标签描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_name` (`name`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 文件标签关联表
CREATE TABLE IF NOT EXISTS `file_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_id` BIGINT NOT NULL COMMENT '文件ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_tag` (`file_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件标签关联表';

-- 文件类型配置表
CREATE TABLE IF NOT EXISTS `file_type_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `extension` VARCHAR(20) NOT NULL COMMENT '文件扩展名',
    `description` VARCHAR(100) DEFAULT NULL COMMENT '类型描述',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用扫描: 0-否 1-是',
    `preview_available` TINYINT NOT NULL DEFAULT 0 COMMENT '是否可预览: 0-否 1-是',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_extension` (`extension`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件类型配置表';

-- 插入默认分类
INSERT INTO `file_category` (`name`, `description`, `parent_id`, `sort_order`) VALUES
('文档', '文档类文件', 0, 1),
('图片', '图片类文件', 0, 2),
('视频', '视频类文件', 0, 3),
('音频', '音频类文件', 0, 4),
('其他', '其他类型文件', 0, 5);

-- 插入默认标签
INSERT INTO `tag` (`name`, `color`, `description`) VALUES
('重要', '#f5222d', '重要文件'),
('待处理', '#fa8c16', '需要处理的文件'),
('已归档', '#52c41a', '已归档文件'),
('临时', '#1890ff', '临时文件');

-- 插入默认文件类型配置
INSERT INTO `file_type_config` (`extension`, `description`, `enabled`, `preview_available`, `sort_order`) VALUES
('pdf', 'PDF文档', 1, 1, 1),
('doc', 'Word文档(旧版)', 1, 0, 2),
('docx', 'Word文档', 1, 0, 3),
('xls', 'Excel表格(旧版)', 1, 0, 4),
('xlsx', 'Excel表格', 1, 0, 5),
('ppt', 'PowerPoint(旧版)', 1, 0, 6),
('pptx', 'PowerPoint', 1, 0, 7),
('txt', '文本文件', 1, 0, 8),
('jpg', 'JPEG图片', 1, 1, 10),
('jpeg', 'JPEG图片', 1, 1, 11),
('png', 'PNG图片', 1, 1, 12),
('gif', 'GIF图片', 1, 1, 13),
('bmp', 'BMP图片', 1, 1, 14),
('mp4', 'MP4视频', 1, 0, 20),
('avi', 'AVI视频', 1, 0, 21),
('mkv', 'MKV视频', 1, 0, 22),
('mp3', 'MP3音频', 1, 0, 30),
('wav', 'WAV音频', 1, 0, 31),
('zip', 'ZIP压缩包', 1, 0, 40),
('rar', 'RAR压缩包', 1, 0, 41);
