package com.example.PetHome.service;

import com.example.PetHome.entity.Pet;
import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.PetMapper;
import com.example.PetHome.mapper.UserMapper;

import com.example.PetHome.mapper.AdoptionApplicationMapper;
import com.example.PetHome.mapper.PetCommentMapper;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
// 【新增】导入PageHelper相关类
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.PetHome.entity.Pet;
import com.example.PetHome.entity.User;
// 【新增】导入我们刚创建的PageResult
import com.example.PetHome.entity.PageResult;

@Service
public class PetService {

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private UserMapper userMapper;

    // 【【【 新增注入 】】】
    @Autowired(required = false) // required=false 避免在旧测试中出错
    private AdoptionApplicationMapper applicationMapper;

    @Autowired(required = false) // required=false 避免在旧测试中出错
    private PetCommentMapper commentMapper;

    public Pet addPet(Pet pet, String ownerUsername) {
        User owner = userMapper.findByUsername(ownerUsername);
        if (owner != null) {
            pet.setOwnerId(owner.getId());
            petMapper.insertPet(pet);
            return pet;
        }
        return null;
    }

    public Pet getPetById(Integer id) {
        return petMapper.findPetById(id);
    }

    // 【修改】修改getAllPets方法，增加分页参数
    public PageResult<Pet> getAllPets(String type, String breed, String city, Integer gender, Integer status, Integer pageNum, Integer pageSize, boolean isAdmin) {
        // 1. 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 2. 执行查询 (这行代码和原来一样，PageHelper会自动拦截)
        List<Pet> petList = petMapper.findAllPets(type, breed, city, gender, status, isAdmin);

        // 3. 包装查询结果到PageInfo对象中，获取总记录数等信息
        PageInfo<Pet> pageInfo = new PageInfo<>(petList);

        // 4. 将结果封装到我们自定义的PageResult中返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    public Pet updatePet(Pet pet) {
        petMapper.updatePet(pet);
        return petMapper.findPetById(pet.getId());
    }

    // 【【【 修改：添加 @Transactional 并先删除子记录 】】】
    @Transactional
    public boolean deletePet(Integer id) {
        // 1. 先删除所有关联的领养申请
        if (applicationMapper != null) {
            applicationMapper.deleteApplicationsByPetId(id);
        }

        // 2. 再删除所有关联的评论
        if (commentMapper != null) {
            commentMapper.deleteCommentsByPetId(id);
        }

        // 3. 最后删除宠物
        return petMapper.deletePetById(id) > 0;
    }
}
