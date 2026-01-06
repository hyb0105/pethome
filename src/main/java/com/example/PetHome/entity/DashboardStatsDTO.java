package com.example.PetHome.entity;

import lombok.Data; // 如果你没用lombok，请手动生成Getter/Setter

@Data
public class DashboardStatsDTO {
    private Integer totalUsers;          // 总用户数
    private Integer totalPets;           // 总宠物数
    private Integer pendingApplications; // 待审核领养申请
    private Integer pendingPosts;        // 待审核帖子
    // 如果需要，还可以加 todayVisits (今日访问) 等
}