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
    email VARCHAR(255),
    display_name VARCHAR(150) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'TUTOR', 'ADMIN')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'LOCKED')),
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblTutorProfile (
    user_id UUID PRIMARY KEY REFERENCES tblUser(id) ON DELETE CASCADE,
    bio TEXT,
    subjects_can_teach TEXT,
    rating NUMERIC(3, 2) DEFAULT 5.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblTutorApplication (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES tblUser(id) ON DELETE CASCADE,
    bio TEXT NOT NULL,
    subjects_can_teach TEXT NOT NULL,
    cv_url TEXT NOT NULL,
    test_submission TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    admin_note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tblBrokeragePackage (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tutor_id UUID NOT NULL REFERENCES tblUser(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
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
    note TEXT,
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
    reason TEXT NOT NULL,
    evidence_url TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    refund_amount BIGINT NOT NULL DEFAULT 0 CHECK (refund_amount >= 0),
    admin_note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

CREATE TABLE tblWithdrawal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES tblUser(id) ON DELETE CASCADE,
    wallet_id UUID NOT NULL REFERENCES tblWallet(id) ON DELETE CASCADE,
    amount BIGINT NOT NULL CHECK (amount > 0),
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_holder VARCHAR(150) NOT NULL,
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
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tblUser (id, username, password, email, display_name, role, status, phone) VALUES
('a0000000-0000-0000-0000-000000000001', 'admin', '123456', 'admin@tutortech.vn', 'Quản trị viên', 'ADMIN', 'ACTIVE', '0901111111'),
('a0000000-0000-0000-0000-000000000002', 'student01', '123456', 'student01@tutortech.vn', 'Nguyễn An', 'STUDENT', 'ACTIVE', '0912345678'),
('a0000000-0000-0000-0000-000000000003', 'tutor01', '123456', 'tutor01@tutortech.vn', 'Minh', 'TUTOR', 'ACTIVE', '0987654321'),
('a0000000-0000-0000-0000-000000000004', 'student02', '123456', 'student02@tutortech.vn', 'Trần Bình', 'STUDENT', 'ACTIVE', '0922334455');

INSERT INTO tblTutorProfile (user_id, bio, subjects_can_teach, rating) VALUES
('a0000000-0000-0000-0000-000000000003', 'Gia sư chuyên Toán cấp 3 & Luyện thi đại học đạt điểm cao', 'Toán, Hình học, Đại số', 5.00);

INSERT INTO tblWallet (id, user_id, available_balance) VALUES
('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 0),
('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 500000),
('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', 200000),
('b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000004', 100000);

INSERT INTO tblBrokeragePackage (id, tutor_id, name, description, price, active) VALUES
('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000003', 'Gói Toán nâng cao 1 kèm 1', 'Chương trình ôn thi đại học Toán chuyên sâu', 300000, true),
('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003', 'Gói Toán cơ bản (Tạm ngưng)', 'Tạm thời không nhận học viên mới', 200000, false);

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
