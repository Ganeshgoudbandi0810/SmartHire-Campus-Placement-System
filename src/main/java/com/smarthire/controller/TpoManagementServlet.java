package com.smarthire.controller;

import com.smarthire.model.Company;
import com.smarthire.model.JobPosting;
import com.smarthire.model.StudentProfile;
import com.smarthire.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * TpoManagementServlet
 *
 * Administrative Controller for Placement Officers (TPO) managing drives,
 * auditing partner companies, and approving student academic records.
 */
@WebServlet(name = "TpoManagementServlet", urlPatterns = {
        "/tpo/drives",
        "/tpo/companies",
        "/tpo/students",
        "/tpo/students/verify",
        "/tpo/companies/verify"
})
public class TpoManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(TpoManagementServlet.class);

    private JobService jobService;
    private CompanyService companyService;
    private StudentService studentService;

    @Override
    public void init() throws ServletException {
        this.jobService = new JobServiceImpl();
        this.companyService = new CompanyServiceImpl();
        this.studentService = new StudentServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/tpo/drives".equals(path)) {
            List<JobPosting> drives = jobService.getAllJobs();
            request.setAttribute("drives", drives);
            request.getRequestDispatcher("/WEB-INF/views/tpo/drives/list.jsp").forward(request, response);
            return;
        }

        if ("/tpo/companies".equals(path)) {
            List<Company> companies = companyService.getAllCompanies();
            request.setAttribute("companies", companies);
            if ("verified".equalsIgnoreCase(request.getParameter("success"))) {
                request.setAttribute("successMessage", "Company verification status updated!");
            }
            request.getRequestDispatcher("/WEB-INF/views/tpo/companies/list.jsp").forward(request, response);
            return;
        }

        if ("/tpo/students".equals(path)) {
            List<StudentProfile> students = studentService.getAllStudents();
            request.setAttribute("students", students);
            if ("verified".equalsIgnoreCase(request.getParameter("success"))) {
                request.setAttribute("successMessage", "Student verification status updated!");
            }
            request.getRequestDispatcher("/WEB-INF/views/tpo/students/list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/tpo/students/verify".equals(path)) {
            try {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                boolean isVerified = Boolean.parseBoolean(request.getParameter("isVerified"));
                studentService.verifyStudent(studentId, isVerified);
                response.sendRedirect(request.getContextPath() + "/tpo/students?success=verified");
            } catch (Exception e) {
                logger.error("Error updating student verification: {}", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/tpo/students?error=" + e.getMessage());
            }
            return;
        }

        if ("/tpo/companies/verify".equals(path)) {
            try {
                int companyId = Integer.parseInt(request.getParameter("companyId"));
                boolean isVerified = Boolean.parseBoolean(request.getParameter("isVerified"));
                companyService.verifyCompany(companyId, isVerified);
                response.sendRedirect(request.getContextPath() + "/tpo/companies?success=verified");
            } catch (Exception e) {
                logger.error("Error updating company verification: {}", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/tpo/companies?error=" + e.getMessage());
            }
        }
    }
}
