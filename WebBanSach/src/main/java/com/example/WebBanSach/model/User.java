package com.example.WebBanSach.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String role; // "USER" hoặc "ADMIN"

    // Điểm thưởng tích lũy (Loyalty Points)
    @Column(name = "reward_points", columnDefinition = "int default 0")
    private int rewardPoints;
}