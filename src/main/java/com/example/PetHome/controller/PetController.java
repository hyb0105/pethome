package com.example.PetHome.controller;

import com.example.PetHome.entity.Pet;
import com.example.PetHome.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import com.example.PetHome.entity.PageResult; // 【新增】导入PageResult

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Pet createPet(@RequestBody Pet pet, Principal principal) {
        return petService.addPet(pet, principal.getName());
    }

    @GetMapping
    public PageResult<Pet> getAllPets(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer gender,
            // 【新增】分页参数，并设置默认值
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return petService.getAllPets(type, breed, city, gender, pageNum, pageSize);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Integer id) {
        Pet pet = petService.getPetById(id);
        if (pet != null) {
            return ResponseEntity.ok(pet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Pet> updatePet(@PathVariable Integer id, @RequestBody Pet petDetails) {
        Pet pet = petService.getPetById(id);
        if (pet == null) {
            return ResponseEntity.notFound().build();
        }
        petDetails.setId(id);
        Pet updatedPet = petService.updatePet(petDetails);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deletePet(@PathVariable Integer id) {
        if (petService.deletePet(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}