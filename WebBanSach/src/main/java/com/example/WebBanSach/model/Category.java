package com.example.WebBanSach.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // 🔥 BỔ SUNG THƯ VIỆN NÀY

import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @JsonIgnore // 🔥 THÊM CÁI NÀY VÀO ĐỂ CHẶN VÒNG LẶP VÔ TẬN KHI TẠO API JSON
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Book> books;
}