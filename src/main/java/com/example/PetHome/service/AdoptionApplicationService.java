package com.example.PetHome.service;

import com.example.PetHome.entity.*;
import com.example.PetHome.mapper.AdoptionApplicationMapper;
import com.example.PetHome.mapper.PetMapper;
import com.example.PetHome.mapper.UserMapper;
import com.example.PetHome.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal; // 【新增】导入 Principal
import org.springframework.security.core.Authentication; // 【新增】
import org.springframework.security.core.GrantedAuthority; // 【新增】
import org.springframework.security.core.context.SecurityContextHolder; // 【新增】

import java.util.List;

@Service
public class AdoptionApplicationService {

    @Autowired
    private AdoptionApplicationMapper applicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private AddressMapper addressMapper;

    // 用户提交申请
    @Transactional
    public AdoptionApplication createApplication(AdoptionApplication application, String username) {
        User user = userMapper.findByUsername(username);
        Pet pet = petMapper.findPetById(application.getPetId());

        // 【新增】验证地址 ID (如果前端传了的话)
        Address address = null;
        if (application.getAddressId() != null) {
            address = addressMapper.findAddressById(application.getAddressId(), user.getId());
        }

        if (user == null || pet == null || pet.getStatus() != 0) { // 宠物状态必须是"待领养"
            return null;
        }

        application.setAdopterId(user.getId());
        applicationMapper.insertApplication(application);

        // 提交申请后，将宠物状态更新为"审核中" (status=2)
//        pet.setStatus(2);
//        petMapper.updatePet(pet);

        return application;
    }

    // 管理员审批申请
    @Transactional
    public boolean approveApplication(Integer applicationId, Integer status, String rejectionReason) {
        AdoptionApplication application = applicationMapper.findApplicationById(applicationId);
        if (application == null) {
            return false;
        }

        applicationMapper.updateApplicationStatus(applicationId, status, rejectionReason);

        // 如果审核通过 (status=1)，则将宠物状态更新为"已领养"
        if (status == 1) {
            Pet pet = petMapper.findPetById(application.getPetId());
            if (pet != null) {
                pet.setStatus(1);
                petMapper.updatePet(pet);
            }
        }
        // 如果审核不通过 (status=2)，则将宠物状态恢复为"待领养"
        else if (status == 2) {
            Pet pet = petMapper.findPetById(application.getPetId());
            if (pet != null && pet.getStatus() == 2) { // 确保是从审核中状态恢复
                pet.setStatus(0);
                petMapper.updatePet(pet);
            }
        }
        return true;
    }

    // 【修改】获取申请详情，增加 Principal 参数和权限检查逻辑
    public AdminApplicationDetailDTO getApplicationDetail(Integer applicationId, Principal principal) {
        // 1. 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || principal == null) {
            return null; // 未登录或认证信息异常
        }
        User currentUser = userMapper.findByUsername(principal.getName());
        if (currentUser == null) {
            return null; // 用户不存在
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // 2. 查询申请详情
        AdminApplicationDetailDTO detail = applicationMapper.findApplicationDetailById(applicationId);

        // 3. 权限检查
        if (detail != null) {
            if (isAdmin) {
                // 管理员可以直接查看
                return detail;
            } else {
                // 普通用户只能查看自己的申请
                if (detail.getAdopterId() != null && detail.getAdopterId().equals(currentUser.getId())) {
                    return detail;
                } else {
                    // 不是自己的申请，返回 null 表示无权限或找不到
                    return null;
                }
            }
        } else {
            // 申请本身不存在
            return null;
        }
    }

    public List<AdoptionApplication> getMyApplications(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        return applicationMapper.findApplicationsByUserId(user.getId());
    }

    // 【【修改】】 添加第三个参数 AdoptionApplication updatedApplicationData
    @Transactional
    public boolean resubmitApplication(Integer applicationId, String username, AdoptionApplication updatedApplicationData) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return false; // 用户不存在
        }

        // 1. 检查原始申请是否存在且属于该用户且已被拒绝
        AdoptionApplication originalApplication = applicationMapper.findApplicationById(applicationId);
        if (originalApplication == null || !originalApplication.getAdopterId().equals(user.getId()) || originalApplication.getStatus() != 2) {
            return false; // 申请不存在，或不属于该用户，或状态不是已拒绝
        }

        // 2. 检查关联的宠物是否仍然可申请 (status == 0)
        Pet pet = petMapper.findPetById(originalApplication.getPetId());
        if (pet == null || pet.getStatus() != 0) {
            // 宠物不存在或已被领养/审核中
            return false;
        }

        // 3. 【新增】验证新的地址 ID 是否有效且属于该用户
        // (这里的 updatedApplicationData 现在是已定义的参数了)
        if (updatedApplicationData.getAddressId() != null) {
            Address address = addressMapper.findAddressById(updatedApplicationData.getAddressId(), user.getId());
            if (address == null) {
                return false; // 新选择的地址无效或不属于该用户
            }
        } else {
            return false; // 必须选择一个地址
        }

        // 4. 准备更新对象 (确保ID和AdopterID来自原始记录，防止篡改)
        updatedApplicationData.setId(applicationId);
        updatedApplicationData.setAdopterId(user.getId());
        // PetId 不需要更新，保持原始值
        updatedApplicationData.setPetId(originalApplication.getPetId());
        // 状态和时间将在Mapper中设置

        // 5. 执行更新
        int affectedRows = applicationMapper.resubmitApplication(updatedApplicationData); // 调用修改后的 Mapper 方法
        return affectedRows > 0;
    }

    public List<AdoptionApplication> getAllApplications() {
        return applicationMapper.findAllApplications();
    }
}