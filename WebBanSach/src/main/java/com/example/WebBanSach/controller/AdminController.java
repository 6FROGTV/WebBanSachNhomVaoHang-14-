package com.example.WebBanSach.controller;

import com.example.WebBanSach.model.Book;
import com.example.WebBanSach.model.Category;
import com.example.WebBanSach.model.Order;
import com.example.WebBanSach.repository.BookRepository;
import com.example.WebBanSach.repository.CategoryRepository;
import com.example.WebBanSach.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Thư viện xử lý File

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin") 
public class AdminController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping({"", "/"})
    public String adminDashboard() {
        return "admin/dashboard"; 
    }

    // ==========================================
    // ======== QUẢN LÝ SÁCH (BOOK CRUD) ========
    // ==========================================
    @GetMapping("/books")
    public String manageBooks(Model model) {
        model.addAttribute("books", bookRepository.findAll()); 
        return "admin/book-list"; 
    }

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryRepository.findAll()); 
        return "admin/book-form"; 
    }

    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách: " + id));
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/book-form"; 
    }

    // ĐÃ NÂNG CẤP HÀM NÀY ĐỂ XỬ LÝ UPLOAD ẢNH
    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute("book") Book book, 
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        
        // Nếu người dùng có chọn file ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Đường dẫn tới thư mục lưu ảnh của project
                String uploadDir = "src/main/resources/static/images/";
                Path uploadPath = Paths.get(uploadDir);
                
                // Nếu thư mục chưa tồn tại thì tạo mới
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Đổi tên file để tránh trùng lặp (Thêm timestamp ở đầu)
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                
                // Copy file ảnh từ trình duyệt vào thư mục của project
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Lưu đường dẫn ảnh vào database (Ví dụ: /images/164502394_sach.jpg)
                book.setImageUrl("/images/" + fileName);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        bookRepository.save(book);
        return "redirect:/admin/books?success"; 
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookRepository.deleteById(id);
        return "redirect:/admin/books?deleted";
    }

    // ==========================================
    // ====== QUẢN LÝ DANH MỤC (CATEGORIES) =====
    // ==========================================
    @GetMapping("/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/category-list"; 
    }

    @GetMapping("/categories/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-form"; 
    }

    @GetMapping("/categories/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục: " + id));
        model.addAttribute("category", category);
        return "admin/category-form"; 
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("category") Category category) {
        categoryRepository.save(category);
        return "redirect:/admin/categories?success"; 
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories?deleted";
    }

    // ==========================================
    // ======== QUẢN LÝ ĐƠN HÀNG (ORDERS) =======
    // ==========================================
    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/order-list"; 
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + id));
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin/orders?success";
    }

    // ==========================================
    // ====== THỐNG KÊ DOANH THU (REVENUE) ======
    // ==========================================
    @GetMapping("/revenue")
    public String viewRevenue(Model model) {
        List<Order> allOrders = orderRepository.findAll();
        
        List<Order> validOrders = allOrders.stream()
                .filter(o -> !"Đã hủy".equals(o.getStatus()))
                .collect(Collectors.toList());

        long totalOrders = validOrders.size();
        double totalRevenue = validOrders.stream().mapToDouble(Order::getTotalAmount).sum();

        double[] monthlyRevenue = new double[12];
        for (Order order : validOrders) {
            if (order.getCreatedAt() != null) {
                int monthIndex = order.getCreatedAt().getMonthValue() - 1; 
                monthlyRevenue[monthIndex] += order.getTotalAmount();
            }
        }

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("monthlyRevenue", monthlyRevenue); 

        return "admin/revenue"; 
    }
}