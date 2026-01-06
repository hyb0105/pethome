package com.example.PetHome.controller;

import com.example.PetHome.entity.DashboardStatsDTO;
import com.example.PetHome.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class DashboardController {

    @Autowired private UserMapper userMapper;
    @Autowired private PetMapper petMapper;
    @Autowired private AdoptionApplicationMapper applicationMapper;
    @Autowired private PetPostMapper petPostMapper;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // 聚合各项数据
        stats.setTotalUsers(userMapper.countUsers());
        stats.setTotalPets(petMapper.countPets());
        stats.setPendingApplications(applicationMapper.countPendingApplications());
        stats.setPendingPosts(petPostMapper.countPendingPosts());

        return ResponseEntity.ok(stats);
    }
}