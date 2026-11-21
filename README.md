# TutorTech - Nền Tảng Kết Nối Gia Sư & Học Tập Trực Tuyến 1 Kèm 1

> **Đồ án môn học**: Phân tích & Thiết kế Hệ thống Hướng đối tượng (OOAD) / Lập trình Hướng đối tượng Java  
> **Kiến trúc áp dụng**: Mô hình **MVC (Model - View - Controller/DAO)** theo chuẩn giáo trình PTIT (*Software: Art of Design and Programming*).  
> **Công nghệ nền tảng**: Java 17+, Java Swing (FlatLaf Look & Feel), PostgreSQL, JDBC thuần, Apache Maven, JUnit 4.

---

## 📌 Mục Lục
1. [Tổng quan Đề tài](#1-tổng-quan-đề-tài)
2. [Kiến trúc Hệ thống (MVC Chuẩn PTIT)](#2-kiến-trúc-hệ-thống-mvc-chuẩn-ptit)
3. [Danh mục 17 Chức năng Nghiệp vụ (Use Cases)](#3-danh-mục-17-chức-năng-nghiệp-vụ-use-cases)
4. [Mô hình Dữ liệu (10 Bảng Thực thể PostgreSQL)](#4-mô-hình-dữ-liệu-10-bảng-thực-thể-postgresql)
5. [Quy tắc Nghiệp vụ Trọng yếu](#5-quy-tắc-nghiệp-vụ-trọng-yếu)
6. [Cấu trúc Thư mục Dự án](#6-cấu-trúc-thư-mục-dự-án)
7. [Hướng dẫn Cài đặt & Khởi chạy](#7-hướng-dẫn-cài-đặt--khởi-chạy)
8. [Tài khoản Kiểm thử Mẫu (Seed Data)](#8-tài-khoản-kiểm-thử-mẫu-seed-data)
9. [Kịch bản Demo Khép Kín Hệ Thống](#9-kịch-bản-demo-khép-kín-hệ-thống)
10. [Kiểm thử Đơn vị (Unit Test với JUnit 4)](#10-kiểm-thử-đơn-vị-unit-test-với-junit-4)

---

## 1. Tổng quan Đề tài

**TutorTech** là ứng dụng máy tính để bàn (Desktop Application) phục vụ việc kết nối học tập 1 kèm 1 giữa **Học viên** (*Student*) và **Gia sư** (*Tutor*), dưới sự điều hành và giám sát toàn diện của **Quản trị viên** (*Admin*).

Hệ thống giải quyết trọn vẹn vòng đời dịch vụ giáo dục:
* **Học viên**: Tra cứu gia sư theo môn học, đặt lịch học chống trùng lịch, nạp tiền vào ví, thanh toán học phí trực tiếp và gửi khiếu nại/yêu cầu hoàn tiền khi có sự cố.
* **Gia sư**: Thiết lập gói dịch vụ giảng dạy, tiếp nhận hoặc từ chối lịch đặt của học viên, quản lý số dư ví và tạo yêu cầu rút tiền về ngân hàng.
* **Quản trị viên**: Quản lý tài khoản người dùng, xét duyệt hồ sơ ứng tuyển làm gia sư, giám sát gói dịch vụ, thẩm định khiếu nại hoàn tiền, đối soát yêu cầu rút tiền và kiểm toán nhật ký thao tác toàn hệ thống.

---

## 2. Kiến trúc Hệ thống (MVC Chuẩn PTIT)

Hệ thống được tổ chức phân tầng rõ ràng theo mô hình **MVC 3 tầng (Model - View - Controller/DAO)**:

```
                Actor (Khách / Học viên / Gia sư / Admin)
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                           VIEW (Boundary)                           │
│ Giao diện Java Swing kế thừa JFrame/JDialog, thiết kế FlatLaf hiện đại│
│ Bắt sự kiện người dùng qua ActionListener và truyền dữ liệu cho DAO │
└──────────────────────────────────┬──────────────────────────────────┘
                                   │ Gọi xử lý nghiệp vụ
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       CONTROLLER / DAO (Control)                    │
│ Lớp cha DAO.java quản trị kết nối JDBC Connection dùng chung        │
│ 10 lớp DAO con xử lý nghiệp vụ, kiểm tra ràng buộc, PreparedStmt    │
│ và quản trị Database Transaction nguyên tử (ACID)                   │
└──────────────────┬───────────────────────────────┬──────────────────┘
                   │ Ánh xạ 1-1                    │ Truy vấn SQL
                   ▼                               ▼
┌──────────────────────────────────┐   ┌──────────────────────────────┐
│          MODEL (Entity)          │   │       CSDL POSTGRESQL        │
│ 10 lớp POJO độc lập, implement   │   │ 10 bảng quan hệ tiền tố tbl* │
│ Serializable, đầy đủ quan hệ OOP │   │ Khóa chính UUID duy nhất     │
└──────────────────────────────────┘   └──────────────────────────────┘
```

* **Model (Entity)**: Đại diện cho các đối tượng nghiệp vụ (`User`, `TutorProfile`, `Booking`, `Wallet`,...), triển khai `Serializable`, quan hệ đối tượng 1-1, 1-N đúng chuẩn sơ đồ lớp phân tích và thiết kế.
* **DAO (Control)**: Đảm nhận mọi truy vấn cơ sở dữ liệu qua `java.sql.PreparedStatement`, tự động chuyển đổi `ResultSet` sang đối tượng Model. Đảm bảo toàn vẹn giao dịch tài chính với `setAutoCommit(false)`, `commit()`, `rollback()`.
* **View (Boundary)**: Phân tách theo vai trò người dùng trong các package con:
  * `view.auth`: Màn hình Đăng nhập, Đăng ký.
  * `view.student`: Màn hình chính học viên, tìm kiếm gia sư, đặt lịch, thanh toán, nạp tiền, khiếu nại, ứng tuyển gia sư.
  * `view.tutor`: Màn hình chính gia sư, duyệt lịch đặt, quản lý gói dịch vụ, rút tiền.
  * `view.admin`: Màn hình chính quản trị, quản lý người dùng, duyệt ứng tuyển, tra cứu gói, xử lý khiếu nại, tra cứu rút tiền, tra cứu nhật ký kiểm toán.

---

## 3. Danh mục 17 Chức năng Nghiệp vụ (Use Cases)

Dự án bao phủ đầy đủ **17 ca sử dụng (CN01 – CN17)** theo đúng bản đặc tả thiết kế trong báo cáo:

| Mã CN | Tên Chức năng Nghiệp vụ | Tác tử chính | Giao diện (View) | Lớp DAO xử lý | Bảng CSDL tác động |
| :---: | :--- | :---: | :--- | :--- | :--- |
| **CN01** | Đăng ký tài khoản | Khách | `RegisterView` | `UserDAO`, `WalletDAO` | `tblUser`, `tblWallet` |
| **CN02** | Đăng nhập và đăng xuất | Tất cả | `LoginView`, `*HomeView` | `UserDAO` | `tblUser` |
| **CN03** | Tra cứu hồ sơ và gói dịch vụ gia sư | Học viên | `TutorSearchView`, `TutorDetailView` | `UserDAO`, `TutorProfileDAO`, `BrokeragePackageDAO` | `tblUser`, `tblTutorProfile`, `tblBrokeragePackage` |
| **CN04** | Đặt lịch thuê gia sư | Học viên | `BookingRequestView` | `BookingDAO`, `BrokeragePackageDAO` | `tblBooking` |
| **CN05** | Thanh toán tiền thuê gia sư trực tiếp vào ví | Học viên | `PaymentCheckoutView` | `BookingDAO`, `WalletDAO`, `PaymentTransactionDAO` | `tblBooking`, `tblWallet`, `tblPaymentTransaction` |
| **CN06** | Nộp và theo dõi hồ sơ ứng tuyển gia sư | Học viên | `TutorApplicationView` | `TutorApplicationDAO`, `ActionLogDAO` | `tblTutorApplication`, `tblActionLog` |
| **CN07** | Nạp tiền vào ví bằng cổng mô phỏng | Học viên | `WalletTopUpView`, `MockPaymentGatewayView` | `WalletDAO`, `PaymentTransactionDAO` | `tblWallet`, `tblPaymentTransaction` |
| **CN08** | Gia sư tiếp nhận và chấp nhận lịch đặt | Gia sư | `TutorBookingView` | `BookingDAO` | `tblBooking` |
| **CN09** | Quản trị người dùng và trạng thái tài khoản | Admin | `AdminUserManagementView` | `UserDAO`, `ActionLogDAO` | `tblUser`, `tblActionLog` |
| **CN10** | Quản trị xét duyệt hồ sơ ứng tuyển gia sư | Admin | `AdminApplicationReviewView` | `TutorApplicationDAO`, `UserDAO`, `TutorProfileDAO`, `ActionLogDAO` | `tblTutorApplication`, `tblUser`, `tblTutorProfile`, `tblActionLog` |
| **CN11** | Quản trị tra cứu danh sách gói gia sư | Admin | `AdminPackageInquiryView` | `BrokeragePackageDAO`, `UserDAO` | `tblBrokeragePackage`, `tblUser` |
| **CN12** | Quản trị xử lý khiếu nại và hoàn tiền | Admin | `AdminDisputeReviewView` | `DisputeDAO`, `WalletDAO`, `PaymentTransactionDAO`, `ActionLogDAO` | `tblDispute`, `tblWallet`, `tblPaymentTransaction`, `tblActionLog` |
| **CN13** | Quản trị tra cứu yêu cầu rút tiền | Admin | `AdminWithdrawalInquiryView` | `WithdrawalDAO`, `UserDAO` | `tblWithdrawal` |
| **CN14** | Quản trị tra cứu nhật ký thao tác | Admin | `AdminActionLogInquiryView` | `ActionLogDAO`, `UserDAO` | `tblActionLog` |
| **CN15** | Gia sư thiết lập và quản lý gói dịch vụ | Gia sư | `TutorPackageManagerView` | `BrokeragePackageDAO` | `tblBrokeragePackage` |
| **CN16** | Gia sư tạo yêu cầu rút tiền về ngân hàng | Gia sư | `CreateWithdrawalView` | `WithdrawalDAO`, `WalletDAO` | `tblWithdrawal`, `tblWallet` |
| **CN17** | Học viên gửi khiếu nại buổi học đã thanh toán | Học viên | `CreateDisputeView` | `DisputeDAO` | `tblDispute` |

---

## 4. Mô hình Dữ liệu (10 Bảng Thực thể PostgreSQL)

Cơ sở dữ liệu gồm **10 bảng**, chuẩn hóa khóa chính **`UUID`**, tiền tố bảng **`tbl*`** và các ràng buộc khóa ngoại chặt chẽ:

1. **`tblUser`**: Lưu thông tin định danh, tài khoản (`id`, `username`, `password`, `email`, `display_name`, `role` [STUDENT, TUTOR, ADMIN], `status` [ACTIVE, LOCKED], `phone`, `created_at`, `updated_at`).
2. **`tblTutorProfile`**: Hồ sơ chuyên môn gia sư (`user_id` PK/FK, `bio`, `subjects_can_teach`, `rating` mặc định 5.0, thời gian tạo/cập nhật).
3. **`tblTutorApplication`**: Hồ sơ ứng tuyển gia sư (`id`, `user_id`, `bio`, `subjects_can_teach`, `cv_url`, `test_submission`, `status` [PENDING, APPROVED, REJECTED], `admin_note`, `created_at`, `updated_at`).
4. **`tblBrokeragePackage`**: Gói dịch vụ gia sư tự cung cấp (`id`, `tutor_id`, `name`, `description`, `price` > 0, `active`, `created_at`, `updated_at`).
5. **`tblBooking`**: Lịch hẹn học kèm (`id`, `student_id`, `tutor_id`, `package_id`, `scheduled_at`, `duration_minutes` > 0, `note`, `status` [PENDING_TUTOR_CONFIRMATION, ACCEPTED, REJECTED, PAID, CANCELLED], `total_price` >= 0, `created_at`, `updated_at`).
6. **`tblWallet`**: Ví tiền người dùng (`id`, `user_id` UNIQUE, `available_balance` >= 0, `created_at`, `updated_at`).
7. **`tblPaymentTransaction`**: Lịch sử biến động số dư và giao dịch (`id`, `source_wallet_id`, `destination_wallet_id`, `booking_id`, `type` [DEPOSIT, TRANSFER, REFUND], `status` [PENDING, SUCCESS, FAILED], `amount` > 0, `reference_code` UNIQUE, `balance_after`, `created_at`).
8. **`tblDispute`**: Khiếu nại buổi học (`id`, `booking_id`, `created_by_id`, `reason`, `evidence_url`, `status` [PENDING, APPROVED, REJECTED], `refund_amount` >= 0, `admin_note`, `created_at`, `resolved_at`).
9. **`tblWithdrawal`**: Yêu cầu rút tiền về ngân hàng (`id`, `user_id`, `wallet_id`, `amount` > 0, `bank_name`, `account_number`, `account_holder`, `status` [PENDING, PROCESSING, COMPLETED, REJECTED], `created_at`, `processed_at`).
10. **`tblActionLog`**: Nhật ký kiểm toán thao tác quản trị (`id`, `user_id`, `action`, `target_type`, `target_id`, `ip_address`, `user_agent`, `created_at`).

---

## 5. Quy tắc Nghiệp vụ Trọng yếu

1. **Thuật toán Chống trùng lịch (Collision Detection)**: Khi học viên gửi lịch đặt, hệ thống đối chiếu khoảng thời gian $[t_{\text{start}}, t_{\text{start}} + \text{duration}]$ với tất cả lịch của gia sư đang ở trạng thái `PENDING_TUTOR_CONFIRMATION`, `ACCEPTED` hoặc `PAID`. Nếu có xung đột, đơn sẽ bị chặn ngay lập tức.
2. **Giao dịch Thanh toán Nguyên tử (Atomic Transfer)**: Khi học viên thanh toán học phí:
   * Trừ tiền ví học viên: $\text{Số dư} = \text{Số dư} - \text{Học phí}$.
   * Cộng tiền ví gia sư: $\text{Số dư} = \text{Số dư} + \text{Học phí}$.
   * Cập nhật trạng thái lịch học sang `PAID`.
   * Ghi log giao dịch `tblPaymentTransaction` loại `TRANSFER` trạng thái `SUCCESS`.
   * Tất cả thao tác thực thi trong một Database Transaction duy nhất (`commit()` / `rollback()`).
3. **Chuyển hoàn Khiếu nại (Atomic Refund)**: Khi Admin duyệt khiếu nại, tiền hoàn được trừ từ ví gia sư và trả về ví học viên kèm bản ghi `REFUND`. Nếu ví gia sư không đủ tiền, giao dịch sẽ được cảnh báo và rollback.
4. **Bảo mật và An toàn Quản trị**:
   * Hệ thống ngăn chặn Quản trị viên tự khóa tài khoản của chính mình khi đang đăng nhập.
   * Số tài khoản ngân hàng trên màn hình tra cứu rút tiền của Admin được tự động che bảo mật (dạng `••••1234`).
   * Không lưu token đăng nhập trong CSDL; trạng thái phiên được quản lý an toàn trong bộ nhớ JVM.

---

## 6. Cấu trúc Thư mục Dự án

```text
TutorTech/
├── database/
│   └── tutortech_schema.sql         # Script tạo 10 bảng CSDL & dữ liệu mẫu kiểm thử chuẩn
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── Main.java            # Điểm khởi chạy ứng dụng (Entry point Swing FlatLaf)
│   │   │   ├── model/               # 10 Lớp thực thể Entity POJO
│   │   │   │   ├── ActionLog.java
│   │   │   │   ├── Booking.java
│   │   │   │   ├── BrokeragePackage.java
│   │   │   │   ├── Dispute.java
│   │   │   │   ├── PaymentTransaction.java
│   │   │   │   ├── TutorApplication.java
│   │   │   │   ├── TutorProfile.java
│   │   │   │   ├── User.java
│   │   │   │   ├── Wallet.java
│   │   │   │   └── Withdrawal.java
│   │   │   ├── dao/                 # 11 Lớp Data Access Object (Control)
│   │   │   │   ├── DAO.java         # Lớp cơ sở kết nối JDBC Connection
│   │   │   │   ├── ActionLogDAO.java
│   │   │   │   ├── BookingDAO.java
│   │   │   │   ├── BrokeragePackageDAO.java
│   │   │   │   ├── DisputeDAO.java
│   │   │   │   ├── PaymentTransactionDAO.java
│   │   │   │   ├── TutorApplicationDAO.java
│   │   │   │   ├── TutorProfileDAO.java
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── WalletDAO.java
│   │   │   │   └── WithdrawalDAO.java
│   │   │   └── view/                # 22 Màn hình giao diện Java Swing (Boundary)
│   │   │       ├── auth/            # Màn hình Đăng nhập, Đăng ký
│   │   │       ├── student/         # Phân hệ Học viên
│   │   │       ├── tutor/           # Phân hệ Gia sư
│   │   │       └── admin/           # Phân hệ Quản trị viên
│   │   └── resources/
│   │       └── db.properties        # File cấu hình kết nối CSDL PostgreSQL
│   └── test/
│       └── java/dao/                # 17 Unit test cases (JUnit 4) bao phủ 17 Use Cases
│           ├── UserDAOTest.java
│           ├── BookingDAOTest.java
│           ├── WalletDAOTest.java
│           └── ApplicationAndDisputeDAOTest.java
├── pom.xml                          # Maven POM Dependencies & Plugins
└── README.md
```

---

## 7. Hướng dẫn Cài đặt & Khởi chạy

### Yêu cầu hệ thống:
* **JDK 17** hoặc mới hơn.
* **Apache Maven 3.8+**.
* **PostgreSQL 12+** (chạy tại cổng mặc định `5432` hoặc cổng Docker `5433`).

### Bước 1: Khởi tạo Cơ sở dữ liệu
1. Mở công cụ quản lý CSDL (`pgAdmin`, `DBeaver` hoặc terminal `psql`).
2. Tạo database mới:
   ```sql
   CREATE DATABASE tutortech;
   ```
3. Chạy toàn bộ file script:
   `database/tutortech_schema.sql`  
   *(Script sẽ tự động khởi tạo cấu trúc 10 bảng và nạp sẵn toàn bộ dữ liệu mẫu ban đầu).*

### Bước 2: Cấu hình kết nối
Kiểm tra file `src/main/resources/db.properties` và điều chỉnh cổng / tài khoản cho khớp với môi trường của bạn:
```properties
db.url=jdbc:postgresql://localhost:5432/tutortech
db.user=postgres
db.password=YOUR_PASSWORD_HERE
```

### Bước 3: Biên dịch & Khởi chạy
* **Chạy ứng dụng bằng Maven**:
  ```bash
  mvn compile exec:java
  ```
* **Chạy toàn bộ bài kiểm thử Unit Test**:
  ```bash
  mvn test
  ```
* **Chạy từ IDE (IntelliJ / NetBeans / Eclipse)**:  
  Mở project, điều hướng đến file `src/main/java/Main.java`, click chuột phải và chọn **Run 'Main.main()'**.

---

## 8. Tài khoản Kiểm thử Mẫu (Seed Data)

Tất cả các tài khoản thử nghiệm đều sử dụng mật khẩu mặc định: **`123456`**

| Tên đăng nhập | Mật khẩu | Vai trò (Role) | Tên hiển thị | Số dư ví ban đầu | Mục đích kiểm thử nghiệp vụ |
| :--- | :---: | :---: | :--- | :---: | :--- |
| **`student01`** | `123456` | **STUDENT** | Nguyễn An | **500.000 VNĐ** | Thử nghiệm Đặt lịch, Thanh toán ví (lịch DG01, DG02, DG03) |
| **`tutor01`** | `123456` | **TUTOR** | Minh | **200.000 VNĐ** | Gia sư dạy Toán, có gói GKN01 (300k), yêu cầu rút tiền RT01 |
| **`admin`** | `123456` | **ADMIN** | Quản trị viên | 0 VNĐ | Kiểm tra toàn bộ 6 phân hệ Quản trị |
| **`student02`** | `123456` | **STUDENT** | Trần Bình | **100.000 VNĐ** | Đã nộp hồ sơ HS01 chờ Admin xét duyệt lên làm gia sư |

---

## 9. Kịch bản Demo Khép Kín Hệ Thống

### Kịch bản 1: Đặt lịch học & Thanh toán học phí
1. Đăng nhập học viên: `student01` / `123456`.
2. Chọn **"Tìm kiếm gia sư & Gói học"** $\rightarrow$ Tìm "Toán" $\rightarrow$ Chọn gia sư **Minh** $\rightarrow$ Xem chi tiết và chọn gói học $\rightarrow$ Đặt lịch hẹn.
3. Đăng xuất, đăng nhập gia sư: `tutor01` / `123456`.
4. Vào **"Tiếp nhận & Xử lý yêu cầu đặt lịch"** $\rightarrow$ Chọn lịch mới $\rightarrow$ Bấm **"Chấp nhận lịch đặt"**.
5. Đăng xuất, đăng nhập lại `student01`.
6. Vào **"Lịch học & Thanh toán học phí"** $\rightarrow$ Bấm **"Thanh toán tiền học phí bằng ví"**.
7. **Kết quả**: Ví học viên trừ tiền, ví gia sư tăng tiền tương ứng, trạng thái lịch chuyển sang `PAID`.

### Kịch bản 2: Khiếu nại & Quản trị viên chuyển hoàn học phí
1. Học viên `student01` vào màn hình lịch học, chọn buổi học `PAID` $\rightarrow$ Bấm **"Khiếu nại buổi học này"** $\rightarrow$ Nhập lý do và số tiền yêu cầu hoàn lại.
2. Đăng nhập quản trị viên: `admin` / `123456`.
3. Vào **"4. Xử lý khiếu nại & Hoàn tiền học phí"** $\rightarrow$ Thẩm tra bằng chứng $\rightarrow$ Bấm **"Chấp thuận hoàn tiền"**.
4. **Kết quả**: Tiền hoàn được trừ từ ví gia sư và chuyển về ví học viên, trạng thái khiếu nại đổi thành `APPROVED`.

### Kịch bản 3: Ứng tuyển Gia sư & Xét duyệt nâng cấp tài khoản
1. Học viên `student02` nộp hồ sơ qua màn hình **"Ứng tuyển làm gia sư"**.
2. Quản trị viên `admin` vào **"2. Xét duyệt hồ sơ ứng tuyển gia sư"** $\rightarrow$ Bấm **"Chấp thuận & Cấp quyền Gia sư"**.
3. Đăng nhập lại `student02`: Hệ thống tự động chuyển vai trò thành `TUTOR` và mở trực tiếp giao diện làm việc của Gia sư.

---

## 10. Kiểm thử Đơn vị (Unit Test với JUnit 4)

Dự án bao gồm bộ kiểm thử đơn vị tích hợp kiểm tra toàn bộ 17 luồng nghiệp vụ trên tầng DAO:

```bash
mvn test
```

### Kết quả chạy kiểm thử thực tế:
```text
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running dao.ApplicationAndDisputeDAOTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
Running dao.BookingDAOTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
Running dao.UserDAOTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
Running dao.WalletDAOTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

Results:
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
