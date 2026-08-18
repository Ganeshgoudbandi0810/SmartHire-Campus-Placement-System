package com.smarthire.service;

import com.smarthire.dao.StudentDAO;
import com.smarthire.dao.StudentDAOImpl;
import com.smarthire.exception.ValidationException;
import com.smarthire.model.StudentProfile;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * StudentServiceImpl
 *
 * Implements business logic for student profile management, input validation,
 * and secure PDF resume file uploads.
 */
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private static final long MAX_RESUME_SIZE = 5 * 1024 * 1024; // 5 MB

    private final StudentDAO studentDAO;

    public StudentServiceImpl() {
        this.studentDAO = new StudentDAOImpl();
    }

    public StudentServiceImpl(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    @Override
    public StudentProfile getProfileByUserId(int userId) {
        Optional<StudentProfile> profileOpt = studentDAO.findByUserId(userId);
        if (profileOpt.isPresent()) {
            return profileOpt.get();
        }

        // Initialize blank profile for new student
        logger.info("Initializing new profile record for user ID: {}", userId);
        StudentProfile newProfile = new StudentProfile();
        newProfile.setUserId(userId);
        newProfile.setRollNumber("STU" + userId);
        newProfile.setFirstName("");
        newProfile.setLastName("");
        newProfile.setDepartment("CSE");
        newProfile.setCgpa(BigDecimal.ZERO);
        newProfile.setTenthPercentage(BigDecimal.ZERO);
        newProfile.setTwelfthPercentage(BigDecimal.ZERO);
        newProfile.setActiveBacklogs(0);
        newProfile.setTotalBacklogsHistory(0);
        newProfile.setGraduationYear(2026);
        newProfile.setVerified(false);

        return studentDAO.save(newProfile);
    }

    @Override
    public StudentProfile getProfileById(int id) {
        return studentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Student profile not found with ID: " + id));
    }

    @Override
    public void updateProfile(StudentProfile profile) throws ValidationException {
        if (profile == null) {
            throw new ValidationException("Profile payload cannot be null.");
        }

        // 1. Validate CGPA (0.00 to 10.00)
        if (profile.getCgpa() == null || 
            profile.getCgpa().compareTo(BigDecimal.ZERO) < 0 || 
            profile.getCgpa().compareTo(new BigDecimal("10.00")) > 0) {
            throw new ValidationException("CGPA must be between 0.00 and 10.00.");
        }

        // 2. Validate 10th and 12th Percentages (0.00 to 100.00)
        if (profile.getTenthPercentage() == null || 
            profile.getTenthPercentage().compareTo(BigDecimal.ZERO) < 0 || 
            profile.getTenthPercentage().compareTo(new BigDecimal("100.00")) > 0) {
            throw new ValidationException("10th Standard Percentage must be between 0.00 and 100.00.");
        }

        if (profile.getTwelfthPercentage() == null || 
            profile.getTwelfthPercentage().compareTo(BigDecimal.ZERO) < 0 || 
            profile.getTwelfthPercentage().compareTo(new BigDecimal("100.00")) > 0) {
            throw new ValidationException("12th Standard / Diploma Percentage must be between 0.00 and 100.00.");
        }

        // 3. Validate Backlogs
        if (profile.getActiveBacklogs() < 0) {
            throw new ValidationException("Active backlogs count cannot be negative.");
        }

        if (profile.getTotalBacklogsHistory() < profile.getActiveBacklogs()) {
            profile.setTotalBacklogsHistory(profile.getActiveBacklogs());
        }

        // 4. Validate Graduation Year
        if (profile.getGraduationYear() < 2000 || profile.getGraduationYear() > 2050) {
            throw new ValidationException("Please provide a realistic graduation year (e.g. 2026).");
        }

        // 5. Validate Name & Roll Number
        if (profile.getRollNumber() == null || profile.getRollNumber().trim().isEmpty()) {
            throw new ValidationException("Institutional Roll Number is mandatory.");
        }

        // 6. Check unique roll number constraint against other users
        Optional<StudentProfile> existingWithRoll = studentDAO.findByRollNumber(profile.getRollNumber().trim());
        if (existingWithRoll.isPresent() && existingWithRoll.get().getId() != profile.getId()) {
            throw new ValidationException("Roll number '" + profile.getRollNumber() + "' is already registered to another student.");
        }

        boolean updated = studentDAO.update(profile);
        if (!updated) {
            throw new RuntimeException("Failed to update student profile in database.");
        }
        logger.info("Successfully updated academic profile for student ID: {}", profile.getId());
    }

    @Override
    public String processResumeUpload(int userId, Part filePart, String uploadRootPath) throws ValidationException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        // 1. File Size Verification
        if (filePart.getSize() > MAX_RESUME_SIZE) {
            throw new ValidationException("Resume file size exceeds the 5 MB limit.");
        }

        // 2. MIME & Extension Verification
        String submittedFilename = filePart.getSubmittedFileName();
        if (submittedFilename == null || !submittedFilename.toLowerCase().endsWith(".pdf")) {
            throw new ValidationException("Only PDF format (.pdf) is supported for resumes.");
        }

        String contentType = filePart.getContentType();
        if (contentType != null && !contentType.equalsIgnoreCase("application/pdf") && !contentType.contains("pdf")) {
            throw new ValidationException("Invalid file type. Please upload a genuine PDF document.");
        }

        // 3. Generate Sanitized Unique Filename
        String safeFilename = "resume_user_" + userId + "_" + System.currentTimeMillis() + ".pdf";

        // 4. Resolve Target Directory: <uploadRootPath>/uploads/resumes/
        Path uploadDir = Paths.get(uploadRootPath, "uploads", "resumes");
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                logger.info("Created upload directory: {}", uploadDir.toAbsolutePath());
            }

            Path destinationPath = uploadDir.resolve(safeFilename);
            filePart.write(destinationPath.toString());
            logger.info("Saved resume for user {} at: {}", userId, destinationPath.toAbsolutePath());

            // 5. Update Database Record
            StudentProfile profile = getProfileByUserId(userId);
            String relativePath = "uploads/resumes/" + safeFilename;
            studentDAO.updateResumePath(profile.getId(), relativePath);

            return relativePath;

        } catch (IOException e) {
            logger.error("Failed to write resume file to disk: {}", e.getMessage(), e);
            throw new ValidationException("Unable to save resume file on server: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentProfile> getAllStudents() {
        return studentDAO.findAll();
    }

    @Override
    public void verifyStudent(int studentId, boolean isVerified) {
        studentDAO.updateVerificationStatus(studentId, isVerified);
        logger.info("TPO updated verification status for student ID {}: {}", studentId, isVerified);
    }
}
