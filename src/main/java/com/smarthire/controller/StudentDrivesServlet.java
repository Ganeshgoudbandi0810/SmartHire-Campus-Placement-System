package com.smarthire.controller;

import com.smarthire.exception.ValidationException;
import com.smarthire.model.*;
import com.smarthire.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDrivesServlet
 *
 * Controller enabling candidates to browse open placement drives with real-time
 * eligibility badges and submit 1-click applications.
 */
@WebServlet(name = "StudentDrivesServlet", urlPatterns = {
        "/student/drives",
        "/student/drives/apply"
})
public class StudentDrivesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(StudentDrivesServlet.class);

    private JobService jobService;
    private StudentService studentService;
    private ApplicationService applicationService;

    @Override
    public void init() throws ServletException {
        this.jobService = new JobServiceImpl();
        this.studentService = new StudentServiceImpl();
        this.applicationService = new ApplicationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        StudentProfile student = studentService.getProfileByUserId(currentUser.getId());
        List<JobPosting> openJobs = jobService.getOpenJobs();

        List<JobDriveViewDTO> driveDTOs = new ArrayList<>();
        for (JobPosting job : openJobs) {
            EligibilityResult eligibility = applicationService.evaluateEligibility(student, job);
            boolean applied = applicationService.hasStudentApplied(job.getId(), student.getId());
            driveDTOs.add(new JobDriveViewDTO(job, eligibility, applied));
        }

        request.setAttribute("student", student);
        request.setAttribute("driveDTOs", driveDTOs);

        String errorParam = request.getParameter("error");
        if (errorParam != null && !errorParam.isEmpty()) {
            request.setAttribute("errorMessage", errorParam);
        }

        request.getRequestDispatcher("/WEB-INF/views/student/drives/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        StudentProfile student = studentService.getProfileByUserId(currentUser.getId());

        try {
            int jobId = Integer.parseInt(request.getParameter("jobId"));
            applicationService.applyForJob(student.getId(), jobId);

            // Redirect to applications tracking view
            response.sendRedirect(request.getContextPath() + "/student/applications?success=applied");

        } catch (ValidationException e) {
            logger.warn("Application validation failed for student {}: {}", student.getId(), e.getMessage());
            String encoded = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/student/drives?error=" + encoded);
        } catch (Exception e) {
            logger.error("Unexpected error applying for drive: {}", e.getMessage(), e);
            String encoded = URLEncoder.encode("An error occurred: " + e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/student/drives?error=" + encoded);
        }
    }
}
