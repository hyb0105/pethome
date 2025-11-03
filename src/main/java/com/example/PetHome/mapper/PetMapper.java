package com.example.PetHome.mapper;

import com.example.PetHome.entity.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetMapper {
    int insertPet(Pet pet);
    Pet findPetById(Integer id);
    List<Pet> findAllPets(@Param("type") String type,
                          @Param("breed") String breed,
                          @Param("city") String city,
                          @Param("gender") Integer gender,
                          @Param("status") Integer status,
                          @Param("isAdmin") boolean isAdmin);


    int updatePet(Pet pet);
    int deletePetById(Integer id);
}