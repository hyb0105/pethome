package com.example.PetHome.mapper;

import com.example.PetHome.entity.AdminApplicationDetailDTO;
import com.example.PetHome.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdoptionApplicationMapper {
    // 新增领养申请
    int insertApplication(AdoptionApplication application);

    // (管理员) 查询所有申请
    List<AdoptionApplication> findAllApplications(@Param("status") Integer status);

    // (用户) 根据用户ID查询自己的申请
    List<AdoptionApplication> findApplicationsByUserId(Integer adopterId);

    // (管理员) 根据ID查询单个申请
    AdoptionApplication findApplicationById(Integer id);

    // 【新增】(管理员) 根据ID查询申请详情 (包含关联信息)
    AdminApplicationDetailDTO findApplicationDetailById(@Param("id") Integer id);

    // (管理员) 更新申请状态
    int updateApplicationStatus(@Param("id") Integer id, @Param("status") Integer status,@Param("rejectionReason") String rejectionReason);

    // 【新增】用户重新提交申请 (更新状态和时间)
    int resubmitApplicationStatus(@Param("id") Integer id, @Param("adopterId") Integer adopterId);
    // 【修改】用户重新提交申请 (更新状态、时间和可编辑字段)
    int resubmitApplication(AdoptionApplication application);
    // 【【【 新增这个方法 】】】
    int deleteApplicationsByPetId(Integer petId);

    Integer countPendingApplications();
}