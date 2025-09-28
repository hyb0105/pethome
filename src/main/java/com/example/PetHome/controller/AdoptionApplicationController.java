package com.example.PetHome.controller;

import com.example.PetHome.entity.AdoptionApplication;
import com.example.PetHome.service.AdoptionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class AdoptionApplicationController {

    @Autowired
    private AdoptionApplicationService applicationService;

    // 用户提交领养申请
    @PostMapping
    @PreAuthorize("isAuthenticated()") // 任何登录的用户都可以申请
    public ResponseEntity<AdoptionApplication> submitApplication(@RequestBody AdoptionApplication application, Principal principal) {
        AdoptionApplication createdApplication = applicationService.createApplication(application, principal.getName());
        if (createdApplication != null) {
            return ResponseEntity.ok(createdApplication);
        }
        return ResponseEntity.badRequest().build(); // 例如宠物已被申请
    }

    // 用户查看自己的申请记录
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<AdoptionApplication> getMyApplications(Principal principal) {
        return applicationService.getMyApplications(principal.getName());
    }

    // 管理员查看所有申请记录
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<AdoptionApplication> getAllApplications() {
        return applicationService.getAllApplications();
    }

    // 管理员审批申请
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> approveApplication(@PathVariable Integer id, @RequestBody Map<String, Integer> statusMap) {
        Integer status = statusMap.get("status");
        if (status == null || (status != 1 && status != 2)) {
            return ResponseEntity.badRequest().body("无效的状态值");
        }
        boolean success = applicationService.approveApplication(id, status);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}