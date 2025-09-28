package com.example.PetHome.service;

import com.example.PetHome.entity.Pet;
import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.PetMapper;
import com.example.PetHome.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private UserMapper userMapper;

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

    public List<Pet> getAllPets() {
        return petMapper.findAllPets();
    }

    public Pet updatePet(Pet pet) {
        petMapper.updatePet(pet);
        return petMapper.findPetById(pet.getId());
    }

    public boolean deletePet(Integer id) {
        return petMapper.deletePetById(id) > 0;
    }
}