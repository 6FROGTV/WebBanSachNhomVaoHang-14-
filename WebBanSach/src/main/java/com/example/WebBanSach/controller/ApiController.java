package com.example.WebBanSach.controller; 

import com.example.WebBanSach.model.*;       
import com.example.WebBanSach.repository.*;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class ApiController {

    @Autowired private BookRepository bookRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository; 

    // ==========================================
    // 🌟 API PUBLIC (Cho giao diện Khách hàng hiển thị F12)
    // ==========================================
    @GetMapping("/books") 
    public List<Book> getBooks() { return bookRepository.findAll(); }
    
    @GetMapping("/categories") 
    public List<Category> getCategories() { return categoryRepository.findAll(); }

    // API Đăng ký tài khoản
    @PostMapping("/register")
    public Map<String, Object> registerUserApi(@RequestBody User user) {
        user.setRole("USER");
        userRepository.save(user);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    // API Thêm vào Giỏ hàng (Lưu tạm vào Session)
    @PostMapping("/cart/add")
    public Map<String, Object> addToCartApi(@RequestParam Long bookId, @RequestParam int quantity, HttpSession session) {
        Integer cartCount = (Integer) session.getAttribute("cartCount");
        cartCount = (cartCount == null) ? quantity : cartCount + quantity;
        session.setAttribute("cartCount", cartCount);
        
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("cartCount", cartCount);
        return res;
    }

    // API Xóa khỏi Giỏ hàng
    @DeleteMapping("/cart/remove")
    public Map<String, Object> removeFromCartApi(@RequestParam Long bookId) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    // API Thanh toán (Đặt hàng)
    @PostMapping("/checkout")
    public Map<String, Object> processCheckoutApi(@RequestBody Order order) {
        order.setStatus("Chờ xác nhận");
        Order savedOrder = orderRepository.save(order);
        
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("orderId", savedOrder.getId());
        return res;
    }

    // ==========================================
    // 🌟 API ADMIN (Trả về dữ liệu JSON cho quản trị)
    // ==========================================
    @GetMapping("/admin/users") public List<User> getAdminUsers() { return userRepository.findAll(); }
    @GetMapping("/admin/orders") public List<Order> getAdminOrders() { return orderRepository.findAll(); }
    @GetMapping("/admin/books") public List<Book> getAdminBooks() { return bookRepository.findAll(); }
    @GetMapping("/admin/categories") public List<Category> getAdminCategories() { return categoryRepository.findAll(); }

    @GetMapping("/admin/revenue-stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookRepository.count());
        stats.put("totalCategories", categoryRepository.count());
        stats.put("totalUsers", userRepository.count());

        List<Order> validOrders = orderRepository.findAll().stream()
                .filter(o -> !"Đã hủy".equals(o.getStatus())).collect(Collectors.toList());

        stats.put("totalValidOrders", validOrders.size());
        stats.put("totalRevenue", validOrders.stream().mapToDouble(Order::getTotalAmount).sum());

        double[] monthlyRevenue = new double[12];
        for (Order order : validOrders) {
            if (order.getCreatedAt() != null) {
                monthlyRevenue[order.getCreatedAt().getMonthValue() - 1] += order.getTotalAmount();
            }
        }
        stats.put("monthlyRevenue", monthlyRevenue); 
        return stats; 
    }

    // --- CÁC API THÊM/SỬA/XÓA CHO ADMIN --- 
    @PostMapping("/admin/books") public Book addBook(@RequestBody Book book) { return bookRepository.save(book); }
    @PutMapping("/admin/books/{id}") public Book updateBook(@PathVariable Long id, @RequestBody Book book) { book.setId(id); return bookRepository.save(book); }
    @DeleteMapping("/admin/books/{id}") public void deleteBook(@PathVariable Long id) { bookRepository.deleteById(id); }

    @PostMapping("/admin/categories") public Category addCategory(@RequestBody Category cat) { return categoryRepository.save(cat); }
    @PutMapping("/admin/categories/{id}") public Category updateCategory(@PathVariable Long id, @RequestBody Category cat) { cat.setId(id); return categoryRepository.save(cat); }
    @DeleteMapping("/admin/categories/{id}") public void deleteCategory(@PathVariable Long id) { categoryRepository.deleteById(id); }

    @PostMapping("/admin/users") public User addUser(@RequestBody User user) { return userRepository.save(user); }
    @PutMapping("/admin/users/{id}") public User updateUser(@PathVariable Long id, @RequestBody User user) { user.setId(id); return userRepository.save(user); }
    @DeleteMapping("/admin/users/{id}") public void deleteUser(@PathVariable Long id) { userRepository.deleteById(id); }

    @PostMapping("/admin/orders") public Order addOrder(@RequestBody Order order) { return orderRepository.save(order); }
    @PutMapping("/admin/orders/{id}") public Order updateOrder(@PathVariable Long id, @RequestBody Order order) { order.setId(id); return orderRepository.save(order); }
    @DeleteMapping("/admin/orders/{id}") public void deleteOrder(@PathVariable Long id) { orderRepository.deleteById(id); }
}