package com.example.PetHome.mapper;

import com.example.PetHome.entity.Pet;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PetMapper {
    int insertPet(Pet pet);
    Pet findPetById(Integer id);
    List<Pet> findAllPets();
    int updatePet(Pet pet);
    int deletePetById(Integer id);
}