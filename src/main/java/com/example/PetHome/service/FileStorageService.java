package com.example.PetHome.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    // 构造函数注入配置的路径
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        // 处理路径分隔符问题，确保路径有效
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建上传目录: " + uploadDir, ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 1. 生成唯一文件名 (防止重名覆盖)
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        String newFileName = UUID.randomUUID().toString() + extension;

        try {
            // 2. 检查文件名是否包含非法字符
            if (newFileName.contains("..")) {
                throw new RuntimeException("文件名无效: " + newFileName);
            }

            // 3. 将文件复制到目标位置 (覆盖同名文件)
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("无法保存文件 " + newFileName, ex);
        }
    }
}