package com.smarthire.controller;

import com.smarthire.exception.ValidationException;
import com.smarthire.model.StudentProfile;
import com.smarthire.model.User;
import com.smarthire.service.StudentService;
import com.smarthire.service.StudentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * StudentProfileServlet
 *
 * Handles viewing, editing, and saving student personal and academic records,
 * including PDF resume document uploads via standard Jakarta Multipart.
 */
@WebServlet(name = "StudentProfileServlet", urlPatterns = {"/student/profile"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB in-memory buffer
        maxFileSize = 5 * 1024 * 1024,         // 5 MB max file size
        maxRequestSize = 6 * 1024 * 1024       // 6 MB max total multipart request size
)
public class StudentProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(StudentProfileServlet.class);
    private StudentService studentService;

    @Override
    public void init() throws ServletException {
        this.studentService = new StudentServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        StudentProfile profile = studentService.getProfileByUserId(currentUser.getId());
        request.setAttribute("profile", profile);

        if ("saved".equalsIgnoreCase(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Your academic profile and documents were saved successfully!");
        }

        request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        StudentProfile profile = studentService.getProfileByUserId(currentUser.getId());

        try {
            // Extract textual form parameters
            profile.setRollNumber(request.getParameter("rollNumber"));
            profile.setFirstName(request.getParameter("firstName"));
            profile.setLastName(request.getParameter("lastName"));
            profile.setPhone(request.getParameter("phone"));
            profile.setGender(request.getParameter("gender"));
            profile.setDepartment(request.getParameter("department"));
            profile.setSkills(request.getParameter("skills"));

            // Parse numerical parameters safely
            String cgpaStr = request.getParameter("cgpa");
            profile.setCgpa(cgpaStr != null && !cgpaStr.trim().isEmpty() ? new BigDecimal(cgpaStr.trim()) : BigDecimal.ZERO);

            String tenthStr = request.getParameter("tenthPercentage");
            profile.setTenthPercentage(tenthStr != null && !tenthStr.trim().isEmpty() ? new BigDecimal(tenthStr.trim()) : BigDecimal.ZERO);

            String twelfthStr = request.getParameter("twelfthPercentage");
            profile.setTwelfthPercentage(twelfthStr != null && !twelfthStr.trim().isEmpty() ? new BigDecimal(twelfthStr.trim()) : BigDecimal.ZERO);

            String activeBacklogsStr = request.getParameter("activeBacklogs");
            profile.setActiveBacklogs(activeBacklogsStr != null && !activeBacklogsStr.trim().isEmpty() ? Integer.parseInt(activeBacklogsStr.trim()) : 0);

            String totalBacklogsStr = request.getParameter("totalBacklogsHistory");
            profile.setTotalBacklogsHistory(totalBacklogsStr != null && !totalBacklogsStr.trim().isEmpty() ? Integer.parseInt(totalBacklogsStr.trim()) : profile.getActiveBacklogs());

            String gradYearStr = request.getParameter("graduationYear");
            profile.setGraduationYear(gradYearStr != null && !gradYearStr.trim().isEmpty() ? Integer.parseInt(gradYearStr.trim()) : 2026);

            // Update academic data
            studentService.updateProfile(profile);

            // Handle optional PDF resume file upload part
            Part resumePart = request.getPart("resumeFile");
            if (resumePart != null && resumePart.getSize() > 0) {
                // Use servlet context real path or standard storage root
                String uploadRoot = getServletContext().getRealPath("/");
                if (uploadRoot == null) {
                    uploadRoot = System.getProperty("user.dir");
                }
                studentService.processResumeUpload(currentUser.getId(), resumePart, uploadRoot);
            }

            response.sendRedirect(request.getContextPath() + "/student/profile?success=saved");

        } catch (ValidationException | NumberFormatException e) {
            logger.warn("Validation error while saving profile for user ID {}: {}", currentUser.getId(), e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("profile", profile);
            request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Unexpected error saving profile for user ID {}: {}", currentUser.getId(), e.getMessage(), e);
            request.setAttribute("errorMessage", "An error occurred while saving your profile: " + e.getMessage());
            request.setAttribute("profile", profile);
            request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(request, response);
        }
    }
}
