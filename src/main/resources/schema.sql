-- ============================================================================
-- SmartHire - Campus Placement and Recruitment Management System Schema
-- Target Database: MySQL 8.0+
-- Engine: InnoDB | Character Set: utf8mb4 | Collation: utf8mb4_unicode_ci
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `smarthire_db`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `smarthire_db`;

-- ----------------------------------------------------------------------------
-- 1. Table: users
-- Core authentication and role-based access control table
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` ENUM('STUDENT', 'TPO_ADMIN', 'RECRUITER') NOT NULL,
    `status` ENUM('ACTIVE', 'INACTIVE', 'PENDING_APPROVAL') NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_role` (`role`),
    INDEX `idx_users_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 2. Table: student_profiles
-- Comprehensive academic and demographic records of candidates
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `student_profiles` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL UNIQUE,
    `roll_number` VARCHAR(50) NOT NULL UNIQUE,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20),
    `gender` ENUM('MALE', 'FEMALE', 'OTHER'),
    `department` VARCHAR(100) NOT NULL, -- e.g., 'CSE', 'IT', 'ECE', 'MECH', 'CIVIL'
    `cgpa` DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    `tenth_percentage` DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    `twelfth_percentage` DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    `active_backlogs` INT NOT NULL DEFAULT 0,
    `total_backlogs_history` INT NOT NULL DEFAULT 0,
    `graduation_year` INT NOT NULL,
    `resume_file_path` VARCHAR(500),
    `skills` TEXT, -- Comma-separated or JSON list of key skills
    `is_verified` BOOLEAN NOT NULL DEFAULT FALSE,
    `verified_at` DATETIME,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) 
        REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_student_dept` (`department`),
    INDEX `idx_student_cgpa` (`cgpa`),
    INDEX `idx_student_backlogs` (`active_backlogs`),
    INDEX `idx_student_grad_year` (`graduation_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 3. Table: companies
-- Registered hiring organizations and recruiter profiles
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `companies` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL UNIQUE,
    `company_name` VARCHAR(200) NOT NULL,
    `industry` VARCHAR(100),
    `website` VARCHAR(255),
    `description` TEXT,
    `headquarters` VARCHAR(150),
    `contact_person_name` VARCHAR(150),
    `contact_phone` VARCHAR(20),
    `is_verified` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_company_user` FOREIGN KEY (`user_id`) 
        REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_company_name` (`company_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 4. Table: job_postings (Campus Recruitment Drives)
-- Job listings posted by recruiters with detailed eligibility criteria
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `job_postings` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `company_id` INT NOT NULL,
    `job_title` VARCHAR(200) NOT NULL,
    `job_description` TEXT NOT NULL,
    `job_location` VARCHAR(150) NOT NULL,
    `employment_type` ENUM('FULL_TIME', 'INTERNSHIP', 'INTERN_TO_FTE') NOT NULL DEFAULT 'FULL_TIME',
    `package_lpa` DECIMAL(6, 2) NOT NULL, -- Annual package in LPA (e.g. 12.50)
    `stipend_monthly` DECIMAL(8, 2) DEFAULT 0.00,
    
    -- Eligibility Rules Checked by Application Engine
    `min_cgpa` DECIMAL(3, 2) NOT NULL DEFAULT 6.00,
    `min_tenth_percentage` DECIMAL(5, 2) NOT NULL DEFAULT 60.00,
    `min_twelfth_percentage` DECIMAL(5, 2) NOT NULL DEFAULT 60.00,
    `max_backlogs_allowed` INT NOT NULL DEFAULT 0,
    `eligible_branches` VARCHAR(255) NOT NULL DEFAULT 'ALL', -- e.g. 'CSE,IT,ECE' or 'ALL'
    `graduation_year` INT NOT NULL,
    
    `application_deadline` DATETIME NOT NULL,
    `drive_date` DATETIME NOT NULL,
    `status` ENUM('DRAFT', 'OPEN', 'CLOSED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'OPEN',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_job_company` FOREIGN KEY (`company_id`) 
        REFERENCES `companies` (`id`) ON DELETE CASCADE,
    INDEX `idx_job_status` (`status`),
    INDEX `idx_job_deadline` (`application_deadline`),
    INDEX `idx_job_cgpa` (`min_cgpa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 5. Table: job_applications
-- Tracks candidate applications and overall selection outcome
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `job_applications` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `job_id` INT NOT NULL,
    `student_id` INT NOT NULL,
    `applied_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `current_status` ENUM(
        'APPLIED', 
        'SHORTLISTED', 
        'IN_PROCESS', 
        'REJECTED', 
        'OFFERED', 
        'ACCEPTED', 
        'DECLINED'
    ) NOT NULL DEFAULT 'APPLIED',
    `rejection_reason` VARCHAR(255),
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_app_job` FOREIGN KEY (`job_id`) 
        REFERENCES `job_postings` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_app_student` FOREIGN KEY (`student_id`) 
        REFERENCES `student_profiles` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uq_job_student` UNIQUE (`job_id`, `student_id`),
    INDEX `idx_app_status` (`current_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 6. Table: selection_rounds
-- Specific interview rounds configured for a placement drive
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `selection_rounds` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `job_id` INT NOT NULL,
    `round_number` INT NOT NULL,
    `round_name` VARCHAR(150) NOT NULL, -- e.g. 'Online Aptitude Test', 'Technical Round 1', 'HR Discussion'
    `round_type` ENUM('ONLINE_TEST', 'CODING_TEST', 'TECHNICAL_INTERVIEW', 'HR_INTERVIEW', 'GROUP_DISCUSSION') NOT NULL,
    `scheduled_time` DATETIME NOT NULL,
    `venue_or_link` VARCHAR(255),
    `instructions` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_round_job` FOREIGN KEY (`job_id`) 
        REFERENCES `job_postings` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uq_job_round_num` UNIQUE (`job_id`, `round_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 7. Table: application_round_history
-- Candidate progression status and feedback for each individual interview round
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `application_round_history` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `application_id` INT NOT NULL,
    `round_id` INT NOT NULL,
    `status` ENUM('SCHEDULED', 'ATTENDED', 'CLEARED', 'ELIMINATED', 'ABSENT') NOT NULL DEFAULT 'SCHEDULED',
    `interviewer_remarks` TEXT,
    `score` DECIMAL(5, 2),
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_history_app` FOREIGN KEY (`application_id`) 
        REFERENCES `job_applications` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_history_round` FOREIGN KEY (`round_id`) 
        REFERENCES `selection_rounds` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uq_app_round` UNIQUE (`application_id`, `round_id`),
    INDEX `idx_history_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- INITIAL SEED DATA (For Testing & Verification)
-- Default Passwords below are all hashed with BCrypt for "Admin@123", "Student@123", "Recruiter@123"
-- BCrypt Hash for "Admin@123": $2a$10$7vFf9g4u1F2X9XQf1b0yauA7r1zS7v8B4uL8Y4L5v6j6k4n9m3qWa
-- ============================================================================

-- 1. Default Placement Officer (TPO Admin)
-- Email: admin@smarthire.edu | Password: Password@123 (hash: $2a$10$4Bqk2lWhXXrjgK45JzvtOe9UvkvJxNX9JySvkDfI0Mi/YDTPa/4Di)
INSERT INTO `users` (`id`, `email`, `password_hash`, `role`, `status`) VALUES
(1, 'admin@smarthire.edu', '$2a$10$4Bqk2lWhXXrjgK45JzvtOe9UvkvJxNX9JySvkDfI0Mi/YDTPa/4Di', 'TPO_ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE `id`=`id`;

-- 2. Sample Student Account
-- Email: rahul.verma@smarthire.edu | Password: Password@123
INSERT INTO `users` (`id`, `email`, `password_hash`, `role`, `status`) VALUES
(2, 'rahul.verma@smarthire.edu', '$2a$10$4Bqk2lWhXXrjgK45JzvtOe9UvkvJxNX9JySvkDfI0Mi/YDTPa/4Di', 'STUDENT', 'ACTIVE')
ON DUPLICATE KEY UPDATE `id`=`id`;

INSERT INTO `student_profiles` (
    `id`, `user_id`, `roll_number`, `first_name`, `last_name`, `phone`, `gender`, 
    `department`, `cgpa`, `tenth_percentage`, `twelfth_percentage`, `active_backlogs`, 
    `total_backlogs_history`, `graduation_year`, `skills`, `is_verified`
) VALUES (
    1, 2, 'CS2026001', 'Rahul', 'Verma', '9876543210', 'MALE', 
    'CSE', 8.65, 91.50, 88.20, 0, 
    0, 2026, 'Java, Python, MySQL, Data Structures, HTML/CSS', TRUE
) ON DUPLICATE KEY UPDATE `id`=`id`;

-- 3. Sample Recruiter Account & Company
-- Email: recruiter@techcorp.com | Password: Password@123
INSERT INTO `users` (`id`, `email`, `password_hash`, `role`, `status`) VALUES
(3, 'recruiter@techcorp.com', '$2a$10$4Bqk2lWhXXrjgK45JzvtOe9UvkvJxNX9JySvkDfI0Mi/YDTPa/4Di', 'RECRUITER', 'ACTIVE')
ON DUPLICATE KEY UPDATE `id`=`id`;

INSERT INTO `companies` (
    `id`, `user_id`, `company_name`, `industry`, `website`, 
    `description`, `headquarters`, `contact_person_name`, `contact_phone`, `is_verified`
) VALUES (
    1, 3, 'TechCorp Solutions', 'Information Technology & Cloud', 'https://techcorp.example.com',
    'Global software solutions and enterprise cloud consulting firm.', 'Bangalore, India',
    'Sarah Jenkins', '9123456780', TRUE
) ON DUPLICATE KEY UPDATE `id`=`id`;

-- 4. Sample Job Posting (Placement Drive)
INSERT INTO `job_postings` (
    `id`, `company_id`, `job_title`, `job_description`, `job_location`, `employment_type`, 
    `package_lpa`, `min_cgpa`, `min_tenth_percentage`, `min_twelfth_percentage`, 
    `max_backlogs_allowed`, `eligible_branches`, `graduation_year`, 
    `application_deadline`, `drive_date`, `status`
) VALUES (
    1, 1, 'Associate Software Engineer', 
    'Looking for talented 2026 graduates proficient in Java, SQL, and problem-solving to join our core backend engineering team.',
    'Bangalore / Hybrid', 'FULL_TIME',
    9.50, 7.50, 70.00, 70.00,
    0, 'CSE,IT,ECE', 2026,
    DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 14 DAY),
    DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 21 DAY),
    'OPEN'
) ON DUPLICATE KEY UPDATE `id`=`id`;

-- 5. Sample Interview Rounds for Drive 1
INSERT INTO `selection_rounds` (`id`, `job_id`, `round_number`, `round_name`, `round_type`, `scheduled_time`, `venue_or_link`) VALUES
(1, 1, 1, 'Online Technical Aptitude & Coding', 'CODING_TEST', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 21 DAY), 'HackerRank Platform'),
(2, 1, 2, 'Technical Interview (Core Java & DS)', 'TECHNICAL_INTERVIEW', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 23 DAY), 'Google Meet'),
(3, 1, 3, 'Managerial & HR Discussion', 'HR_INTERVIEW', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 25 DAY), 'Campus Auditorium / Virtual')
ON DUPLICATE KEY UPDATE `id`=`id`;
