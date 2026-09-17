CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_name VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    status VARCHAR(20) NOT NULL,
    protocol VARCHAR(100) NULL,
    attempts INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_attendance_status (status),
    INDEX idx_attendance_cpf_status (cpf, status)
);
