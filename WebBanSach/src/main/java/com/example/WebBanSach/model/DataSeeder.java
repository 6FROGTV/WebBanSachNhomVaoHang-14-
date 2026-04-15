package com.example.WebBanSach.model; // Nhớ kiểm tra lại package xem đúng của bạn chưa nha

import com.example.WebBanSach.model.Book;
import com.example.WebBanSach.model.Category;
import com.example.WebBanSach.repository.BookRepository;
import com.example.WebBanSach.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(CategoryRepository categoryRepository, BookRepository bookRepository) {
        return args -> {
            
            // 1. KIỂM TRA VÀ BƠM DANH MỤC
            if (categoryRepository.count() == 0) {
                Category cat1 = new Category(); cat1.setName("Văn học trong nước");
                Category cat2 = new Category(); cat2.setName("Kỹ năng sống");
                Category cat3 = new Category(); cat3.setName("Kinh tế - Kinh doanh");
                Category cat4 = new Category(); cat4.setName("Sách ngoại ngữ");
                Category cat5 = new Category(); cat5.setName("Sách thiếu nhi");

                categoryRepository.saveAll(Arrays.asList(cat1, cat2, cat3, cat4, cat5));
                System.out.println("ĐÃ BƠM THÀNH CÔNG DỮ LIỆU DANH MỤC!");
            }

            // 2. BƠM 30 CUỐN SÁCH CÓ NỘI DUNG GIỚI THIỆU RIÊNG BIỆT
            if (bookRepository.count() == 0) {
                List<Category> cats = categoryRepository.findAll();
                if (cats.isEmpty()) return;

                // CẤU TRÚC MỚI: {Tựa sách, Tác giả, Giá, Tồn kho, Khuyến mãi, Tên file ảnh, Tên Danh Mục, NỘI DUNG GIỚI THIỆU}
                String[][] bookData = {
                    // Danh mục 1: Văn học trong nước
                    {"Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "50000", "100", "0", "/images/sach1.jpg", "Văn học trong nước", "Tác phẩm kinh điển gắn liền với tuổi thơ của biết bao thế hệ người Việt. Cuộc phiêu lưu kỳ thú của chú Dế Mèn sẽ mang đến những bài học sâu sắc về tình bạn và lý tưởng sống."},
                    {"Mắt Biếc", "Nguyễn Nhật Ánh", "110000", "50", "10", "/images/sach2.webp", "Văn học trong nước", "Một câu chuyện tình buồn, trong trẻo và day dứt của Ngạn dành cho Hà Lan. Tác phẩm đưa người đọc lên chuyến tàu về lại tuổi thơ nơi làng Đo Đo mộc mạc."},
                    {"Tôi Thấy Hoa Vàng Trên Cỏ Xanh", "Nguyễn Nhật Ánh", "95000", "45", "0", "/images/sach3.jpg", "Văn học trong nước", "Những trang sách mở ra một thế giới tuổi thơ hồn nhiên, pha lẫn những rung động đầu đời và tình cảm anh em sâu sắc, cảm động."},
                    {"Số Đỏ", "Vũ Trọng Phụng", "60000", "30", "20", "/images/sach4.jpg", "Văn học trong nước", "Kiệt tác trào phúng đỉnh cao của văn học Việt Nam. Khắc họa chân dung Xuân Tóc Đỏ và một xã hội lố lăng, kệch cỡm thời bấy giờ."},
                    {"Đất Rừng Phương Nam", "Đoàn Giỏi", "75000", "80", "15", "/images/sach5.jpg", "Văn học trong nước", "Hành trình tìm cha đầy gian truân nhưng cũng vô cùng kỳ thú của cậu bé An giữa thiên nhiên miền Tây Nam Bộ hoang sơ và hào sảng."},
                    {"Tuổi Thơ Dữ Dội", "Phùng Quán", "85000", "60", "0", "/images/sach6.jpg", "Văn học trong nước", "Khúc tráng ca hào hùng và bi thương về đội thiếu niên trinh sát Trần Cao Vân. Một cuốn sách lấy đi nước mắt của hàng triệu độc giả."},

                    // Danh mục 2: Kỹ năng sống
                    {"Đắc Nhân Tâm", "Dale Carnegie", "80000", "120", "20", "/images/sach7.webp", "Kỹ năng sống", "Cuốn sách nghệ thuật giao tiếp kinh điển nhất mọi thời đại. Giúp bạn thấu hiểu tâm lý con người và xây dựng các mối quan hệ bền chặt."},
                    {"Tuổi Trẻ Đáng Giá Bao Nhiêu", "Rosie Nguyễn", "90000", "100", "10", "/images/sach8.webp", "Kỹ năng sống", "Cẩm nang dẫn đường cho những người trẻ đang chênh vênh. Truyền cảm hứng để bạn dám dấn thân, học hỏi và cháy hết mình với đam mê."},
                    {"Nhà Giả Kim", "Paulo Coelho", "79000", "150", "25", "/images/sach9.jpg", "Kỹ năng sống", "Cuộc hành trình theo đuổi giấc mơ của cậu bé chăn cừu Santiago. Cuốn sách mang tính triết lý sâu sắc, thúc đẩy bạn lắng nghe trái tim mình."},
                    {"Nghệ Thuật Tinh Tế Của Việc Đếch Quan Tâm", "Mark Manson", "120000", "70", "0", "/images/sach10.jpg", "Kỹ năng sống", "Góc nhìn thực tế, thô nhưng thật về cách đối mặt với những khó khăn trong đời. Dạy bạn cách lựa chọn những điều thực sự đáng để quan tâm."},
                    {"Sức Mạnh Của Thói Quen", "Charles Duhigg", "150000", "50", "30", "/images/sach11.jpg", "Kỹ năng sống", "Khám phá cơ chế hình thành thói quen trong não bộ. Chìa khóa để thay đổi bản thân, từ bỏ thói quen xấu và xây dựng những thói quen thành công."},
                    {"Tư Duy Nhanh Và Chậm", "Daniel Kahneman", "200000", "40", "40", "/images/sach12.webp", "Kỹ năng sống", "Phân tích hai hệ thống tư duy chi phối con người. Giúp bạn hiểu rõ tại sao chúng ta lại đưa ra những quyết định sai lầm và cách khắc phục."},

                    // Danh mục 3: Kinh tế - Kinh doanh
                    {"Cha Giàu Cha Nghèo", "Robert Kiyosaki", "110000", "90", "20", "/images/sach13.webp", "Kinh tế - Kinh doanh", "Bài học vỡ lòng về giáo dục tài chính. Thay đổi hoàn toàn tư duy của bạn về tiền bạc, tài sản và cách bắt tiền làm việc cho mình."},
                    {"Chiến Tranh Tiền Tệ", "Song Hongbing", "160000", "60", "10", "/images/sach14.webp", "Kinh tế - Kinh doanh", "Mở ra bức màn bí mật về sự vận hành của dòng tiền toàn cầu. Những thế lực ngầm đang điều khiển nền kinh tế thế giới như thế nào?"},
                    {"Từ Tốt Đến Vĩ Đại", "Jim Collins", "135000", "55", "0", "/images/sach15.webp", "Kinh tế - Kinh doanh", "Nghiên cứu kinh điển về cách các công ty bình thường vươn lên thành những đế chế vĩ đại trường tồn. Tài liệu gối đầu giường cho các CEO."},
                    {"Khởi Nghiệp Tinh Gọn", "Eric Ries", "145000", "45", "40", "/images/sach16.webp", "Kinh tế - Kinh doanh", "Phương pháp xây dựng doanh nghiệp đổi mới sáng tạo, giảm thiểu rủi ro và tiết kiệm chi phí. Bí kíp sinh tồn cho các startup hiện đại."},
                    {"Tư Duy Ngược", "Nguyễn Anh Dũng", "95000", "110", "15", "/images/sach17.jpg", "Kinh tế - Kinh doanh", "Phá vỡ những lối mòn trong tư duy thông thường. Dạy bạn cách nhìn nhận vấn đề từ nhiều góc độ khác nhau để tìm ra hướng đi đột phá."},
                    {"Những Kẻ Xuất Chúng", "Malcolm Gladwell", "155000", "65", "0", "/images/sach18.webp", "Kinh tế - Kinh doanh", "Giải mã bí mật đằng sau thành công của những vĩ nhân. Phân tích tác động của hoàn cảnh, văn hóa và quy tắc 10.000 giờ luyện tập."},

                    // Danh mục 4: Sách ngoại ngữ
                    {"Hack Não 1500 Từ Vựng", "Nguyễn Văn Hiệp", "395000", "200", "30", "/images/sach19.jpg", "Sách ngoại ngữ", "Phương pháp học từ vựng bằng âm thanh tương tự và truyện chêm, giúp não bộ ghi nhớ siêu tốc 1500 từ vựng tiếng Anh chỉ trong 50 ngày."},
                    {"Mindset For IELTS", "Cambridge", "250000", "80", "20", "/images/sach20.webp", "Sách ngoại ngữ", "Bộ giáo trình chuẩn từ Đại học Cambridge. Cung cấp nền tảng tư duy và kỹ năng toàn diện để chinh phục kỳ thi IELTS đạt điểm cao."},
                    {"Destination B1", "Malcom Mann", "180000", "75", "0", "/images/sach21.webp", "Sách ngoại ngữ", "Tài liệu ôn tập ngữ pháp và từ vựng kinh điển dành cho người học tiếng Anh trình độ trung cấp. Rất phù hợp để luyện thi THPT và chứng chỉ."},
                    {"English Grammar in Use", "Raymond Murphy", "210000", "90", "15", "/images/sach22.webp", "Sách ngoại ngữ", "Cuốn sách ngữ pháp tự học bán chạy nhất thế giới. Giải thích rõ ràng, dễ hiểu kèm bài tập thực hành áp dụng ngay lập tức."},
                    {"Tự Học Tiếng Trung Giao Tiếp", "Trang Nguyễn", "120000", "60", "25", "/images/sach23.jpg", "Sách ngoại ngữ", "Giáo trình thiết kế tối ưu cho người tự học. Tập trung vào các mẫu câu giao tiếp thông dụng hàng ngày trong đời sống và công việc."},
                    {"Oxford Thương Yêu", "Dương Thụy", "85000", "40", "10", "/images/sach24.webp", "Sách ngoại ngữ", "Câu chuyện tình yêu lãng mạn và hành trình du học đầy nỗ lực của cô gái Việt tại Đại học Oxford. Truyền cảm hứng mạnh mẽ cho giới trẻ."},

                    // Danh mục 5: Sách thiếu nhi
                    {"Harry Potter và Hòn Đá Phù Thủy", "J.K. Rowling", "150000", "100", "10", "/images/sach25.jpg", "Sách thiếu nhi", "Mở ra thế giới phép thuật kỳ diệu cùng cậu bé phù thủy Harry Potter. Cuốn sách gắn liền với tuổi thơ của hàng triệu trẻ em trên thế giới."},
                    {"Hoàng Tử Bé", "Antoine de Saint-Exupéry", "65000", "120", "0", "/images/sach26.jpg", "Sách thiếu nhi", "Câu chuyện ngụ ngôn đầy chất thơ về tình yêu, sự trưởng thành và bản chất của con người, được kể qua góc nhìn trong sáng của một cậu bé."},
                    {"Chuyện Con Mèo Dạy Hải Âu Bay", "Luis Sepúlveda", "75000", "85", "20", "/images/sach27.webp", "Sách thiếu nhi", "Tác phẩm cảm động về tình mẫu tử khác loài và tầm quan trọng của việc giữ lời hứa. Dạy trẻ em biết yêu thương và trân trọng môi trường."},
                    {"Totto-chan Bên Cửa Sổ", "Tetsuko Kuroyanagi", "95000", "70", "15", "/images/sach28.jpg", "Sách thiếu nhi", "Hồi ký về ngôi trường Tomoe kỳ diệu ở Nhật Bản. Minh chứng cho một phương pháp giáo dục tuyệt vời, tôn trọng và yêu thương trẻ em."},
                    {"Cây Cam Ngọt Của Tôi", "José Mauro", "105000", "95", "40", "/images/sach29.jpg", "Sách thiếu nhi", "Câu chuyện lấy đi nhiều nước mắt về cậu bé Zézé nghèo khó nhưng giàu trí tưởng tượng. Khắc họa vẻ đẹp của sự thấu hiểu và sẻ chia."},
                    {"Nhóc Nicolas", "René Goscinny", "80000", "50", "0", "/images/sach30.jpeg", "Sách thiếu nhi", "Tuyển tập những câu chuyện hài hước, đáng yêu về cậu nhóc Nicolas và đám bạn. Một góc nhìn hóm hỉnh về thế giới tuổi thơ ngây ngô."}
                };

                for (String[] data : bookData) {
                    Book b = new Book();
                    b.setTitle(data[0]);
                    b.setAuthor(data[1]);
                    b.setPrice(Double.parseDouble(data[2]));
                    b.setStock(Integer.parseInt(data[3]));
                    b.setDiscountPercent(Integer.parseInt(data[4]));
                    b.setImageUrl(data[5]);
                    
                    String targetCategoryName = data[6];
                    Category matchedCategory = cats.stream()
                            .filter(c -> c.getName().equals(targetCategoryName))
                            .findFirst()
                            .orElse(cats.get(0)); 
                            
                    b.setCategory(matchedCategory);

                    // SỬA Ở ĐÂY: Gán nội dung giới thiệu từ mảng vào sách
                    b.setDescription(data[7]);
                    
                    bookRepository.save(b);
                }
                System.out.println("====== ĐÃ BƠM XONG 30 CUỐN SÁCH KÈM GIỚI THIỆU CỰC CHUẨN! ======");
            }
        };
    }
}