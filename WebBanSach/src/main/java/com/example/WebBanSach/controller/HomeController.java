package com.example.WebBanSach.controller;

import com.example.WebBanSach.model.Book;
import com.example.WebBanSach.repository.BookRepository;
import com.example.WebBanSach.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Trang chủ: Hiện toàn bộ sách
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "index"; 
    }

    // Lọc sách theo Danh mục (Bấm trên thanh Menu ngang)
    @GetMapping("/category/{id}")
    public String viewBooksByCategory(@PathVariable("id") Long id, Model model) {
        model.addAttribute("books", bookRepository.findByCategoryId(id));
        
        categoryRepository.findById(id).ifPresent(cat -> 
            model.addAttribute("categoryName", cat.getName())
        );
        return "index"; 
    }

    // Xử lý Tìm kiếm sách (Nhập từ khóa trên Header)
    @GetMapping("/search")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("books", bookRepository.findByTitleContainingIgnoreCase(keyword));
        model.addAttribute("searchKeyword", keyword); 
        return "index";
    }

    // Chi tiết 1 cuốn sách
    @GetMapping("/book/{id}")
    public String bookDetail(@PathVariable("id") Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách với ID: " + id));
        model.addAttribute("book", book);
        return "book-detail"; 
    }
}