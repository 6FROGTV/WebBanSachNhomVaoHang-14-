package com.example.WebBanSach.repository;

import com.example.WebBanSach.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Tìm sách theo ID của Danh mục
    List<Book> findByCategoryId(Long categoryId);
    
    // Tìm kiếm theo tên sách (Bỏ qua phân biệt viết hoa/viết thường)
    List<Book> findByTitleContainingIgnoreCase(String keyword);
}