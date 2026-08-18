package com.smarthire.dao;

import com.smarthire.model.StudentProfile;

import java.util.List;
import java.util.Optional;

/**
 * StudentDAO
 *
 * Data Access Object interface for managing student academic and demographic profiles.
 */
public interface StudentDAO {

    /**
     * Persists a new student profile and sets its generated primary key ID.
     */
    StudentProfile save(StudentProfile profile);

    /**
     * Updates an existing student profile.
     */
    boolean update(StudentProfile profile);

    /**
     * Retrieves a student profile by the associated user ID.
     */
    Optional<StudentProfile> findByUserId(int userId);

    /**
     * Retrieves a student profile by its primary key ID.
     */
    Optional<StudentProfile> findById(int id);

    /**
     * Retrieves a student profile by unique institutional roll number.
     */
    Optional<StudentProfile> findByRollNumber(String rollNumber);

    /**
     * Updates the PDF resume file path for a student.
     */
    boolean updateResumePath(int studentId, String resumePath);

    /**
     * Updates profile verification flag by TPO placement officers.
     */
    boolean updateVerificationStatus(int studentId, boolean isVerified);

    /**
     * Retrieves all student profiles with associated user email.
     */
    List<StudentProfile> findAll();

    /**
     * Retrieves students belonging to a specific department.
     */
    List<StudentProfile> findByDepartment(String department);
}
