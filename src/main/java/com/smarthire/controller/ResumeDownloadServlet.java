package com.smarthire.controller;

import com.smarthire.model.Role;
import com.smarthire.model.StudentProfile;
import com.smarthire.model.User;
import com.smarthire.service.StudentService;
import com.smarthire.service.StudentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ResumeDownloadServlet
 *
 * Securely streams uploaded candidate PDF resumes for in-browser preview or download.
 * Ensures that students can only view their own resume, while TPO and Recruiters can view all.
 */
@WebServlet(name = "ResumeDownloadServlet", urlPatterns = {"/student/resume/download", "/resume/download"})
public class ResumeDownloadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ResumeDownloadServlet.class);
    private StudentService studentService;

    @Override
    public void init() throws ServletException {
        this.studentService = new StudentServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please sign in to access documents.");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        StudentProfile targetProfile = null;

        String studentIdParam = request.getParameter("studentId");
        if (studentIdParam != null && !studentIdParam.trim().isEmpty()) {
            try {
                int studentId = Integer.parseInt(studentIdParam.trim());
                targetProfile = studentService.getProfileById(studentId);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student profile not found.");
                return;
            }
        } else {
            // Default to current user's profile
            targetProfile = studentService.getProfileByUserId(currentUser.getId());
        }

        // Authorization check: Student can only view their own resume
        if (currentUser.getRole() == Role.STUDENT && targetProfile.getUserId() != currentUser.getId()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You cannot access other candidates' resumes.");
            return;
        }

        String resumeRelativePath = targetProfile.getResumeFilePath();
        if (resumeRelativePath == null || resumeRelativePath.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No resume has been uploaded for this candidate.");
            return;
        }

        // Resolve absolute file path on disk
        String uploadRoot = getServletContext().getRealPath("/");
        if (uploadRoot == null) {
            uploadRoot = System.getProperty("user.dir");
        }

        Path filePath = Paths.get(uploadRoot, resumeRelativePath);
        if (!Files.exists(filePath)) {
            logger.warn("Resume file not found on disk at: {}", filePath.toAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resume file could not be located on the server.");
            return;
        }

        File resumeFile = filePath.toFile();
        response.setContentType("application/pdf");
        response.setContentLengthLong(resumeFile.length());

        String filename = targetProfile.getRollNumber() + "_Resume.pdf";
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

        try (InputStream in = new FileInputStream(resumeFile);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }
}
