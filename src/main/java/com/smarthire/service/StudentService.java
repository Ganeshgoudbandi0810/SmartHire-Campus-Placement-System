package com.smarthire.service;

import com.smarthire.exception.ValidationException;
import com.smarthire.model.StudentProfile;
import jakarta.servlet.http.Part;

import java.util.List;

/**
 * StudentService
 *
 * Business service interface governing candidate academic profiles, validations,
 * and resume document processing.
 */
public interface StudentService {

    /**
     * Retrieves the profile for the given user ID. If the profile record doesn't
     * exist yet, initializes and returns a default profile.
     */
    StudentProfile getProfileByUserId(int userId);

    /**
     * Retrieves a student profile by its primary key ID.
     */
    StudentProfile getProfileById(int id);

    /**
     * Validates and updates a student's personal and academic records.
     *
     * @throws ValidationException if academic bounds (CGPA, marks, year) are invalid
     */
    void updateProfile(StudentProfile profile) throws ValidationException;

    /**
     * Validates and saves an uploaded PDF resume document.
     *
     * @param userId         User ID of the student
     * @param filePart       Jakarta Servlet Part containing the uploaded file
     * @param uploadRootPath Root directory path on disk for storage
     * @return Saved relative file path
     * @throws ValidationException if file is not a PDF, exceeds size limits, or fails storage
     */
    String processResumeUpload(int userId, Part filePart, String uploadRootPath) throws ValidationException;

    /**
     * Retrieves all student profiles for administrative review.
     */
    List<StudentProfile> getAllStudents();

    /**
     * TPO verification action to approve/reject a student's academic claims.
     */
    void verifyStudent(int studentId, boolean isVerified);
}
