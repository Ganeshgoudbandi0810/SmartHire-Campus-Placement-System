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
import java.util.List;

/**
 * TpoDashboardServlet
 *
 * Gateway controller for authenticated Placement Officer (TPO) admin portal.
 * Loads dynamic counts of registered students, partner companies, and live drives.
 */
@WebServlet(name = "TpoDashboardServlet", urlPatterns = {"/tpo/dashboard"})
public class TpoDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private StudentService studentService;
    private CompanyService companyService;
    private JobService jobService;

    @Override
    public void init() throws ServletException {
        this.studentService = new StudentServiceImpl();
        this.companyService = new CompanyServiceImpl();
        this.jobService = new JobServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        List<StudentProfile> students = studentService.getAllStudents();
        long verifiedStudentsCount = students.stream().filter(StudentProfile::isVerified).count();
        int totalCompanies = companyService.countTotalCompanies();
        int activeDrives = jobService.countActiveJobs();

        request.setAttribute("user", currentUser);
        request.setAttribute("totalStudents", students.size());
        request.setAttribute("verifiedStudentsCount", verifiedStudentsCount);
        request.setAttribute("totalCompanies", totalCompanies);
        request.setAttribute("activeDrives", activeDrives);

        request.getRequestDispatcher("/WEB-INF/views/tpo/dashboard.jsp").forward(request, response);
    }
}
