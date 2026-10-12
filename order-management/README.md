# Order Management — Tìm kiếm đơn hàng

Dự án Maven, Java 17, Spring Boot 4.0.1, Spring Web MVC, Spring Data JPA và MySQL.

## Nội dung bài tập
- Model `Order`: @Entity, @Table(name = "orders"); Long id, String orderCode, String customerName, Double totalPrice, String status, LocalDateTime createdAt. Có constructor và getter/setter; id tự tăng.
- `OrderRepository extends JpaRepository<Order, Long>` với findByStatus(String status) và findByCustomerNameContaining(String name).
- `OrderService` có getOrdersByStatus(String status) và getOrdersByCustomerName(String name), gọi Repository qua constructor injection.
- `OrderController` cung cấp đúng 2 endpoint của đề.
- SQL thêm 20 đơn mẫu: mỗi trạng thái có 5 đơn.
- Postman collection có 9 request và assertion kiểm tra.

Các package model, repository, service, controller nằm dưới `src/main/java/com/example/orders/`.

## API
| Method | Endpoint | Query parameter |
| --- | --- | --- |
| GET | /api/v1/orders/searchStatus | status |
| GET | /api/v1/orders/searchByCustomer | customerName |

Ví dụ:
```text
http://localhost:8080/api/v1/orders/searchStatus?status=PENDING
http://localhost:8080/api/v1/orders/searchByCustomer?customerName=Minh
```
Response thành công: HTTP 200, mảng JSON. Không tìm thấy: `[]`. Thiếu query parameter bắt buộc: HTTP 400. status không tồn tại trả `[]`; bài này chưa bổ sung validation trạng thái.

Tìm tên bằng Containing (chứa chuỗi); quy tắc phân biệt hoa/thường và dấu phụ thuộc collation MySQL. Thứ tự kết quả chưa được quy định.

## Chạy dự án
1. Cài JDK 17, MySQL (ví dụ MySQL 8) và IntelliJ IDEA. Nếu chạy terminal, cần Maven 3.6.3 trở lên.
2. Giải nén, mở thư mục chứa pom.xml trong IntelliJ, chọn JDK 17 và đợi Maven tải thư viện.
3. Trong MySQL Workbench, chạy `database/01_create_database.sql`.
4. Trong Run → Edit Configurations → Environment variables của cấu hình OrderManagementApplication, đặt `DB_USERNAME` và `DB_PASSWORD` theo tài khoản MySQL.
5. URL mặc định là jdbc:mysql://localhost:3306/order_db; đặt biến `DB_URL` nếu dùng địa chỉ khác.
6. Chạy `OrderManagementApplication.java`. Hibernate dùng ddl-auto=update để tạo bảng orders trong database có sẵn.
7. Chạy `database/02_seed_orders.sql` trong MySQL Workbench để thêm 20 đơn mẫu.
8. Gọi API qua Postman theo hướng dẫn bên dưới.

Mặc định username là root, password rỗng; phải cung cấp mật khẩu đúng nếu tài khoản MySQL của bạn có mật khẩu. Không commit mật khẩu thật. Dự án không tự đọc file .env.

Hoặc sau khi đặt biến môi trường và tạo database, chạy:
```bash
mvn spring-boot:run
```
Đóng gói:
```bash
mvn clean package
java -jar target/order-management-0.0.1-SNAPSHOT.jar
```

## Dữ liệu mẫu
20 mã ORD001 đến ORD020, 5 đơn cho mỗi trạng thái PENDING, SHIPPING, DELIVERED, CANCELLED. Script bỏ qua mã đã tồn tại khi chạy lại tuần tự và không xóa dữ liệu cũ. Để kiểm tra đúng số lượng mẫu, dùng database mới chỉ có dữ liệu của bài này; script không sửa các bản ghi đã có.

Kiểm tra:
```sql
USE order_db;
SELECT COUNT(*) FROM orders;
SELECT status, COUNT(*) FROM orders GROUP BY status;
SELECT * FROM orders WHERE customer_name LIKE '%Minh%';
```
Kết quả trên database mẫu: 20 đơn; mỗi trạng thái 5 đơn; tên chứa Minh có 4 đơn.

## Test bằng Postman
1. Khởi động ứng dụng và nạp dữ liệu mẫu.
2. Mở Postman → Import → chọn `postman/Order_Management.postman_collection.json`.
3. Biến collection `baseUrl` mặc định http://localhost:8080. Đổi nếu ứng dụng dùng địa chỉ khác.
4. Mở từng request → Send → xem Test Results, hoặc dùng Collection Runner để chạy cả 9 request.
5. Không cần body hay Authorization.

| Trường hợp | Kết quả mong đợi trên dữ liệu mẫu |
| --- | --- |
| status=PENDING | 200, 5 đơn đều PENDING |
| status=SHIPPING | 200, 5 đơn đều SHIPPING |
| status=DELIVERED | 200, 5 đơn đều DELIVERED |
| status=CANCELLED | 200, 5 đơn đều CANCELLED |
| customerName=Minh | 200, 4 đơn có tên chứa Minh |
| customerName=NO_MATCH_999 | 200, [] |
| status=UNKNOWN | 200, [] |
| Thiếu status | 400 |
| Thiếu customerName | 400 |

Các assertion đếm số lượng giả định database chỉ có đúng 20 bản ghi mẫu. Thêm dữ liệu khác có thể làm kiểm tra số lượng không còn đúng.

## Đưa lên GitHub
Giải nén và đưa nội dung dự án lên repository mới trống:
```bash
git init
git add .
git commit -m "Implement order search APIs with 20 sample orders"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/order-management.git
git push -u origin main
```
Thay YOUR_USERNAME bằng tài khoản của bạn. Không chỉ upload ZIP; cần đưa các file mã nguồn đã giải nén lên repo.

## Trạng thái kiểm tra
Đã kiểm tra XML pom.xml, JSON Postman, số lượng và phân bố 20 đơn mẫu, cấu trúc mã nguồn và ZIP. Chưa chạy build, MySQL hoặc Postman thực tế trong môi trường tạo file. Bảng trên là kết quả mong đợi, không phải báo cáo test đã chạy. Hãy chạy collection trên máy của bạn sau khi cấu hình MySQL.
