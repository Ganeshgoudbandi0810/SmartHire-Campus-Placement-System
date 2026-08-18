package com.smarthire.controller;

import com.smarthire.model.JobApplication;
import com.smarthire.model.StudentProfile;
import com.smarthire.model.User;
import com.smarthire.service.ApplicationService;
import com.smarthire.service.ApplicationServiceImpl;
import com.smarthire.service.StudentService;
import com.smarthire.service.StudentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * StudentApplicationsServlet
 *
 * Controller displaying submitted application history and live selection status
 * for the logged-in candidate.
 */
@WebServlet(name = "StudentApplicationsServlet", urlPatterns = {"/student/applications"})
public class StudentApplicationsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private StudentService studentService;
    private ApplicationService applicationService;

    @Override
    public void init() throws ServletException {
        this.studentService = new StudentServiceImpl();
        this.applicationService = new ApplicationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        StudentProfile student = studentService.getProfileByUserId(currentUser.getId());
        List<JobApplication> applications = applicationService.getStudentApplications(student.getId());

        request.setAttribute("student", student);
        request.setAttribute("applications", applications);

        if ("applied".equalsIgnoreCase(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Application submitted successfully! Track your selection progress below.");
        }

        request.getRequestDispatcher("/WEB-INF/views/student/applications/list.jsp").forward(request, response);
    }
}
