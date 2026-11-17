# TUTORTECH - NỀN TẢNG KẾT NỐI GIA SƯ VÀ HỌC TẬP TRỰC TUYẾN

> **Báo cáo Bài tập lớn**: Phân tích & Thiết kế Hệ thống Hướng đối tượng (OOAD) / Lập trình Hướng đối tượng Java  
> **Kiến trúc áp dụng**: Mô hình **MVC (Model - View - Controller/DAO)** theo chuẩn tài liệu *Software: Art of Design and Programming* (Thầy Nguyễn Mạnh Hùng - PTIT).  
> **Công nghệ**: Java 17+, Java Swing (UI với FlatLaf), PostgreSQL, JDBC, Apache Maven, JUnit 4.

---

## 📌 MỤC LỤC
1. [Giới thiệu Đề tài & Mục tiêu](#1-giới-thiệu-đề-tài--mục-tiêu)
2. [Kiến trúc & Phong cách Thiết kế](#2-kiến-trúc--phong-cách-thiết-kế)
3. [Danh sách 14 Chức năng Nghiệp vụ](#3-danh-sách-14-chức-năng-nghiệp-vụ)
4. [Mô hình Dữ liệu (10 Bảng Entity CSDL)](#4-mô-hình-dữ-liệu-10-bảng-entity-csdl)
5. [Các Quy tắc Nghiệp vụ Cốt lõi](#5-các-quy-tắc-nghiệp-vụ-cốt-lõi)
6. [Cấu trúc Thư mục Dự án](#6-cấu-trúc-thư-mục-dự-án)
7. [Hướng dẫn Cài đặt & Khởi chạy](#7-hướng-dẫn-cài-đặt--khởi-chạy)
8. [Tài khoản Mẫu để Demo (Seed Data)](#8-tài-khoản-mẫu-để-demo-seed-data)
9. [Kịch bản Demo Khép kín Toàn hệ thống](#9-kịch-bản-demo-khép-kín-toàn-hệ-thống)

---

## 1. GIỚI THIỆU ĐỀ TÀI & MỤC TIÊU

**TutorTech** là hệ thống kết nối trực tuyến giữa **Học viên** (*Student*) có nhu cầu tìm kiếm người dạy kèm và **Gia sư** (*Tutor*) cung cấp các gói dịch vụ học tập. Toàn bộ hoạt động được quản lý, kiểm soát và vận hành bởi **Quản trị viên** (*Admin*).

### Phạm vi và Mục tiêu chính:
- **Học viên**: Tra cứu gia sư theo môn học, xem chi tiết hồ sơ & gói dịch vụ, nạp tiền vào ví, đặt lịch học, thanh toán học phí trực tiếp vào ví gia sư, nộp hồ sơ ứng tuyển làm gia sư, gửi khiếu nại buổi học nếu có sự cố.
- **Gia sư**: Tiếp nhận các yêu cầu đặt lịch, chấp nhận (`ACCEPTED`) hoặc từ chối (`REJECTED`), nhận tiền thanh toán trực tiếp vào ví, tạo và quản lý các gói dịch vụ dạy kèm, gửi yêu cầu rút tiền về tài khoản ngân hàng.
- **Quản trị viên**: Khóa/mở khóa tài khoản người dùng, xét duyệt hồ sơ ứng tuyển gia sư (thăng cấp role thành `TUTOR` và tạo `TutorProfile`), xử lý khiếu nại & hoàn tiền học phí, tra cứu gói dịch vụ, tra cứu yêu cầu rút tiền (che số tài khoản bảo mật), tra cứu nhật ký thao tác kiểm toán (`ActionLog`).

### Các thành phần ĐÃ LOẠI BỎ (đúng theo thiết kế bài báo cáo):
- ❌ Không làm các bảng quản lý nội dung video, khóa học, bài học (`Course`, `Lesson`).
- ❌ Không tạo bảng phiên đăng nhập, JWT hay token trong CSDL (`SessionToken`, `InvalidatedToken`). Trạng thái đăng nhập `currentUser` chỉ lưu trữ trong bộ nhớ tạm thời của ứng dụng.
- ❌ Không gửi email xác minh (email chỉ lưu làm kênh liên hệ khi cần).
- ❌ Không sử dụng ví trung gian hoặc tài khoản ký quỹ (Escrow) khi thanh toán; tiền chuyển trực tiếp từ ví học viên sang ví gia sư.

---

## 2. KIẾN TRÚC & PHONG CÁCH THIẾT KẾ

Hệ thống được xây dựng bám sát 100% tài liệu hướng dẫn thiết kế & lập trình Java Swing:

```
  Actor (User)
       │
       ▼
 [View / Boundary]   (JFrame / JDialog, bắt sự kiện actionPerformed)
       │
       ▼
 [DAO / Control]     (UserDAO, BookingDAO, WalletDAO,... kế thừa DAO cơ sở)
       │
       ├─────────────────────────┐
       ▼                         ▼
[Model / Entity]        [CSDL PostgreSQL]
(User, Booking,...)     (tblUser, tblBooking,...)
```

- **`model` (Entity)**: POJO thuần túy, đại diện cho thực thể miền, ánh xạ 1–1 với các bảng `tbl*`, implement `Serializable`, đầy đủ getter/setter/constructor.
- **`dao` (Control)**: 
  - Lớp cơ sở `DAO.java` nạp driver JDBC và quản lý kết nối `Connection con` tĩnh dùng chung.
  - Các lớp DAO nghiệp vụ kế thừa `DAO`, sử dụng `PreparedStatement` chống SQL Injection.
  - Các thao tác tài chính (thanh toán giữa 2 ví, hoàn tiền) được xử lý **nguyên tử** (Atomic Transaction) bằng `con.setAutoCommit(false)`, `con.commit()`, `con.rollback()`.
- **`view` (Boundary)**:
  - Sử dụng Java Swing (`JFrame` cho trang chính, `JDialog` cho form/modal), kết hợp thư viện giao diện hiện đại **FlatLaf**.
  - Các sự kiện người dùng gọi trực tiếp hàm của DAO, nhận về Entity và cập nhật bảng (`JTable`) hoặc hiển thị hộp thoại `JOptionPane`.
  - Quản lý phiên: Đối tượng `User currentUser` được giữ trong bộ nhớ và xóa khi ấn Đăng xuất.

---

## 3. DANH SÁCH 14 CHỨC NĂNG NGHIỆP VỤ

| Mã CN | Tên Chức năng | Phân hệ (Actor) | Lớp Giao diện (View) | Lớp DAO xử lý |
| :--- | :--- | :--- | :--- | :--- |
| **CN01.03** | Đăng ký tài khoản mới | Khách truy cập | `RegisterFrm` | `UserDAO`, `WalletDAO` |
| **CN01.04** | Đăng nhập & Đăng xuất | Người dùng | `LoginFrm`, `MainFrm` | `UserDAO` |
| **CN02.03** | Tra cứu hồ sơ & gói dịch vụ gia sư | Học viên | `TutorSearchFrm`, `TutorDetailFrm` | `UserDAO`, `TutorProfileDAO`, `BrokeragePackageDAO` |
| **CN02.07** | Đặt lịch thuê gia sư | Học viên | `BookingRequestFrm` | `BookingDAO`, `BrokeragePackageDAO`, `UserDAO` |
| **CN02.08** | Thanh toán tiền thuê gia sư trực tiếp vào ví | Học viên | `PaymentCheckoutFrm` | `BookingDAO`, `WalletDAO`, `PaymentTransactionDAO` |
| **CN02.13** | Nộp và theo dõi hồ sơ ứng tuyển gia sư | Học viên | `TutorApplicationFrm` | `TutorApplicationDAO`, `ActionLogDAO` |
| **CN02.14** | Nạp tiền vào ví bằng cổng mô phỏng | Học viên | `WalletTopUpFrm`, `MockPaymentGatewayFrm` | `WalletDAO`, `PaymentTransactionDAO` |
| **CN04.01** | Gia sư tiếp nhận và chấp nhận lịch đặt | Gia sư | `TutorBookingFrm` | `BookingDAO`, `UserDAO` |
| **CN06.01** | Quản trị người dùng & trạng thái tài khoản | Quản trị viên | `AdminUserManagementFrm` | `UserDAO`, `ActionLogDAO` |
| **CN06.02** | Quản trị xét duyệt hồ sơ ứng tuyển gia sư | Quản trị viên | `AdminApplicationReviewFrm` | `TutorApplicationDAO`, `UserDAO`, `TutorProfileDAO`, `ActionLogDAO` |
| **CN06.03** | Quản trị tra cứu danh sách gói gia sư | Quản trị viên | `AdminPackageInquiryFrm` | `BrokeragePackageDAO`, `UserDAO` |
| **CN06.05** | Quản trị xử lý khiếu nại và hoàn tiền | Quản trị viên | `AdminDisputeReviewFrm` | `DisputeDAO`, `BookingDAO`, `WalletDAO`, `PaymentTransactionDAO`, `ActionLogDAO` |
| **CN06.06** | Quản trị tra cứu yêu cầu rút tiền | Quản trị viên | `AdminWithdrawalInquiryFrm` | `WithdrawalDAO`, `UserDAO`, `WalletDAO` |
| **CN06.07** | Quản trị tra cứu nhật ký thao tác | Quản trị viên | `AdminActionLogInquiryFrm` | `ActionLogDAO`, `UserDAO` |

---

## 4. MÔ HÌNH DỮ LIỆU (10 BẢNG ENTITY CSDL)

Toàn bộ CSDL sử dụng tiền tố **`tbl`**, khóa chính chuẩn **`UUID`**:

1. **`tblUser`**: Lưu trữ tài khoản (`id`, `username`, `password_hash`, `email`, `display_name`, `role` [STUDENT, TUTOR, ADMIN], `status` [ACTIVE, LOCKED], `phone`, `created_at`, `updated_at`).
2. **`tblTutorProfile`**: Hồ sơ chuyên môn gia sư (`user_id` PK/FK quan hệ 1–1 với `tblUser`, `bio`, `subjects_can_teach`, `rating` mặc định 5.00).
3. **`tblTutorApplication`**: Hồ sơ ứng tuyển gia sư (`id`, `user_id`, `bio`, `subjects_can_teach`, `cv_url`, `test_submission`, `status` [PENDING, APPROVED, REJECTED], `admin_note`).
4. **`tblBrokeragePackage`**: Gói dịch vụ kết nối gia sư (`id`, `tutor_id`, `name`, `description`, `price` > 0, `active`).
5. **`tblBooking`**: Yêu cầu đặt lịch học (`id`, `student_id`, `tutor_id`, `package_id`, `scheduled_at`, `duration_minutes` > 0, `note`, `status` [PENDING_TUTOR_CONFIRMATION, ACCEPTED, REJECTED, PAID, CANCELLED], `total_price` >= 0).
6. **`tblWallet`**: Ví tiền người dùng (`id`, `user_id` UNIQUE, `available_balance` >= 0).
7. **`tblPaymentTransaction`**: Giao dịch biến động tiền (`id`, `source_wallet_id`, `destination_wallet_id`, `booking_id`, `type` [DEPOSIT, TRANSFER, REFUND], `status` [PENDING, SUCCESS, FAILED], `amount` > 0, `reference_code` UNIQUE, `balance_after`).
8. **`tblDispute`**: Khiếu nại buổi học & hoàn tiền (`id`, `booking_id`, `created_by_id`, `reason`, `evidence_url`, `status` [PENDING, APPROVED, REJECTED], `refund_amount` >= 0, `admin_note`).
9. **`tblWithdrawal`**: Đề nghị rút tiền (`id`, `user_id`, `wallet_id`, `amount` > 0, `bank_name`, `account_number`, `account_holder`, `status` [PENDING, PROCESSING, COMPLETED, REJECTED]).
10. **`tblActionLog`**: Nhật ký kiểm toán hệ thống (`id`, `user_id`, `action`, `target_type`, `target_id`, `ip_address`, `user_agent`, `created_at`).

---

## 5. CÁC QUY TẮC NGHIỆP VỤ CỐT LÕI

1. **Đăng ký tài khoản (CN01.03)**: Kiểm tra `username` duy nhất. Không gửi email xác thực; tài khoản tạo xong là có thể đăng nhập ngay và tự động được cấp 1 ví ban đầu với số dư 0đ.
2. **Đăng nhập & Phiên (CN01.04)**: Kiểm tra `username`, `password` và `status != 'LOCKED'`. Thông tin người dùng lưu tạm thời trong biến `currentUser` ở bộ nhớ RAM. Khi đăng xuất thì gán `currentUser = null` và đóng cửa sổ.
3. **Đặt lịch học (CN02.07)**: Kiểm tra thời gian học không ở quá khứ và không trùng với các lịch đã xác nhận của gia sư (`isScheduleAvailable`). Trạng thái ban đầu là `PENDING_TUTOR_CONFIRMATION`. **Chưa trừ tiền ví học viên tại bước này.**
4. **Thanh toán học phí trực tiếp (CN02.08)**: Chỉ thanh toán khi lịch ở trạng thái `ACCEPTED`. Trừ ví học viên, cộng ví gia sư và cập nhật trạng thái `PAID` trong **cùng 1 Transaction**; nếu có lỗi lập tức rollback toàn bộ. Không dùng ví trung gian / ký quỹ.
5. **Xét duyệt hồ sơ gia sư (CN06.02)**: Chỉ quản trị viên mới có quyền duyệt. Khi bấm Chấp thuận (`APPROVE`), hệ thống đồng thời: cập nhật hồ sơ `APPROVED`, chuyển vai trò `User.role` thành `TUTOR`, tạo bản ghi `TutorProfile` mới và ghi nhận `ActionLog`.
6. **Xử lý khiếu nại (CN06.05)**: Nếu quản trị viên chấp thuận khiếu nại, tiền hoàn được chuyển trực tiếp từ ví gia sư về ví học viên (yêu cầu ví gia sư phải đủ số dư hoàn).
7. **Bảo mật rút tiền (CN06.06)**: Màn hình danh sách tra cứu rút tiền luôn che một phần số tài khoản (dạng `••••1234`).
8. **Quy tắc an toàn quản trị (CN06.01)**: Quản trị viên không được phép tự khóa tài khoản của chính mình đang thực hiện thao tác.

---

## 6. CẤU TRÚC THƯ MỤC DỰ ÁN

```text
TutorTech/
├── database/
│   └── tutortech_schema.sql         # Script tạo CSDL PostgreSQL và dữ liệu mẫu
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── Main.java            # Điểm khởi chạy ứng dụng (Entry point)
│   │   │   ├── model/               # 10 Lớp thực thể (Entity Model)
│   │   │   │   ├── User.java
│   │   │   │   ├── TutorProfile.java
│   │   │   │   ├── TutorApplication.java
│   │   │   │   ├── BrokeragePackage.java
│   │   │   │   ├── Booking.java
│   │   │   │   ├── Wallet.java
│   │   │   │   ├── PaymentTransaction.java
│   │   │   │   ├── Dispute.java
│   │   │   │   ├── Withdrawal.java
│   │   │   │   └── ActionLog.java
│   │   │   ├── dao/                 # 10 Lớp truy xuất CSDL (Data Access Object)
│   │   │   │   ├── DAO.java         # Base DAO quản lý Connection JDBC
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── TutorProfileDAO.java
│   │   │   │   ├── TutorApplicationDAO.java
│   │   │   │   ├── BrokeragePackageDAO.java
│   │   │   │   ├── BookingDAO.java
│   │   │   │   ├── WalletDAO.java
│   │   │   │   ├── PaymentTransactionDAO.java
│   │   │   │   ├── DisputeDAO.java
│   │   │   │   ├── WithdrawalDAO.java
│   │   │   │   └── ActionLogDAO.java
│   │   │   └── view/                # Tầng giao diện người dùng Java Swing
│   │   │       ├── auth/
│   │   │       │   ├── LoginFrm.java
│   │   │       │   └── RegisterFrm.java
│   │   │       ├── student/
│   │   │       │   ├── StudentHomeFrm.java
│   │   │       │   ├── TutorSearchFrm.java
│   │   │       │   ├── TutorDetailFrm.java
│   │   │       │   ├── BookingRequestFrm.java
│   │   │       │   ├── PaymentCheckoutFrm.java
│   │   │       │   ├── CreateDisputeFrm.java
│   │   │       │   ├── WalletTopUpFrm.java
│   │   │       │   ├── MockPaymentGatewayFrm.java
│   │   │       │   └── TutorApplicationFrm.java
│   │   │       ├── tutor/
│   │   │       │   ├── TutorHomeFrm.java
│   │   │       │   ├── TutorBookingFrm.java
│   │   │       │   ├── TutorPackageManagerFrm.java
│   │   │       │   └── CreateWithdrawalFrm.java
│   │   │       └── admin/
│   │   │           ├── AdminHomeFrm.java
│   │   │           ├── AdminUserManagementFrm.java
│   │   │           ├── AdminApplicationReviewFrm.java
│   │   │           ├── AdminPackageInquiryFrm.java
│   │   │           ├── AdminDisputeReviewFrm.java
│   │   │           ├── AdminWithdrawalInquiryFrm.java
│   │   │           └── AdminActionLogInquiryFrm.java
│   │   └── resources/
│   │       └── db.properties        # Cấu hình kết nối CSDL PostgreSQL
│   └── test/
│       └── java/dao/                # Kiểm thử đơn vị (JUnit 4) cho tầng DAO
│           ├── UserDAOTest.java
│           ├── BookingDAOTest.java
│           └── WalletDAOTest.java
├── pom.xml                          # File cấu hình Maven Dependencies & Plugins
└── README.md
```

---

## 7. HƯỚNG DẪN CÀI ĐẶT & KHỞI CHẠY

### Điều kiện tiên quyết:
- **JDK 17** trở lên.
- **Apache Maven 3.8+**.
- **PostgreSQL 12+** (đã cài đặt và đang chạy dịch vụ tại cổng `5432`).

### Bước 1: Khởi tạo Cơ sở dữ liệu PostgreSQL
1. Mở `pgAdmin` hoặc công cụ dòng lệnh `psql`.
2. Tạo database mới tên là `tutortech`:
   ```sql
   CREATE DATABASE tutortech;
   ```
3. Chạy toàn bộ file kịch bản CSDL tại `database/tutortech_schema.sql` vào database `tutortech`. (File này đã bao gồm bảng và dữ liệu mẫu đầy đủ).

### Bước 2: Cấu hình kết nối Cơ sở dữ liệu
Mở file `src/main/resources/db.properties` và điều chỉnh lại username/mật khẩu PostgreSQL của máy bạn:
```properties
db.url=jdbc:postgresql://localhost:5432/tutortech
db.user=postgres
db.password=postgres
```

### Bước 3: Biên dịch & Khởi chạy ứng dụng
Mở Terminal tại thư mục gốc dự án:

- **Cách 1: Chạy bằng Maven**
  ```powershell
  mvn compile exec:java
  ```
- **Cách 2: Chạy trực tiếp từ IDE**
  Mở thư mục dự án bằng **IntelliJ IDEA**, **Eclipse** hoặc **VS Code**, tìm đến file `src/main/java/Main.java`, click chuột phải chọn **Run 'Main.main()'**.

- **Cách 3: Chạy các bài kiểm thử đơn vị JUnit**
  ```powershell
  mvn test
  ```

---

## 8. TÀI KHOẢN MẪU ĐỂ DEMO (SEED DATA)

Mọi tài khoản mẫu đều có mật khẩu mặc định là: **`123456`**

| Tên đăng nhập (Username) | Mật khẩu | Vai trò (Role) | Tên hiển thị | Số dư ví ban đầu |
| :--- | :--- | :--- | :--- | :--- |
| **`admin`** | `123456` | **ADMIN** | Quản Trị Viên Hệ Thống | - |
| **`tutor01`** | `123456` | **TUTOR** | Thầy Nguyễn Minh (Toán, Lý) | 2.000.000 VNĐ |
| **`tutor02`** | `123456` | **TUTOR** | Cô Trần Lan (Tiếng Anh) | 1.200.000 VNĐ |
| **`student01`** | `123456` | **STUDENT** | Bạn Nguyễn An | 1.500.000 VNĐ |
| **`student02`** | `123456` | **STUDENT** | Bạn Lê Bình | 800.000 VNĐ |

---

## 9. KỊCH BẢN DEMO KHÉP KÍN TOÀN HỆ THỐNG

### Kịch bản 1: Học viên tìm gia sư, đặt lịch và thanh toán trực tiếp
1. Đăng nhập với tài khoản `student01` / `123456`.
2. Chọn **"1. Tìm kiếm gia sư & Gói học"** -> Tìm theo môn hoặc tên -> Bấm **"Xem hồ sơ chi tiết"**.
3. Xem thông tin gia sư, chọn gói học -> Bấm **"Đặt lịch với gói này"** -> Nhập ngày giờ học -> Bấm **"Xác nhận gửi yêu cầu"** (tạo booking `PENDING_TUTOR_CONFIRMATION`).
4. Đăng xuất `student01`, đăng nhập tài khoản Gia sư `tutor01` / `123456`.
5. Gia sư vào **"1. Tiếp nhận & Xử lý yêu cầu đặt lịch"** -> Thấy booking mới -> Bấm **"Chấp nhận lịch đặt"** (chuyển sang `ACCEPTED`).
6. Đăng xuất gia sư, đăng nhập lại `student01`.
7. Học viên vào **"2. Lịch học & Thanh toán học phí"** -> Chọn lịch đã được chấp nhận -> Bấm **"Thanh toán tiền học phí bằng ví"**. Tiền trừ thẳng ví học viên và cộng vào ví gia sư, booking đổi sang `PAID`.

### Kịch bản 2: Học viên nộp đơn ứng tuyển làm Gia sư -> Admin phê duyệt
1. Đăng nhập với tài khoản `student01`.
2. Chọn **"4. Ứng tuyển làm gia sư"** -> Điền môn dạy, link CV, bài test, bio -> Bấm **"Gửi hồ sơ ứng tuyển"**.
3. Đăng xuất, đăng nhập tài khoản Quản trị viên `admin` / `123456`.
4. Admin vào **"2. Xét duyệt hồ sơ ứng tuyển gia sư"** -> Thấy hồ sơ đang chờ (`PENDING`) -> Xem chi tiết và bấm **"Chấp thuận & Cấp quyền Gia sư"**.
5. Đăng xuất Admin, đăng nhập lại `student01`. Lúc này hệ thống nhận diện tài khoản đã thăng cấp thành `TUTOR` và tự động chuyển thẳng vào giao diện của Gia sư!

### Kịch bản 3: Nạp tiền ví qua Cổng thanh toán mô phỏng (Mock Payment)
1. Đăng nhập với tài khoản `student02` (số dư ban đầu 800.000đ).
2. Vào **"3. Nạp tiền vào ví"** -> Chọn nạp 500.000đ -> Bấm **"Tiếp tục thanh toán"**.
3. Màn hình Cổng thanh toán mô phỏng hiện lên -> Chọn **"Giao dịch THÀNH CÔNG"** -> Bấm **"Xác nhận kết quả"**.
4. Tiền được cộng ngay vào ví: 800.000đ + 500.000đ = 1.300.000đ.

### Kịch bản 4: Khiếu nại buổi học & Quản trị viên hoàn tiền
1. Học viên vào lịch học đã thanh toán (`PAID`), chọn buổi học và bấm **"Khiếu nại buổi học"** -> Nhập lý do (ví dụ: gia sư không vào dạy) -> Bấm **"Gửi khiếu nại"**.
2. Đăng nhập tài khoản `admin` -> Vào **"4. Xử lý khiếu nại & Hoàn tiền học phí"**.
3. Admin xem chi tiết bằng chứng và số dư ví gia sư -> Bấm **"Chấp thuận & Hoàn tiền về ví học viên"**.
4. Tiền được tự động trừ từ ví gia sư và hoàn lại đủ vào ví học viên.

### Kịch bản 5: Rút tiền & Tra cứu nhật ký kiểm toán (Audit Log)
1. Gia sư vào **"3. Yêu cầu rút tiền về ngân hàng"** -> Nhập số tiền muốn rút và thông tin thẻ ngân hàng -> Gửi yêu cầu.
2. Admin đăng nhập -> Vào **"5. Tra cứu yêu cầu rút tiền"** để xem thông tin (số thẻ đã che bảo mật dạng `••••1234`).
3. Admin vào **"6. Tra cứu nhật ký thao tác kiểm toán"** để kiểm tra lịch sử tất cả các hành động vừa diễn ra trong hệ thống (`APPLY_TUTOR`, `UPDATE_USER_STATUS`, `REVIEW_TUTOR_APPLICATION`, `RESOLVE_DISPUTE`...).
