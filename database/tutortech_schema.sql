-- =====================================================================
-- DATABASE SCHEMA: TUTORTECH - HỆ THỐNG KẾT NỐI GIA SƯ VÀ HỌC TẬP TRỰC TUYẾN
-- Hệ quản trị cơ sở dữ liệu: PostgreSQL
-- Chuẩn thiết kế: BTL 14 Chức năng (10 bảng thực thể tbl*)
-- =====================================================================

-- Bật extension tạo UUID tự động nếu cần (pgcrypto / uuid-ossp)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Xóa bảng cũ nếu tồn tại (theo thứ tự khóa ngoại)
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

-- 1. Bảng tblUser
CREATE TABLE tblUser (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    display_name VARCHAR(150) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'TUTOR', 'ADMIN')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'LOCKED')),
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 2. Bảng tblTutorProfile (Quan hệ 1-1 với tblUser)
CREATE TABLE tblTutorProfile (
    user_id UUID PRIMARY KEY REFERENCES tblUser(id) ON DELETE CASCADE,
    bio TEXT,
    subjects_can_teach TEXT,
    rating NUMERIC(3, 2) DEFAULT 5.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 3. Bảng tblTutorApplication (Hồ sơ ứng tuyển gia sư)
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

-- 4. Bảng tblBrokeragePackage (Gói dịch vụ gia sư)
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

-- 5. Bảng tblBooking (Yêu cầu đặt lịch thuê gia sư)
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

-- 6. Bảng tblWallet (Ví người dùng)
CREATE TABLE tblWallet (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES tblUser(id) ON DELETE CASCADE,
    available_balance BIGINT NOT NULL DEFAULT 0 CHECK (available_balance >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 7. Bảng tblPaymentTransaction (Giao dịch nạp, chuyển, hoàn)
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

-- 8. Bảng tblDispute (Khiếu nại và hoàn tiền)
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

-- 9. Bảng tblWithdrawal (Yêu cầu rút tiền)
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

-- 10. Bảng tblActionLog (Nhật ký quản trị & hoạt động)
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

INSERT INTO tbluser (username, password_hash, email, display_name, role, status) 
VALUES ('admin', '123456', 'hung@gmail.com', 'admin', 'ADMIN', 'ACTIVE');