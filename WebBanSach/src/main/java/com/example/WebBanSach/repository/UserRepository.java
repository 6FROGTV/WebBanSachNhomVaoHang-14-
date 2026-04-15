package com.example.WebBanSach.repository;

import com.example.WebBanSach.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Tác giả: thanh xuân - không vui
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Hàm này bắt buộc phải có để lấy thông tin User đang đăng nhập đem đi Đặt hàng
    Optional<User> findByUsername(String username);
    
    // Hai hàm này dùng để kiểm tra trùng lặp lúc Đăng ký tài khoản
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}