package com.example.PetHome.controller;

import com.example.PetHome.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        // 1. 保存文件，获取文件名
        String fileName = fileStorageService.storeFile(file);

        // 2. 拼接完整 URL (确保 WebConfig 已配置 /uploads/** 映射)
        String fullUrl = "http://localhost:8080/uploads/" + fileName;

        // 3. 返回 JSON 格式 (保持与前端 PetFormModal 兼容)
        Map<String, String> response = new HashMap<>();
        response.put("url", fullUrl);

        return ResponseEntity.ok(response);
    }
}