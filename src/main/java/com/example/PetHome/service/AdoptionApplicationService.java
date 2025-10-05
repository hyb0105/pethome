package com.example.PetHome.service;

import com.example.PetHome.entity.AdoptionApplication;
import com.example.PetHome.entity.Pet;
import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.AdoptionApplicationMapper;
import com.example.PetHome.mapper.PetMapper;
import com.example.PetHome.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdoptionApplicationService {

    @Autowired
    private AdoptionApplicationMapper applicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PetMapper petMapper;

    // 用户提交申请
    @Transactional
    public AdoptionApplication createApplication(AdoptionApplication application, String username) {
        User user = userMapper.findByUsername(username);
        Pet pet = petMapper.findPetById(application.getPetId());

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
    public boolean approveApplication(Integer applicationId, Integer status) {
        AdoptionApplication application = applicationMapper.findApplicationById(applicationId);
        if (application == null) {
            return false;
        }

        applicationMapper.updateApplicationStatus(applicationId, status);

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

    public List<AdoptionApplication> getMyApplications(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        return applicationMapper.findApplicationsByUserId(user.getId());
    }

    public List<AdoptionApplication> getAllApplications() {
        return applicationMapper.findAllApplications();
    }
}