package com.smarthire.controller;

import com.smarthire.model.StudentProfile;
import com.smarthire.model.User;
import com.smarthire.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * StudentDashboardServlet
 *
 * Gateway controller for the Student workspace. Loads real-time academic profile,
 * verification status, applied job counts, and live active drives.
 */
@WebServlet(name = "StudentDashboardServlet", urlPatterns = {"/student/dashboard"})
public class StudentDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private StudentService studentService;
    private JobService jobService;
    private ApplicationService applicationService;

    @Override
    public void init() throws ServletException {
        this.studentService = new StudentServiceImpl();
        this.jobService = new JobServiceImpl();
        this.applicationService = new ApplicationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser != null) {
            StudentProfile profile = studentService.getProfileByUserId(currentUser.getId());
            int appliedCount = applicationService.countStudentApplications(profile.getId());
            int activeDrives = jobService.countActiveJobs();

            request.setAttribute("profile", profile);
            request.setAttribute("appliedCount", appliedCount);
            request.setAttribute("activeDrives", activeDrives);
        }

        request.setAttribute("user", currentUser);
        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(request, response);
    }
}
