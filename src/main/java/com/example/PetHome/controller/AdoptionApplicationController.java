package com.example.PetHome.controller;

import com.example.PetHome.entity.AdminApplicationDetailDTO;
import com.example.PetHome.entity.AdoptionApplication;
import com.example.PetHome.entity.PageResult;
import com.example.PetHome.service.AdoptionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

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
    public PageResult<AdoptionApplication> getAllApplications(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return applicationService.getAllApplications(status, pageNum, pageSize);
    }

    // 【修改】管理员审批申请
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> approveApplication(@PathVariable Integer id, @RequestBody ApprovalRequest approvalRequest) { // 【修改】使用 DTO
        Integer status = approvalRequest.getStatus();
        String reason = approvalRequest.getRejectionReason();

        System.out.println("====== 后端接收到的审批状态: " + status + ", 理由: " + reason + " ======"); // 打印理由

        if (status == null || (status != 1 && status != 2)) {
            return ResponseEntity.badRequest().body("无效的状态值");
        }
        // 【修改】如果状态是拒绝(2)，理由不能为空
        if (status == 2 && (reason == null || reason.trim().isEmpty())) {
            return ResponseEntity.badRequest().body("拒绝申请必须填写理由");
        }

        // 【修改】调用更新后的 service 方法
        boolean success = applicationService.approveApplication(id, status, status == 2 ? reason : null);

        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 【新增】管理员根据ID获取申请详情
    // 【修改】管理员根据ID获取申请详情 --> 改为：获取指定ID的申请详情
    @GetMapping("/{id}")
    // 【修改】权限改为只要登录即可
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AdminApplicationDetailDTO> getApplicationDetail(@PathVariable Integer id, Principal principal) { // 【修改】添加 Principal
        // 【修改】将 principal 传递给 service
        AdminApplicationDetailDTO detail = applicationService.getApplicationDetail(id, principal);
        if (detail != null) {
            return ResponseEntity.ok(detail);
        } else {
            // 如果 service 返回 null (因为无权限或找不到)，则返回 404
            return ResponseEntity.notFound().build();
        }
    }

    // 【新增】用于接收审批请求体的 DTO
    @Data
    static class ApprovalRequest {
        private Integer status;
        private String rejectionReason;
    }

    // 【修改】用户重新提交已拒绝的申请 - 接收更新后的数据
    @PutMapping("/{id}/resubmit")
    @PreAuthorize("isAuthenticated()") // 仅限登录用户
    public ResponseEntity<?> resubmitApplication(@PathVariable Integer id,
                                                 @RequestBody AdoptionApplication updatedApplication, // 接收更新数据
                                                 Principal principal) {
        boolean success = applicationService.resubmitApplication(id, principal.getName(), updatedApplication);
        if (success) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "申请已重新提交审核");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "重新提交失败，请检查申请状态、宠物状态或地址信息");
            return ResponseEntity.badRequest().body(response);
        }
    }
}