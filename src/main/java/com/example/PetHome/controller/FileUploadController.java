package com.example.PetHome.controller;

import com.example.PetHome.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // 1. 保存文件，拿到文件名 (例如: uuid.jpg)
        String fileName = fileStorageService.storeFile(file);

        // 2. 【关键修正】拼接完整的访问路径
        // 这里的 /uploads/ 对应 WebConfig 中的配置
        String fileUrl = "http://localhost:8080/uploads/" + fileName;

        // 3. 直接返回字符串 URL，前端拿到后直接存入数据库
        return ResponseEntity.ok(fileUrl);
    }
}