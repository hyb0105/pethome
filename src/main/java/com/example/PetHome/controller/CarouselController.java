package com.example.PetHome.controller;

import com.example.PetHome.entity.Carousel;
import com.example.PetHome.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carousels")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    // 获取所有轮播图 (公开)
    @GetMapping
    public List<Carousel> getList() {
        return carouselService.getAll();
    }

    // 保存或更新 (仅管理员)
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> save(@RequestBody Carousel carousel) {
        if (carouselService.save(carousel)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    // 删除 (仅管理员)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (carouselService.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}