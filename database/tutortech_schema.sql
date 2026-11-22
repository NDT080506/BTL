CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DROP TABLE IF EXISTS tblActionLog CASCADE;
DROP TABLE IF EXISTS tblWithdrawal CASCADE;
DROP TABLE IF EXISTS tblDispute CASCADE;
DROP TABLE IF EXISTS tblPaymentTransaction CASCADE;
DROP TABLE IF EXISTS tblWallet CASCADE;
DROP TABLE IF EXISTS tblBooking CASCADE;
DROP TABLE IF EXISTS tblBrokeragePackage CASCADE;
DROP TABLE IF EXISTS tblTutorApplication CASCADE;
DROP TABLE IF EXISTS tblTutorProfile CASCADE;
DROP TABLE IF EXISTS tblUser CASCADE;

CREATE TABLE tblUser (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(150) NOT NULL,
    email VARCHAR(255),
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'TUTOR', 'ADMIN')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'LOCKED')),
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblTutorProfile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES tblUser(id) ON DELETE CASCADE,
    bio TEXT NOT NULL,
    subjects_can_teach TEXT NOT NULL,
    rating NUMERIC(3, 2) NOT NULL DEFAULT 5.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblTutorApplication (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES tblUser(id) ON DELETE CASCADE,
    bio TEXT NOT NULL,
    subjects_can_teach TEXT NOT NULL,
    cv_url VARCHAR(255) NOT NULL,
    test_submission TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    admin_note VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblBrokeragePackage (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tutor_id UUID NOT NULL REFERENCES tblUser(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price BIGINT NOT NULL CHECK (price > 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblBooking (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES tblUser(id) ON DELETE CASCADE,
    tutor_id UUID NOT NULL REFERENCES tblUser(id) ON DELETE CASCADE,
    package_id UUID REFERENCES tblBrokeragePackage(id) ON DELETE SET NULL,
    scheduled_at TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0),
    note VARCHAR(255),
    status VARCHAR(40) NOT NULL DEFAULT 'PENDING_TUTOR_CONFIRMATION' 
        CHECK (status IN ('PENDING_TUTOR_CONFIRMATION', 'ACCEPTED', 'REJECTED', 'PAID', 'CANCELLED')),
    total_price BIGINT NOT NULL DEFAULT 0 CHECK (total_price >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblWallet (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES tblUser(id) ON DELETE CASCADE,
    available_balance BIGINT NOT NULL DEFAULT 0 CHECK (available_balance >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblPaymentTransaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_wallet_id UUID REFERENCES tblWallet(id) ON DELETE SET NULL,
    destination_wallet_id UUID REFERENCES tblWallet(id) ON DELETE SET NULL,
    booking_id UUID REFERENCES tblBooking(id) ON DELETE SET NULL,
    type VARCHAR(30) NOT NULL CHECK (type IN ('DEPOSIT', 'TRANSFER', 'REFUND')),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    amount BIGINT NOT NULL CHECK (amount > 0),
    reference_code VARCHAR(100) NOT NULL UNIQUE,
    balance_after BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tblDispute (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL REFERENCES tblBooking(id) ON DELETE CASCADE,
    created_by_id UUID NOT NULL REFERENCES tblUser(id) ON DELETE CASCADE,
    reason VARCHAR(255) NOT NULL,
    evidence_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    refund_amount BIGINT NOT NULL DEFAULT 0 CHECK (refund_amount >= 0),
    admin_note VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

CREATE TABLE tblWithdrawal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES tblUser(id) ON DELETE CASCADE,
    wallet_id UUID NOT NULL REFERENCES tblWallet(id) ON DELETE CASCADE,
    amount BIGINT NOT NULL CHECK (amount > 0),
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_holder VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'REJECTED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP
);

CREATE TABLE tblActionLog (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES tblUser(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(50),
    target_id UUID,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tblUser (id, username, password, email, display_name, role, status, phone) VALUES
('a0000000-0000-0000-0000-000000000001', 'admin', '123456', 'admin@tutortech.vn', 'Quản trị viên', 'ADMIN', 'ACTIVE', '0901111111'),
('a0000000-0000-0000-0000-000000000002', 'student01', '123456', 'student01@tutortech.vn', 'Nguyễn An', 'STUDENT', 'ACTIVE', '0912345678'),
('a0000000-0000-0000-0000-000000000003', 'tutor01', '123456', 'tutor01@tutortech.vn', 'Minh', 'TUTOR', 'ACTIVE', '0987654321'),
('a0000000-0000-0000-0000-000000000004', 'student02', '123456', 'student02@tutortech.vn', 'Trần Bình', 'STUDENT', 'ACTIVE', '0922334455'),
('a0000000-0000-0000-0000-000000000005', 'tutor02', '123456', 'hoanglong.ielts@tutortech.vn', 'Lê Hoàng Long', 'TUTOR', 'ACTIVE', '0933112233'),
('a0000000-0000-0000-0000-000000000006', 'tutor03', '123456', 'thuha.physics@tutortech.vn', 'Trần Thu Hà', 'TUTOR', 'ACTIVE', '0944223344'),
('a0000000-0000-0000-0000-000000000007', 'tutor04', '123456', 'ducanh.chem@tutortech.vn', 'Nguyễn Đức Anh', 'TUTOR', 'ACTIVE', '0955334455'),
('a0000000-0000-0000-0000-000000000008', 'tutor05', '123456', 'quochuy.dev@tutortech.vn', 'Đặng Quốc Huy', 'TUTOR', 'ACTIVE', '0966445566'),
('a0000000-0000-0000-0000-000000000009', 'tutor06', '123456', 'phuongthao.van@tutortech.vn', 'Phạm Phương Thảo', 'TUTOR', 'ACTIVE', '0977556677'),
('a0000000-0000-0000-0000-000000000010', 'tutor07', '123456', 'mailinh.primary@tutortech.vn', 'Vũ Mai Linh', 'TUTOR', 'ACTIVE', '0988667788'),
('a0000000-0000-0000-0000-000000000011', 'tutor08', '123456', 'tuankiet.math@tutortech.vn', 'Hoàng Tuấn Kiệt', 'TUTOR', 'ACTIVE', '0999778899');

INSERT INTO tblTutorProfile (user_id, bio, subjects_can_teach, rating) VALUES
('a0000000-0000-0000-0000-000000000003', 'Gia sư chuyên Toán cấp 3 & Luyện thi đại học đạt điểm cao', 'Toán, Hình học, Đại số', 5.00),
('a0000000-0000-0000-0000-000000000005', 'Cựu du học sinh Anh, chứng chỉ IELTS 8.5 (Reading & Listening 9.0). 4 năm kinh nghiệm luyện thi IELTS và tiếng Anh giao tiếp chuẩn phản xạ tự nhiên.', 'Tiếng Anh, IELTS, Tiếng Anh Giao tiếp, TOEIC', 4.90),
('a0000000-0000-0000-0000-000000000006', 'Thủ khoa đầu ra Khoa Vật lý - ĐH Sư phạm Hà Nội. Hơn 5 năm giảng dạy kèm môn Vật lý lớp 10, 11, 12 và luyện đề thi THPT Quốc gia.', 'Vật lý, Khoa học tự nhiên, Ôn thi THPT Quốc Gia', 4.85),
('a0000000-0000-0000-0000-000000000007', 'Bác sĩ nội trú Đại học Y Dược, giải Nhất Quốc gia môn Hóa học. Phương pháp dạy dễ hiểu, ghi nhớ bản chất phản ứng, không học vẹt.', 'Hóa học, Hóa hữu cơ, Sinh học, Luyện thi khối B', 4.95),
('a0000000-0000-0000-0000-000000000008', 'Kỹ sư phần mềm Senior tại FPT Software, tốt nghiệp ĐH Bách Khoa. Dạy lập trình Java, Python, Web từ căn bản đến nâng cao, hướng dẫn làm đồ án.', 'Lập trình Java, Python, Web Development, Cấu trúc dữ liệu, OOP', 5.00),
('a0000000-0000-0000-0000-000000000009', 'Thạc sĩ Văn học ĐH Khoa học Xã hội và Nhân văn. Giúp học sinh nắm chắc dàn ý, viết văn cảm xúc và mở rộng vốn từ nghị luận xã hội.', 'Ngữ văn, Nghị luận văn học, Luyện thi vào 10, Văn học THPT', 4.80),
('a0000000-0000-0000-0000-000000000010', 'Giáo viên Tiểu học chuyên bồi dưỡng học sinh thi Toán tư duy Singapore, Kangaroo, IMAS. Phương pháp dạy sinh động, truyền cảm hứng yêu thích môn Toán.', 'Toán tiểu học, Toán tư duy, Toán Olympic, Tiếng Việt tiểu học', 4.90),
('a0000000-0000-0000-0000-000000000011', 'Giảng viên thỉnh giảng Đại học, cựu học sinh trường THPT Chuyên. Chuyên kèm Toán cao cấp A1, A2, Giải tích, Đại số tuyến tính, Xác suất thống kê.', 'Toán cao cấp, Giải tích, Đại số tuyến tính, Xác suất thống kê', 4.85);

INSERT INTO tblWallet (id, user_id, available_balance) VALUES
('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 0),
('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 500000),
('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', 200000),
('b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000004', 100000),
('b0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000005', 450000),
('b0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000006', 320000),
('b0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000007', 800000),
('b0000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000008', 1200000),
('b0000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000009', 150000),
('b0000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000010', 600000),
('b0000000-0000-0000-0000-000000000011', 'a0000000-0000-0000-0000-000000000011', 950000);

INSERT INTO tblBrokeragePackage (id, tutor_id, name, description, price, active) VALUES
('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000003', 'Gói Toán nâng cao 1 kèm 1', 'Chương trình ôn thi đại học Toán chuyên sâu', 300000, true),
('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003', 'Gói Toán cơ bản (Tạm ngưng)', 'Tạm thời không nhận học viên mới', 200000, false),
('c0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000005', 'Luyện thi IELTS cấp tốc 1 kèm 1', 'Khóa luyện thi IELTS chuyên sâu 4 kỹ năng, cam kết đầu ra 7.0+', 350000, true),
('c0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000005', 'Tiếng Anh giao tiếp công việc & phỏng vấn', 'Rèn phát âm chuẩn IPA, kỹ năng thuyết trình và phản xạ phỏng vấn', 280000, true),
('c0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000006', 'Luyện giải đề Vật lý 12 nâng cao (Mục tiêu 8+)', 'Tổng ôn trọng tâm công thức, phương pháp giải nhanh đồ thị dao động và sóng', 300000, true),
('c0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000006', 'Vật lý nền tảng lớp 10 & 11', 'Lấy lại gốc kiến thức cơ bản, chuẩn bị nền tảng vững chắc cho năm lớp 12', 220000, true),
('c0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000007', 'Bí quyết chinh phục Hóa học khối B đạt 9+', 'Phương pháp tư duy dồn chất, đồng đẳng hóa và bảo toàn nguyên tố chuyên sâu', 320000, true),
('c0000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000007', 'Bổ trợ kiến thức Hóa học THPT căn bản', 'Dành cho học sinh mất gốc, hệ thống hóa bảng tuần hoàn và phản ứng vô cơ', 200000, true),
('c0000000-0000-0000-0000-000000000009', 'a0000000-0000-0000-0000-000000000008', 'Lập trình Java & Thiết kế hướng đối tượng OOP', 'Học Java Core, Swing UI, JDBC, Hibernate và các Design Pattern thực tế', 400000, true),
('c0000000-0000-0000-0000-000000000010', 'a0000000-0000-0000-0000-000000000008', 'Nhập môn lập trình Python cho người mới bắt đầu', 'Cú pháp Python căn bản, xử lý dữ liệu và làm quen tự động hóa', 300000, true),
('c0000000-0000-0000-0000-000000000011', 'a0000000-0000-0000-0000-000000000009', 'Khóa bứt phá điểm số Ngữ Văn thi THPT Quốc gia', 'Kỹ năng phân tích tác phẩm văn xuôi, thơ trữ tình và viết đoạn văn nghị luận 200 chữ', 250000, true),
('c0000000-0000-0000-0000-000000000012', 'a0000000-0000-0000-0000-000000000009', 'Rèn kỹ năng làm văn nghị luận xã hội & tư duy phản biện', 'Xây dựng luận điểm sắc bén, dẫn chứng phong phú và tư duy mở rộng', 220000, true),
('c0000000-0000-0000-0000-000000000013', 'a0000000-0000-0000-0000-000000000010', 'Toán tư duy Singapore cho học sinh lớp 3-5', 'Phát triển tư duy hình ảnh (Bar Modeling) và giải toán logic', 280000, true),
('c0000000-0000-0000-0000-000000000014', 'a0000000-0000-0000-0000-000000000010', 'Kèm Toán & Tiếng Việt tiểu học lấy lại gốc', 'Củng cố phép tính, rèn chính tả và tập làm văn lớp 1 đến lớp 5', 180000, true),
('c0000000-0000-0000-0000-000000000015', 'a0000000-0000-0000-0000-000000000011', 'Chinh phục Toán cao cấp & Giải tích Đại học', 'Hệ thống hóa ma trận, định thức, không gian vector, tích phân suy rộng và chuỗi số', 350000, true),
('c0000000-0000-0000-0000-000000000016', 'a0000000-0000-0000-0000-000000000011', 'Cấp tốc Xác suất thống kê cho sinh viên', 'Bí quyết làm bài thi trắc nghiệm và tự luận môn Xác suất thống kê đạt điểm A', 300000, true);

INSERT INTO tblBooking (id, student_id, tutor_id, package_id, scheduled_at, duration_minutes, note, status, total_price) VALUES
('d0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000001', '2030-01-01 08:00:00', 60, 'Lịch học Toán buổi sáng (DG01)', 'ACCEPTED', 300000),
('d0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000001', '2030-01-02 09:00:00', 60, 'Lịch học chờ xác nhận (DG02)', 'PENDING_TUTOR_CONFIRMATION', 300000),
('d0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000001', '2030-01-03 14:00:00', 60, 'Lịch học đã thanh toán trước (DG03)', 'PAID', 300000);

INSERT INTO tblTutorApplication (id, user_id, bio, subjects_can_teach, cv_url, test_submission, status) VALUES
('e0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000004', 'Sinh viên ĐH Sư phạm Hà Nội, giải Nhì Toán quốc gia', 'Toán THCS, Toán THPT', 'https://cv.tutortech.vn/tranbinh_cv.pdf', 'Đã hoàn thành bài kiểm tra môn Toán 95/100', 'PENDING'),
('e0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000004', 'Gia sư tự do kinh nghiệm 1 năm', 'Toán tiểu học', 'https://cv.tutortech.vn/cv2.pdf', 'Điểm bài test: 40/100', 'PENDING');

INSERT INTO tblDispute (id, booking_id, created_by_id, reason, evidence_url, status, refund_amount) VALUES
('f0000000-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002', 'Gia sư vào lớp muộn 20 phút và thiết bị âm thanh rè', 'https://evidence.tutortech.vn/kn01_screenshot.png', 'PENDING', 100000);

INSERT INTO tblWithdrawal (id, user_id, wallet_id, amount, bank_name, account_number, account_holder, status) VALUES
('80000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000003', 100000, 'Vietcombank', '0123456789', 'NGUYEN VAN MINH', 'PENDING');

INSERT INTO tblActionLog (id, user_id, action, target_type, target_id, ip_address, user_agent) VALUES
('90000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'REVIEW_TUTOR_APPLICATION', 'TutorApplication', 'e0000000-0000-0000-0000-000000000001', '127.0.0.1', 'TutorTech-Swing-Client/1.0');
